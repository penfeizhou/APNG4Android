package com.yupaopao.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.yupaopao.animation.webp.StreamLoader;
import com.yupaopao.animation.webp.writer.ByteBufferWriter;
import com.yupaopao.animation.webp.writer.Writer;

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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Abstract Frame Animation Decoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameSeqDecoder {
    private static final String TAG = FrameSeqDecoder.class.getSimpleName();
    protected final StreamLoader mLoader;
    protected List<Frame> frames = new ArrayList<>();
    protected int frameIndex = -1;
    private int playCount;
    private Integer loopLimit = null;
    private final RenderListener renderListener;
    private boolean running;
    private boolean paused;
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
    protected Writer mWriter = new ByteBufferWriter();

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
    public FrameSeqDecoder(StreamLoader loader, RenderListener renderListener) {
        this.mLoader = loader;
        this.renderListener = renderListener;
    }

    public Rect getBounds() {
        if (fullRect != null) {
            return fullRect;
        } else {
            Callable<Rect> callable = new Callable<Rect>() {
                @Override
                public Rect call() throws Exception {
                    if (fullRect == null) {
                        initCanvasBounds(read());
                    }
                    return fullRect;
                }
            };
            FutureTask<Rect> futureTask = new FutureTask<>(callable);
            getExecutor().execute(futureTask);
            try {
                fullRect = futureTask.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fullRect;
        }
    }

    private void initCanvasBounds(Rect rect) {
        fullRect = rect;
        frameBuffer = ByteBuffer.allocate((rect.width() * rect.height() / sampleSize ^ 2 + 1) * 4);
    }

    private ScheduledThreadPoolExecutor getExecutor() {
        if (scheduledThreadPoolExecutor == null || scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.DiscardPolicy());
        }
        return scheduledThreadPoolExecutor;
    }

    public void start() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (frames.size() == 0) {
                    try {
                        initCanvasBounds(read());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (running) {
            Log.i(TAG, "Already started");
        } else if (getNumPlays() == 0
                || this.playCount < getNumPlays() - 1
                || (this.playCount == getNumPlays() - 1 && this.frameIndex < this.getFrameCount() - 1)) {
            running = true;
            this.frameIndex = -1;
            getExecutor().remove(renderTask);
            getExecutor().execute(renderTask);
            renderListener.onStart();
        } else {
            Log.i(TAG, "No need to started");
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
        boolean tempRunning = running;
        running = false;
        getExecutor().remove(renderTask);
        getExecutor().execute(new Runnable() {
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
                    mLoader.release();
                    mWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (frameBuffer == null) {
            getExecutor().shutdownNow();
        } else {
            getExecutor().shutdown();
        }
        if (tempRunning) {
            renderListener.onEnd();
        }
    }

    public boolean isRunning() {
        return running;
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
            final boolean tempRunning = running;
            stop();
            getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    frames.clear();
                    try {
                        initCanvasBounds(read());
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

    private int getDesiredSample(int desiredWidth, int desiredHeight) {
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

    protected abstract Rect read() throws IOException;

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
