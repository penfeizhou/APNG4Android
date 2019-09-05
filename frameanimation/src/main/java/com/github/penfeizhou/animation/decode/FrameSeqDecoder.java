package com.github.penfeizhou.animation.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.github.penfeizhou.animation.executor.CoroutinePool;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.Writer;
import com.github.penfeizhou.animation.loader.Loader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * @Description: Abstract Frame Animation Decoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameSeqDecoder<R extends Reader, W extends Writer> {
    private static final String TAG = FrameSeqDecoder.class.getSimpleName();
    private int count;

    private final Loader mLoader;
    protected List<Frame> frames = new ArrayList<>();
    protected int frameIndex = -1;
    private int playCount;
    private Integer loopLimit = null;
    private final RenderListener renderListener;
    private AtomicBoolean paused = new AtomicBoolean(true);
    private static final Rect RECT_EMPTY = new Rect();
    private Runnable renderTask = new Runnable() {
        @Override
        public void run() {
            if (DEBUG) {
                Log.d(TAG, renderListener.toString() + ",run");
            }
            if (paused.get()) {
                return;
            }
            if (canStep()) {
                long start = System.currentTimeMillis();
                long delay = step();
                long cost = System.currentTimeMillis() - start;
                CoroutinePool.INSTANCE.runDelay(this, Math.max(0, delay - cost), count, new Continuation<Unit>() {
                    @NotNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NotNull Object o) {

                    }
                });
                renderListener.onRender(frameBuffer);
            } else {
                stop();
            }
        }
    };
    protected int sampleSize = 1;

    private Set<Bitmap> cacheBitmaps = new HashSet<>();
    protected Map<Bitmap, Canvas> cachedCanvas = new WeakHashMap<>();
    protected ByteBuffer frameBuffer;
    protected Rect fullRect;
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
        Bitmap ret = null;
        Iterator<Bitmap> iterator = cacheBitmaps.iterator();
        while (iterator.hasNext()) {
            int reuseSize = width * height * 4;
            ret = iterator.next();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (ret != null && ret.getAllocationByteCount() >= reuseSize) {
                    iterator.remove();
                    if (ret.getWidth() != width || ret.getHeight() != height) {
                        ret.reconfigure(width, height, Bitmap.Config.ARGB_8888);
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

        try {
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            ret = Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !cacheBitmaps.contains(bitmap)) {
            cacheBitmaps.add(bitmap);
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
    public FrameSeqDecoder(Loader loader, RenderListener renderListener) {
        this.mLoader = loader;
        this.renderListener = renderListener;
        count = CoroutinePool.INSTANCE.incrementAndGet();
    }

    public Rect getBounds() {
        if (fullRect != null) {
            return fullRect;
        } else {
            if (mState == State.FINISHING) {
                Log.e(TAG, "In finishing,do not interrupt");
            }
            Callable<Rect> callable = new Callable<Rect>() {
                @Override
                public Rect call() throws Exception {
                    if (fullRect == null) {
                        if (mReader == null) {
                            mReader = getReader(mLoader.obtain());
                        } else {
                            mReader.reset();
                        }
                        initCanvasBounds(read(mReader));
                    }
                    return fullRect;
                }
            };
            CoroutinePool.INSTANCE.run(callable, count, new Continuation<Rect>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    if (o instanceof Rect) {
                        fullRect = (Rect) o;
                    } else {
                        fullRect = RECT_EMPTY;
                    }
                }
            });
            return fullRect;
        }
    }

    private void initCanvasBounds(Rect rect) {
        fullRect = rect;
        frameBuffer = ByteBuffer.allocate((rect.width() * rect.height() / (sampleSize * sampleSize) + 1) * 4);
        if (mWriter == null) {
            mWriter = getWriter();
        }
    }

    public synchronized void start() {
        if (fullRect == RECT_EMPTY) {
            return;
        }

        if (mState == State.RUNNING || mState == State.INITIALIZING) {
            Log.i(TAG, debugInfo() + " Already started");
            return;
        }
        while (mState == State.FINISHING) {
            Log.e(TAG, debugInfo() + " Processing,wait for finish at " + mState);
        }
        if (DEBUG) {
            Log.i(TAG, debugInfo() + "Set state to INITIALIZING");
        }
        mState = State.INITIALIZING;
        paused.compareAndSet(true, false);

        final long start = System.currentTimeMillis();
        CoroutinePool.INSTANCE.run(new Runnable() {
            @Override
            public void run() {
                try {
                    if (frames.size() == 0) {
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
            }
        }, count, new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {

            }
        });
        if (getNumPlays() == 0 || !finished) {
            this.frameIndex = -1;
            CoroutinePool.INSTANCE.run(renderTask, count, new Continuation<Unit>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {

                }
            });
            renderListener.onStart();
        } else {
            Log.i(TAG, debugInfo() + " No need to started");
        }
    }

    private int getFrameCount() {
        return this.frames.size();
    }

    /**
     * @return Loop Count defined in file
     */
    protected abstract int getLoopCount();

    public synchronized void stop() {
        if (fullRect == RECT_EMPTY) {
            return;
        }
        if (mState == State.FINISHING || mState == State.IDLE) {
            Log.i(TAG, debugInfo() + "No need to stop");
            return;
        }
        while (mState == State.INITIALIZING) {
            Log.e(TAG, debugInfo() + "Processing,wait for finish at " + mState);
        }
        if (DEBUG) {
            Log.i(TAG, debugInfo() + " Set state to finishing");
        }
        mState = State.FINISHING;
        CoroutinePool.INSTANCE.run(new Runnable() {
            @Override
            public void run() {
                frames.clear();
                for (Bitmap bitmap : cacheBitmaps) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                cacheBitmaps.clear();
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
            }
        }, count, new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {

            }
        });
        renderListener.onEnd();
    }

    private String debugInfo() {
        if (DEBUG) {
            return String.format("thread is %s, decoder is %s,state is %s", Thread.currentThread(), FrameSeqDecoder.this.toString(), mState.toString());
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
        this.playCount = 0;
        this.frameIndex = -1;
        this.finished = false;
    }

    public void pause() {
        paused.compareAndSet(false, true);
    }

    public void resume() {
        paused.compareAndSet(true, false);
        CoroutinePool.INSTANCE.run(renderTask, count, new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {

            }
        });
    }


    public int getSampleSize() {
        return sampleSize;
    }

    public void setDesiredSize(int width, int height) {
        int sample = getDesiredSample(width, height);
        if (sample != this.sampleSize) {
            this.sampleSize = sample;
            final boolean tempRunning = isRunning();
            stop();
            CoroutinePool.INSTANCE.run(new Runnable() {
                @Override
                public void run() {
                    frames.clear();
                    try {
                        initCanvasBounds(read(getReader(mLoader.obtain())));
                        if (tempRunning) {
                            start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, count, new Continuation<Unit>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {

                }
            });
        }
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
        if (frames.size() == 0) {
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
        Frame frame = getFrame(this.frameIndex);
        if (frame == null) {
            return 0;
        }
        renderFrame(frame);
        return frame.frameDuration;
    }

    protected abstract void renderFrame(Frame frame);

    private Frame getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            return null;
        }
        return frames.get(index);
    }
}
