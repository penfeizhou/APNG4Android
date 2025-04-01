package com.github.penfeizhou.animation.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.penfeizhou.animation.executor.FrameDecoderExecutor;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.Writer;
import com.github.penfeizhou.animation.loader.Loader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

/**
 * @Description: Abstract Frame Animation Decoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameSeqDecoder<R extends Reader, W extends Writer> {
    private static final String TAG = FrameSeqDecoder.class.getSimpleName();
    private final int taskId;

    private final Loader mLoader;
    private final Handler workerHandler;
    protected List<Frame<R, W>> frames = new ArrayList<>();
    protected int frameIndex = -1;
    protected static final double MB = 1024.0 * 1024.0;
    private int playCount;
    private Integer loopLimit = null;
    private final Set<RenderListener> renderListeners = new HashSet<>();
    private final AtomicBoolean paused = new AtomicBoolean(true);
    private static final Rect RECT_EMPTY = new Rect();
    private final Runnable renderTask = new Runnable() {
        @Override
        public void run() {
            if (DEBUG) {
                Log.d(TAG, renderTask + ",run");
            }
            if (paused.get()) {
                return;
            }
            if (canStep()) {
                long start = System.currentTimeMillis();
                long delay = step();
                long cost = System.currentTimeMillis() - start;
                workerHandler.removeCallbacks(renderTask);
                workerHandler.postDelayed(this, Math.max(0, delay - cost));
                for (RenderListener renderListener : renderListeners) {
                    if (frameBuffer != null) {
                        renderListener.onRender(frameBuffer);
                    }
                }
            } else {
                stop();
            }
        }
    };
    protected int sampleSize = 1;

    private final Set<Bitmap> cacheBitmaps = new HashSet<>();
    private final Object cacheBitmapsLock = new Object();

    protected Map<Bitmap, Canvas> cachedCanvas = new WeakHashMap<>();
    protected ByteBuffer frameBuffer;
    protected volatile Rect fullRect;
    private W mWriter = getWriter();
    private R mReader = null;
    public static final boolean DEBUG = false;
    /**
     * If played all the needed
     */
    private boolean finished = false;

    private enum State {
        IDLE,
        RUNNING,
        INITIALIZING,
        FINISHING,
    }

    private volatile State mState = State.IDLE;

    protected abstract W getWriter();

    protected abstract R getReader(Reader reader);

    protected Bitmap obtainBitmap(int width, int height) {
        synchronized (cacheBitmapsLock) {
            Bitmap ret = null;
            Iterator<Bitmap> iterator = cacheBitmaps.iterator();
            while (iterator.hasNext()) {
                int reuseSize = width * height * 4;
                ret = iterator.next();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (ret != null && ret.getAllocationByteCount() >= reuseSize) {
                        iterator.remove();
                        if ((ret.getWidth() != width || ret.getHeight() != height)) {
                            if (width > 0 && height > 0) {
                                    ret.reconfigure(width, height, Bitmap.Config.ARGB_8888);
                            }
                        }
                        ret.eraseColor(0);
                        return ret;
                    }
                } else {
                    if (ret != null && ret.getByteCount() >= reuseSize) {
                        if (ret.getWidth() == width && ret.getHeight() == height) {
                            iterator.remove();
                            ret.eraseColor(0);
                        }
                        return ret;
                    }
                }
            }

            if (width <= 0 || height <= 0) {
                return null;
            }
            try {
                Bitmap.Config config = Bitmap.Config.ARGB_8888;
                ret = Bitmap.createBitmap(width, height, config);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return ret;
        }
    }

    protected void recycleBitmap(Bitmap bitmap) {
        synchronized (cacheBitmapsLock) {
            if (bitmap != null) {
                cacheBitmaps.add(bitmap);
            }
        }
    }

    /**
     * 解码器的渲染回调
     */
    public interface RenderListener {
        /**
         * 播放开始
         */
        void onStart();

        /**
         * 帧播放
         */
        void onRender(ByteBuffer byteBuffer);

        /**
         * 播放结束
         */
        void onEnd();
    }


    /**
     * @param loader         webp的reader
     * @param renderListener 渲染的回调
     */
    public FrameSeqDecoder(Loader loader, @Nullable RenderListener renderListener) {
        this.mLoader = loader;
        if (renderListener != null) {
            this.renderListeners.add(renderListener);
        }
        this.taskId = FrameDecoderExecutor.getInstance().generateTaskId();
        this.workerHandler = new Handler(FrameDecoderExecutor.getInstance().getLooper(taskId));
    }


    public void addRenderListener(final RenderListener renderListener) {
        this.workerHandler.post(new Runnable() {
            @Override
            public void run() {
                renderListeners.add(renderListener);
            }
        });
    }

    public void removeRenderListener(final RenderListener renderListener) {
        this.workerHandler.post(new Runnable() {
            @Override
            public void run() {
                renderListeners.remove(renderListener);
            }
        });
    }

    public void stopIfNeeded() {
        this.workerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (renderListeners.size() == 0) {
                    stop();
                }
            }
        });
    }

    public Rect getBounds() {
        if (fullRect == null) {
            if (mState == State.FINISHING) {
                Log.e(TAG, "In finishing,do not interrupt");
            }
            final Thread thread = Thread.currentThread();
            workerHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (fullRect == null) {
                            if (mReader == null) {
                                mReader = getReader(mLoader.obtain());
                            } else {
                                mReader.reset();
                            }
                            initCanvasBounds(read(mReader));
                        }
                    } catch (Exception | OutOfMemoryError e) {
                        e.printStackTrace();
                        fullRect = RECT_EMPTY;
                    } finally {
                        LockSupport.unpark(thread);
                    }
                }
            });
            LockSupport.park(thread);
        }
        return fullRect == null ? RECT_EMPTY : fullRect;
    }

    private void initCanvasBounds(Rect rect) {
        fullRect = rect;
        long bufferSize = ((long) rect.width() * rect.height() / ((long) sampleSize * sampleSize) + 1) * 4;

        try {                
            frameBuffer = ByteBuffer.allocate((int)bufferSize);
            if (mWriter == null) {
                mWriter = getWriter();
            }
        } catch (OutOfMemoryError error) {
            Log.e(TAG, String.format(
                    "OutOfMemoryError in FrameSeqDecoder: Buffer needed: %.2fMB (%,d bytes)",
                    bufferSize / MB, bufferSize
                )
            );
            frameBuffer = null;
            fullRect = RECT_EMPTY;
            throw error;
        }
    }


    public int getFrameCount() {
        return this.frames.size();
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    /**
     * @return Loop Count defined in file
     */
    protected abstract int getLoopCount();

    public void start() {
        if (fullRect == RECT_EMPTY) {
            return;
        }
        if (mState == State.RUNNING || mState == State.INITIALIZING) {
            Log.i(TAG, debugInfo() + " Already started");
            return;
        }
        if (mState == State.FINISHING) {
            Log.e(TAG, debugInfo() + " Processing,wait for finish at " + mState);
        }
        if (DEBUG) {
            Log.i(TAG, debugInfo() + "Set state to INITIALIZING");
        }
        mState = State.INITIALIZING;
        if (Looper.myLooper() == workerHandler.getLooper()) {
            innerStart();
        } else {
            workerHandler.post(new Runnable() {
                @Override
                public void run() {
                    innerStart();
                }
            });
        }
    }

    @WorkerThread
    private void innerStart() {
        paused.compareAndSet(true, false);

        final long start = System.currentTimeMillis();
        try {
            if (getFrameCount() == 0) {
                try {
                    if (mReader == null) {
                        mReader = getReader(mLoader.obtain());
                    } else {
                        mReader.reset();
                    }
                    initCanvasBounds(read(mReader));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } finally {
            Log.i(TAG, debugInfo() + " Set state to RUNNING,cost " + (System.currentTimeMillis() - start));
            mState = State.RUNNING;
        }
        if (getNumPlays() == 0 || !finished) {
            this.frameIndex = -1;
            workerHandler.removeCallbacks(renderTask);
            renderTask.run();
            for (RenderListener renderListener : renderListeners) {
                renderListener.onStart();
            }
        } else {
            Log.i(TAG, debugInfo() + " No need to started");
        }
    }

    @WorkerThread
    private void innerStop() {
        workerHandler.removeCallbacks(renderTask);
        frames.clear();
        synchronized (cacheBitmapsLock) {
            for (Bitmap bitmap : cacheBitmaps) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            cacheBitmaps.clear();
        }
        if (frameBuffer != null) {
            frameBuffer = null;
        }
        cachedCanvas.clear();
        try {
            if (mReader != null) {
                mReader.close();
                mReader = null;
            }
            if (mWriter != null) {
                mWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        release();
        if (DEBUG) {
            Log.i(TAG, debugInfo() + " release and Set state to IDLE");
        }
        mState = State.IDLE;
        for (RenderListener renderListener : renderListeners) {
            renderListener.onEnd();
        }
    }

    public void stop() {
        if (fullRect == RECT_EMPTY) {
            return;
        }
        if (mState == State.FINISHING || mState == State.IDLE) {
            Log.i(TAG, debugInfo() + "No need to stop");
            return;
        }
        if (mState == State.INITIALIZING) {
            Log.e(TAG, debugInfo() + "Processing,wait for finish at " + mState);
        }
        if (DEBUG) {
            Log.i(TAG, debugInfo() + " Set state to finishing");
        }
        mState = State.FINISHING;
        if (Looper.myLooper() == workerHandler.getLooper()) {
            innerStop();
        } else {
            workerHandler.post(new Runnable() {
                @Override
                public void run() {
                    innerStop();
                }
            });
        }
    }

    private String debugInfo() {
        if (DEBUG) {
            return String.format("thread is %s, decoder is %s,state is %s", Thread.currentThread(), FrameSeqDecoder.this, mState.toString());
        }
        return "";
    }

    protected abstract void release();

    public boolean isRunning() {
        return mState == State.RUNNING || mState == State.INITIALIZING;
    }

    public boolean isPaused() {
        return paused.get();
    }

    public void setLoopLimit(int limit) {
        this.loopLimit = limit;
    }

    public void reset() {
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                playCount = 0;
                frameIndex = -1;
                finished = false;
            }
        });
    }

    public void pause() {
        workerHandler.removeCallbacks(renderTask);
        paused.compareAndSet(false, true);
    }

    public void resume() {
        paused.compareAndSet(true, false);
        workerHandler.removeCallbacks(renderTask);
        workerHandler.post(renderTask);
    }


    public int getSampleSize() {
        return sampleSize;
    }

    public int setDesiredSize(int width, int height) {
        final int sample = getDesiredSample(width, height);
        if (sample != getSampleSize()) {
            final boolean tempRunning = isRunning();
            workerHandler.removeCallbacks(renderTask);
            workerHandler.post(new Runnable() {
                @Override
                public void run() {
                    innerStop();
                    try {
                        sampleSize = sample;
                        initCanvasBounds(read(getReader(mLoader.obtain())));
                        if (tempRunning) {
                            innerStart();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return sample;
    }

    protected int getDesiredSample(int desiredWidth, int desiredHeight) {
        if (desiredWidth == 0 || desiredHeight == 0) {
            return 1;
        }
        int radio = Math.min(getBounds().width() / desiredWidth, getBounds().height() / desiredHeight);
        int sample = 1;
        while ((sample * 2) <= radio) {
            sample *= 2;
        }
        return sample;
    }

    protected abstract Rect read(R reader) throws IOException;

    private int getNumPlays() {
        return this.loopLimit != null ? this.loopLimit : this.getLoopCount();
    }

    private boolean canStep() {
        if (!isRunning()) {
            return false;
        }
        if (getFrameCount() == 0) {
            return false;
        }
        if (getNumPlays() <= 0) {
            return true;
        }
        if (this.playCount < getNumPlays() - 1) {
            return true;
        } else if (this.playCount == getNumPlays() - 1 && this.frameIndex < this.getFrameCount() - 1) {
            return true;
        }
        finished = true;
        return false;
    }

    @WorkerThread
    private long step() {
        this.frameIndex++;
        if (this.frameIndex >= this.getFrameCount()) {
            this.frameIndex = 0;
            this.playCount++;
        }
        Frame<R, W> frame = getFrame(this.frameIndex);
        if (frame == null) {
            return 0;
        }
        renderFrame(frame);
        return frame.frameDuration;
    }

    protected abstract void renderFrame(Frame<R, W> frame);

    public Frame<R, W> getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            return null;
        }
        return frames.get(index);
    }

    /**
     * Get Indexed frame
     *
     * @param index <0 means reverse from last index
     */
    public Bitmap getFrameBitmap(int index) throws IOException {
        if (mState != State.IDLE) {
            Log.e(TAG, debugInfo() + ",stop first");
            return null;
        }
        mState = State.RUNNING;
        paused.compareAndSet(true, false);
        if (frames.size() == 0) {
            if (mReader == null) {
                mReader = getReader(mLoader.obtain());
            } else {
                mReader.reset();
            }
            initCanvasBounds(read(mReader));
        }
        if (index < 0) {
            index += this.frames.size();
        }
        if (index < 0) {
            index = 0;
        }
        frameIndex = -1;
        while (frameIndex < index) {
            if (canStep()) {
                step();
            } else {
                break;
            }
        }
        frameBuffer.rewind();
        Bitmap bitmap = Bitmap.createBitmap(getBounds().width() / getSampleSize(), getBounds().height() / getSampleSize(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(frameBuffer);
        innerStop();
        return bitmap;
    }

    public int getMemorySize() {
        synchronized (cacheBitmapsLock) {
            int size = 0;
            for (Bitmap bitmap : cacheBitmaps) {
                if (bitmap.isRecycled()) {
                    continue;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    size += bitmap.getAllocationByteCount();
                } else {
                    size += bitmap.getByteCount();
                }
            }
            if (frameBuffer != null) {
                size += frameBuffer.capacity();
            }
            return size;
        }
    }
}
