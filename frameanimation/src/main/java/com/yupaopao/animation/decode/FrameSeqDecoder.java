package com.yupaopao.animation.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.io.Writer;
import com.yupaopao.animation.loader.Loader;

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
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: Abstract Frame Animation Decoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameSeqDecoder<R extends Reader, W extends Writer> {
    private static final String TAG = FrameSeqDecoder.class.getSimpleName();
    protected final Loader mLoader;
    protected List<Frame> frames = new ArrayList<>();
    protected int frameIndex = -1;
    private int playCount;
    private Integer loopLimit = null;
    private final RenderListener renderListener;
    private boolean paused;
    private static final Rect RECT_EMPTY = new Rect();
    private Runnable renderTask = new Runnable() {
        @Override
        public void run() {
            if (paused) {
                return;
            }
            if (canStep()) {
                long start = System.currentTimeMillis();
                long delay = step();
                long cost = System.currentTimeMillis() - start;
                getExecutor().schedule(this, Math.max(0, delay - cost), TimeUnit.MILLISECONDS);
                renderListener.onRender(frameBuffer);
            } else {
                stop();
            }
        }
    };
    protected int sampleSize = 1;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private Set<Bitmap> cacheBitmaps = new HashSet<>();
    protected Map<Bitmap, Canvas> cachedCanvas = new WeakHashMap<>();
    protected ByteBuffer frameBuffer;
    protected Rect fullRect;
    protected W mWriter = getWriter();
    private R mReader = null;

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
            if (ret != null && ret.getAllocationByteCount() >= reuseSize) {
                iterator.remove();
                if (ret.getWidth() != width || ret.getHeight() != height) {
                    ret.reconfigure(width, height, Bitmap.Config.ARGB_8888);
                }
                ret.eraseColor(0);
                return ret;
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
    }

    public Rect getBounds() {
        if (fullRect != null) {
            return fullRect;
        } else {
            while (mState == State.FINISHING) {
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
            FutureTask<Rect> futureTask = new FutureTask<>(callable);
            getExecutor().execute(futureTask);
            try {
                fullRect = futureTask.get(500, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                fullRect = RECT_EMPTY;
            }
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

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + "-pool-" +
                            poolNumber.getAndIncrement() +
                            "-thread-" + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    protected ScheduledThreadPoolExecutor getExecutor() {
        if (scheduledThreadPoolExecutor == null || scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1,
                    new DefaultThreadFactory(this.getClass().getSimpleName()),
                    new ThreadPoolExecutor.DiscardPolicy());
        }
        return scheduledThreadPoolExecutor;
    }

    public void start() {
        if (fullRect == RECT_EMPTY) {
            return;
        }

        if (mState == State.RUNNING || mState == State.INITIALIZING) {
            Log.i(TAG, getEnv() + " Already started");
            return;
        }

        while (mState == State.FINISHING) {
            Log.e(TAG, getEnv() + " Processing,wait for finish at " + mState);
        }
        Log.i(TAG, getEnv() + "Set state to INITIALIZING");
        mState = State.INITIALIZING;
        ScheduledThreadPoolExecutor executor = getExecutor();
        executor.execute(new Runnable() {
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
                            Log.i(TAG, getEnv() + " Set state to RUNNING");
                            mState = State.RUNNING;
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    if (mState == State.INITIALIZING) {
                        Log.i(TAG, getEnv() + " Set state to IDLE");
                        mState = State.IDLE;
                    }
                }
            }
        });
        if (getNumPlays() == 0
                || this.playCount < getNumPlays() - 1
                || (this.playCount == getNumPlays() - 1 && this.frameIndex < this.getFrameCount() - 1)) {
            this.frameIndex = -1;
            executor.remove(renderTask);
            executor.execute(renderTask);
            renderListener.onStart();
        } else {
            Log.i(TAG, getEnv() + " No need to started");
        }
    }

    private int getFrameCount() {
        return this.frames.size();
    }

    /**
     * @return Loop Count defined in file
     */
    protected abstract int getLoopCount();

    public void stop() {
        if (fullRect == RECT_EMPTY) {
            return;
        }
        if (mState == State.FINISHING || mState == State.IDLE) {
            Log.i(TAG, getEnv() + "No need to stop");
            return;
        }
        while (mState == State.INITIALIZING) {
            Log.e(TAG, getEnv() + "Processing,wait for finish at " + mState);
        }
        Log.i(TAG, getEnv() + " Set state to finishing");
        mState = State.FINISHING;
        final ScheduledThreadPoolExecutor executor = getExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                executor.getQueue().clear();
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
                Log.i(TAG, getEnv() + " release and Set state to IDLE");
                mState = State.IDLE;
            }
        });
        executor.shutdown();
        renderListener.onEnd();
    }

    private String getEnv() {
        return String.format("thread is %s, decoder is %s,state is %s", Thread.currentThread(), FrameSeqDecoder.this.toString(), mState.toString());
    }

    protected abstract void release();

    public boolean isRunning() {
        return mState == State.RUNNING || mState == State.INITIALIZING;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setLoopLimit(int limit) {
        this.loopLimit = limit;
    }

    public void reset() {
        this.playCount = 0;
        this.frameIndex = -1;
    }

    public void pause() {
        paused = true;
        getExecutor().remove(renderTask);
    }

    public void resume() {
        paused = false;
        getExecutor().execute(renderTask);
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
            getExecutor().execute(new Runnable() {
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
