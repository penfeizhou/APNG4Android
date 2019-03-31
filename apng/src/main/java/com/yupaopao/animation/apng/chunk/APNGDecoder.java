package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: APNG解码器
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDecoder {
    private static final String TAG = APNGDecoder.class.getSimpleName();
    private List<Frame> frames = new ArrayList<>();
    private int frameIndex = -1;
    private int playCount;
    private Rect fullRect;
    private Paint paint;
    private int num_plays;
    private Integer loopLimit = null;
    private int num_frames;
    private final RenderListener renderListener;
    private boolean running;
    private final APNGStreamLoader mAPNGStreamLoader;
    private Runnable renderTask = new Runnable() {
        @Override
        public void run() {
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
    private final Mode mode;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private byte[] decodingBuffers = new byte[0];

    private Set<Bitmap> cacheBitmaps = new HashSet<>();
    private ByteBuffer frameBuffer;

    private class SnapShot {
        byte dispose_op;
        Rect dstRect;
        ByteBuffer byteBuffer;
    }

    private SnapShot snapShot = new SnapShot();

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

    public enum Mode {
        /**
         * 播放速度优先
         * 该模式下每一帧都缓存解码后的bitmap
         * java memory低，native memory占用高
         * 播放速度最快
         * 大图模式下容易引发OOM，推荐仅小图使用
         */
        MODE_SPEED,
        /**
         * 内存占用优先
         * 该模式下每一帧仅保留基本信息，播放时实时从流中实时读取到内存并解码
         * java memory略高，native memory占用低
         * 播放速度最慢
         * 推荐在帧间隔较大情况下使用
         */
        MODE_MEMORY,
        /**
         * 平衡策略
         * 该模式下每一帧保留图像原始信息，播放时从内存中解码
         * java memory高，native memory占用低
         * 播放速度较慢
         * 默认使用这种模式
         */
        MODE_BALANCED,
    }

    public APNGDecoder(APNGStreamLoader provider, RenderListener renderListener) {
        this(provider, renderListener, Mode.MODE_MEMORY);
    }

    /**
     * @param provider       APNG文件流加载器
     * @param renderListener 渲染的回调
     * @param mode           帧播放方式,@see FrameMode
     */
    public APNGDecoder(APNGStreamLoader provider, RenderListener renderListener, Mode mode) {
        this.mAPNGStreamLoader = provider;
        this.renderListener = renderListener;
        this.mode = mode;
    }

    public Rect getBounds() {
        if (fullRect != null) {
            return fullRect;
        } else {
            Callable<Rect> callable = new Callable<Rect>() {
                @Override
                public Rect call() throws Exception {
                    return mAPNGStreamLoader.getBounds();
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
                    readInputStream();
                }
            }
        });
        if (running) {
            Log.i(TAG, "Already started");
        } else {
            running = true;
            getExecutor().remove(renderTask);
            getExecutor().execute(renderTask);
            renderListener.onStart();
        }
    }

    public void stop() {
        boolean tempRunning = running;
        running = false;
        getExecutor().remove(renderTask);
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (Frame frame : frames) {
                    frame.recycle();
                }
                frames.clear();
                for (Bitmap bitmap : cacheBitmaps) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                cacheBitmaps.clear();
                if (snapShot != null) {
                    snapShot.byteBuffer = null;
                    snapShot = null;
                }
                if (frameBuffer != null) {
                    frameBuffer = null;
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

    public void setLoopLimit(int limit) {
        this.loopLimit = limit;
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
                    readInputStream();
                    if (tempRunning) {
                        start();
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

    private void readInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = mAPNGStreamLoader.getInputStream();
            byte[] sigBytes = new byte[8];
            inputStream.read(sigBytes);
            String signature = new String(sigBytes);
            Log.d(TAG, "read png signature:" + signature);
            Chunk chunk;
            int lastSeq = -1;
            List<Chunk> otherChunks = new ArrayList<>();
            ACTLChunk actlChunk;
            IHDRChunk ihdrChunk = null;
            int pos = 8;
            while ((chunk = Chunk.read(inputStream, mode == Mode.MODE_MEMORY)) != null) {
                pos += chunk.getRawDataLength();
                if (chunk instanceof IENDChunk) {
                    if (lastSeq >= 0) {
                        frames.get(lastSeq).endPos = pos - chunk.getRawDataLength();
                    }
                    break;
                } else if (chunk instanceof ACTLChunk) {
                    actlChunk = (ACTLChunk) chunk;
                    this.num_frames = actlChunk.num_frames;
                    this.num_plays = actlChunk.num_plays;
                } else if (chunk instanceof FCTLChunk) {
                    if (lastSeq >= 0) {
                        frames.get(lastSeq).endPos = pos - chunk.getRawDataLength();
                    }
                    lastSeq++;
                    Frame frame;
                    switch (mode) {
                        case MODE_SPEED:
                            frame = new SpeedFirstFrame(ihdrChunk,
                                    (FCTLChunk) chunk, otherChunks,
                                    sampleSize, mAPNGStreamLoader);
                            break;

                        case MODE_BALANCED:
                            frame = new BalancedFrame(ihdrChunk,
                                    (FCTLChunk) chunk, otherChunks,
                                    sampleSize, mAPNGStreamLoader);
                            break;
                        case MODE_MEMORY:
                        default:
                            frame = new LowMemoryFrame(ihdrChunk,
                                    (FCTLChunk) chunk, otherChunks,
                                    sampleSize, mAPNGStreamLoader);
                            break;
                    }
                    frame.sequence_number = lastSeq;
                    frame.startPos = pos;
                    frames.add(frame);
                } else if (chunk instanceof FDATChunk) {
                    Frame frame = frames.get(lastSeq);
                    if (frame instanceof BalancedFrame) {
                        ((BalancedFrame) frame).idatChunks.add(new FakedIDATChunk((FDATChunk) chunk));
                    }
                } else if (chunk instanceof IDATChunk) {
                    Frame frame = frames.get(lastSeq);
                    if (frame instanceof BalancedFrame) {
                        ((BalancedFrame) frame).idatChunks.add((IDATChunk) chunk);
                    }
                } else {
                    if (chunk instanceof IHDRChunk) {
                        ihdrChunk = (IHDRChunk) chunk;
                        createCanvas((IHDRChunk) chunk);
                    } else {
                        otherChunks.add(chunk);
                    }
                }
            }

            int maxSize = 0;
            for (Frame frame : frames) {
                maxSize = Math.max(maxSize, frame.endPos - frame.startPos);
            }
            maxSize += ihdrChunk.getRawDataLength();
            for (Chunk each : otherChunks) {
                maxSize += each.getRawDataLength();
            }
            maxSize += 20;
            decodingBuffers = new byte[maxSize];
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getNumPlays() {
        return this.loopLimit != null ? this.loopLimit : this.num_plays;
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
        renderFrame(frame);
        return frame.delay;
    }

    private void renderFrame(Frame frame) {
        if (frame == null) {
            return;
        }
        Bitmap bitmap = obtainBitmap(fullRect.width() / sampleSize, fullRect.height() / sampleSize);
        Canvas canvas = new Canvas(bitmap);
        // 从缓存中恢复当前帧
        frameBuffer.rewind();
        bitmap.copyPixelsFromBuffer(frameBuffer);

        // 如果需要在下一帧渲染前恢复当前显示内容，需要在渲染前将当前显示内容保存到快照中
        if (frame.dispose_op == FCTLChunk.APNG_DISPOSE_OP_PREVIOUS) {
            frameBuffer.rewind();
            snapShot.byteBuffer.rewind();
            snapShot.byteBuffer.put(frameBuffer);
        }


        //开始绘制前，处理快照中的设定
        switch (snapShot.dispose_op) {
            // 从快照中恢复上一帧之前的显示内容
            case FCTLChunk.APNG_DISPOSE_OP_PREVIOUS:
                snapShot.byteBuffer.rewind();
                bitmap.copyPixelsFromBuffer(snapShot.byteBuffer);
                break;
            // 清空上一帧所画区域
            case FCTLChunk.APNG_DISPOSE_OP_BACKGROUND:
                canvas.clipRect(snapShot.dstRect, Region.Op.REPLACE);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                break;
            // 什么都不做
            case FCTLChunk.APNG_DISPOSE_OP_NON:
            default:
                break;
        }

        //开始真正绘制当前帧的内容
        canvas.clipRect(frame.dstRect, Region.Op.REPLACE);
        if (frame.blend_op == FCTLChunk.APNG_BLEND_OP_SOURCE) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        Bitmap inBitmap = obtainBitmap(frame.srcRect.width(), frame.srcRect.height());
        recycleBitmap(frame.draw(canvas, paint, inBitmap, decodingBuffers));
        recycleBitmap(inBitmap);
        //然后根据dispose设定传递到快照信息中
        snapShot.dispose_op = frame.dispose_op;
        snapShot.dstRect = frame.dstRect;
        if (frame.blend_op == FCTLChunk.APNG_DISPOSE_OP_PREVIOUS) {
            snapShot.byteBuffer.rewind();
        }
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

    private void createCanvas(IHDRChunk ihdrChunk) {
        fullRect = new Rect(0, 0, ihdrChunk.width, ihdrChunk.height);
        paint = new Paint();
        paint.setAntiAlias(true);
        frameBuffer = ByteBuffer.allocate((ihdrChunk.width * ihdrChunk.height / sampleSize ^ 2 + 1) * 4);
        snapShot.byteBuffer = ByteBuffer.allocate((ihdrChunk.width * ihdrChunk.height / sampleSize ^ 2 + 1) * 4);
    }

}
