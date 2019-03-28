package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseArray;

import com.yupaopao.animation.apng.APNGStreamLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG解码器
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDecoder {
    private static final String TAG = APNGDecoder.class.getSimpleName();
    private SparseArray<Frame> frames = new SparseArray<>();
    private Bitmap bitmap;
    private Canvas canvas;
    private int frameIndex = -1;
    private int playCount;
    private Rect fullRect;
    private Paint paint;
    private int num_plays;
    private int num_frames;
    private final Handler animationHandler;
    private final Handler uiHandler;
    private final RenderListener renderListener;
    private boolean running;
    private final APNGStreamLoader APNGStreamLoader;
    private Runnable renderTask = new Runnable() {
        @Override
        public void run() {
            if (canStep()) {
                animationHandler.postDelayed(this, step());
                uiHandler.post(invalidateRunnable);
            } else {
                stop();
            }
        }
    };

    private Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            renderListener.onRender(bitmap);
        }
    };
    private int sampleSize = 1;
    private int desiredWidth;
    private int desiredHeight;

    public APNGDecoder(APNGStreamLoader provider, RenderListener renderListener) {
        this.APNGStreamLoader = provider;
        this.renderListener = renderListener;
        this.uiHandler = new Handler();

        HandlerThread handlerThread = new HandlerThread("apng");
        handlerThread.start();
        animationHandler = new Handler(handlerThread.getLooper());
    }

    public void start() {
        if (frames.size() == 0) {
            animationHandler.post(new Runnable() {
                @Override
                public void run() {
                    readInputStream();
                }
            });
        }

        if (running) {
            Log.i(TAG, "Already started");
        } else {
            running = true;
            animationHandler.removeCallbacks(renderTask);
            uiHandler.removeCallbacks(invalidateRunnable);
            animationHandler.post(renderTask);
        }
    }

    public void stop() {
        running = false;
        animationHandler.removeCallbacks(renderTask);
        uiHandler.removeCallbacks(invalidateRunnable);
    }

    public boolean isRunning() {
        return running;
    }

    public void setLoopLimit(int limit) {
        this.num_plays = limit;
    }


    public void setDesiredSize(int width, int height) {
        desiredWidth = width;
        desiredHeight = height;
        if (fullRect == null) {
            animationHandler.post(new Runnable() {
                @Override
                public void run() {
                    frames.clear();
                    readInputStream();
                }
            });
        } else {
            int sample = getDesiredSample();
            if (sample != this.sampleSize) {
                this.sampleSize = sample;
                final boolean tempRunning = running;
                stop();
                animationHandler.post(new Runnable() {
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
    }

    private int getDesiredSample() {
        if (fullRect == null || desiredWidth == 0 || desiredHeight == 0) {
            return 1;
        }
        int radio = Math.min(fullRect.width() / desiredWidth, fullRect.height() / desiredHeight);
        int sample = 1;
        while ((sample * 2) <= radio) {
            sample *= 2;
        }
        return sample;
    }

    private void readInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = APNGStreamLoader.getInputStream();
            byte[] sigBytes = new byte[8];
            inputStream.read(sigBytes);
            String signature = new String(sigBytes);
            Log.d(TAG, "read png signature:" + signature);
            Chunk chunk;
            int lastSeq = -1;
            List<Chunk> otherChunks = new ArrayList<>();
            ACTLChunk actlChunk;
            while ((chunk = Chunk.read(inputStream)) != null) {
                if (chunk instanceof IENDChunk) {
                    break;
                } else if (chunk instanceof ACTLChunk) {
                    actlChunk = (ACTLChunk) chunk;
                    this.num_frames = actlChunk.num_frames;
                    this.num_plays = actlChunk.num_plays;
                } else if (chunk instanceof FCTLChunk) {
                    lastSeq++;
                    Frame frame = new Frame();
                    frame.otherChunks.addAll(otherChunks);
                    frame.sampleSize = sampleSize;
                    frames.put(lastSeq, frame);
                    frame.sequence_number = lastSeq;
                    frame.fctlChunk = (FCTLChunk) chunk;
                } else if (chunk instanceof FDATChunk) {
                    Frame frame = frames.get(lastSeq);
                    if (frame != null) {
                        frame.idatChunks.add(new FakedIDATChunk((FDATChunk) chunk));
                    }
                } else if (chunk instanceof IDATChunk) {
                    Frame frame = frames.get(lastSeq);
                    if (frame != null) {
                        frame.idatChunks.add((IDATChunk) chunk);
                    }
                } else {
                    if (chunk instanceof IHDRChunk) {
                        createCanvas((IHDRChunk) chunk);
                    }
                    otherChunks.add(chunk);
                }
            }
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
        return this.num_plays;
    }

    private boolean canStep() {
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
        disposeOp();
        this.frameIndex++;
        if (this.frameIndex >= this.num_frames) {
            this.frameIndex = 0;
            this.playCount++;
        }
        Frame frame = getFrame(this.frameIndex);
        frame.prepare();
        blendOp();
        return frame.delay;
    }

    private void disposeOp() {
        if (this.frameIndex < 0) {
            canvas.clipRect(fullRect, Region.Op.REPLACE);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            return;
        }
        Frame frame = getFrame(this.frameIndex);
        frame.prepare();
        switch (frame.dispose_op) {
            case FCTLChunk.APNG_DISPOSE_OP_PREVIOUS:
                canvas.clipRect(frame.dstRect, Region.Op.REPLACE);
                canvas.drawBitmap(frame.bitmap, frame.srcRect, frame.dstRect, paint);
                break;
            case FCTLChunk.APNG_DISPOSE_OP_BACKGROUND:
                canvas.clipRect(frame.dstRect, Region.Op.REPLACE);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                break;
            case FCTLChunk.APNG_DISPOSE_OP_NON:
            default:
                break;
        }
    }

    private void blendOp() {
        Frame frame = getFrame(this.frameIndex);
        frame.prepare();
        canvas.clipRect(frame.dstRect, Region.Op.REPLACE);
        if (frame.blend_op == FCTLChunk.APNG_BLEND_OP_SOURCE) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        canvas.drawBitmap(frame.bitmap, frame.srcRect, frame.dstRect, paint);
    }

    private Frame getFrame(int index) {
        return frames.get(index);
    }

    private void createCanvas(IHDRChunk ihdrChunk) {
        Bitmap.Config config;
        switch (ihdrChunk.colorType) {
            case 0:
            case 2:
            case 3:
                config = Bitmap.Config.ARGB_4444;
                break;
            case 4:
            case 6:
            default:
                config = Bitmap.Config.ARGB_8888;
                break;
        }
        fullRect = new Rect(0, 0, ihdrChunk.width, ihdrChunk.height);
        sampleSize = getDesiredSample();
        bitmap = Bitmap.createBitmap(ihdrChunk.width / sampleSize, ihdrChunk.height / sampleSize, config);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public interface RenderListener {
        void onRender(Bitmap bitmap);
    }

    public int getWidth() {
        return fullRect.width();
    }

    public int getHeight() {
        return fullRect.height();
    }
}
