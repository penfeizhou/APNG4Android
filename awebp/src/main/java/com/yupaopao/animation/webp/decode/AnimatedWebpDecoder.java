package com.yupaopao.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.yupaopao.animation.webp.StreamLoader;
import com.yupaopao.animation.webp.reader.Reader;
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
 * @Description: Animated webp 解码器
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class AnimatedWebpDecoder {
    private static final String TAG = AnimatedWebpDecoder.class.getSimpleName();
    private final Paint mTransparentFillPaint;
    private final StreamLoader mLoader;
    private List<Frame> frames = new ArrayList<>();
    private int frameIndex = -1;
    private int playCount;
    private Paint paint;
    private int loopCount;
    private Integer loopLimit = null;
    private int num_frames;
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
    private int sampleSize = 1;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private Set<Bitmap> cacheBitmaps = new HashSet<>();
    private Map<Bitmap, Canvas> cachedCanvas = new WeakHashMap<>();
    private ByteBuffer frameBuffer;
    private Rect fullRect;
    private int canvasWidth;
    private int canvasHeight;
    private int backgroundColor;
    private Writer mWriter = new ByteBufferWriter();

    private Bitmap obtainBitmap(int width, int height) {
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

    private void recycleBitmap(Bitmap bitmap) {
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
    public AnimatedWebpDecoder(StreamLoader loader, RenderListener renderListener) {
        this.mLoader = loader;
        this.renderListener = renderListener;
        mTransparentFillPaint = new Paint();
        mTransparentFillPaint.setColor(Color.TRANSPARENT);
        mTransparentFillPaint.setStyle(Paint.Style.FILL);
        mTransparentFillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public Rect getBounds() {
        if (fullRect != null) {
            return fullRect;
        } else {
            Callable<Rect> callable = new Callable<Rect>() {
                @Override
                public Rect call() throws Exception {
                    if (fullRect == null) {
                        read();
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
                        read();
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
                || (this.playCount == getNumPlays() - 1 && this.frameIndex < this.num_frames - 1)) {
            running = true;
            this.frameIndex = -1;
            getExecutor().remove(renderTask);
            getExecutor().execute(renderTask);
            renderListener.onStart();
        } else {
            Log.i(TAG, "No need to started");
        }
    }

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
                        read();
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

    private void read() throws IOException {
        Reader reader = mLoader.obtain();
        List<BaseChunk> chunks = WebPParser.parse(reader);
        for (BaseChunk chunk : chunks) {
            if (chunk instanceof VP8XChunk) {
                this.canvasWidth = ((VP8XChunk) chunk).canvasWidth;
                this.canvasHeight = ((VP8XChunk) chunk).canvasHeight;
            } else if (chunk instanceof ANIMChunk) {
                this.backgroundColor = ((ANIMChunk) chunk).backgroundColor;
                this.loopCount = ((ANIMChunk) chunk).loopCount;
            } else if (chunk instanceof ANMFChunk) {
                frames.add(new Frame(reader, (ANMFChunk) chunk));
            }
        }
        this.num_frames = frames.size();
        createCanvas();
    }

    private int getNumPlays() {
        return this.loopLimit != null ? this.loopLimit : this.loopCount;
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
        } else if (this.playCount == getNumPlays() - 1 && this.frameIndex < this.num_frames - 1) {
            return true;
        }
        return false;
    }

    @WorkerThread
    private long step() {
        this.frameIndex++;
        if (this.frameIndex >= this.num_frames) {
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

    private void renderFrame(Frame frame) {
        if (frame == null) {
            return;
        }
        Bitmap bitmap = obtainBitmap(fullRect.width() / sampleSize, fullRect.height() / sampleSize);
        Canvas canvas = cachedCanvas.get(bitmap);
        if (canvas == null) {
            canvas = new Canvas(bitmap);
            cachedCanvas.put(bitmap, canvas);
        }
        // 从缓存中恢复当前帧
        frameBuffer.rewind();
        bitmap.copyPixelsFromBuffer(frameBuffer);

        if (this.frameIndex == 0) {
            canvas.drawColor(backgroundColor);
        } else {
            Frame preFrame = frames.get(this.frameIndex - 1);
            //Dispose to background color. Fill the rectangle on the canvas covered by the current frame with background color specified in the ANIM chunk.
            if (preFrame.disposalMethod) {
                final float left = (float) preFrame.frameX / (float) sampleSize;
                final float top = (float) preFrame.frameY / (float) sampleSize;
                final float right = (float) (preFrame.frameX + preFrame.frameWidth) / (float) sampleSize;
                final float bottom = (float) (preFrame.frameY + preFrame.frameHeight) / (float) sampleSize;
                canvas.drawRect(left, top, right, bottom, mTransparentFillPaint);
            }
        }
        Bitmap inBitmap = obtainBitmap(frame.frameWidth / sampleSize, frame.frameHeight / sampleSize);
        recycleBitmap(frame.draw(canvas, paint, sampleSize, inBitmap, mWriter));
        recycleBitmap(inBitmap);
        frameBuffer.rewind();
        bitmap.copyPixelsToBuffer(frameBuffer);
        recycleBitmap(bitmap);
    }

    private Frame getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            return null;
        }
        return frames.get(index);
    }

    private void createCanvas() {
        fullRect = new Rect(0, 0, canvasWidth, canvasHeight);
        paint = new Paint();
        paint.setAntiAlias(true);
        frameBuffer = ByteBuffer.allocate((canvasWidth * canvasHeight / sampleSize ^ 2 + 1) * 4);
        mTransparentFillPaint.setColor(backgroundColor);
    }

}
