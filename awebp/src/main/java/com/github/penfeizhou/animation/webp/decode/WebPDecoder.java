package com.github.penfeizhou.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.loader.Loader;
import com.github.penfeizhou.animation.webp.io.WebPReader;
import com.github.penfeizhou.animation.webp.io.WebPWriter;

import java.io.IOException;
import java.util.List;

/**
 * @Description: Animated webp Decoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class WebPDecoder extends FrameSeqDecoder<WebPReader, WebPWriter> {
    private static final String TAG = WebPDecoder.class.getSimpleName();
    private final Paint mTransparentFillPaint;
    private Paint paint;
    private int loopCount;

    private int canvasWidth;
    private int canvasHeight;
    private boolean alpha;
    private int backgroundColor;
    private WebPWriter mWriter;

    /**
     * @param loader         webp stream loader
     * @param renderListener callback for rendering
     */
    public WebPDecoder(Loader loader, RenderListener renderListener) {
        super(loader, renderListener);
        mTransparentFillPaint = new Paint();
        mTransparentFillPaint.setColor(Color.TRANSPARENT);
        mTransparentFillPaint.setStyle(Paint.Style.FILL);
        mTransparentFillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    @Override
    protected WebPWriter getWriter() {
        if (mWriter == null) {
            mWriter = new WebPWriter();
        }
        return mWriter;
    }

    @Override
    protected WebPReader getReader(Reader reader) {
        return new WebPReader(reader);
    }

    @Override
    protected int getLoopCount() {
        return loopCount;
    }

    @Override
    protected void release() {

    }

    @Override
    protected Rect read(WebPReader reader) throws IOException {
        List<BaseChunk> chunks = WebPParser.parse(reader);
        boolean anim = false;
        boolean vp8x = false;
        for (BaseChunk chunk : chunks) {
            if (chunk instanceof VP8XChunk) {
                this.canvasWidth = ((VP8XChunk) chunk).canvasWidth;
                this.canvasHeight = ((VP8XChunk) chunk).canvasHeight;
                this.alpha = ((VP8XChunk) chunk).alpha();
                vp8x = true;
            } else if (chunk instanceof ANIMChunk) {
                anim = true;
                this.backgroundColor = ((ANIMChunk) chunk).backgroundColor;
                this.loopCount = ((ANIMChunk) chunk).loopCount;
            } else if (chunk instanceof ANMFChunk) {
                frames.add(new AnimationFrame(reader, (ANMFChunk) chunk));
            }
        }
        if (!anim) {
            //静态图
            if (!vp8x) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(reader.toInputStream(), null, options);
                canvasWidth = options.outWidth;
                canvasHeight = options.outHeight;
            }
            frames.add(new StillFrame(reader, canvasWidth, canvasHeight));
            this.loopCount = 1;
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        if (!this.alpha) {
            mTransparentFillPaint.setColor(backgroundColor);
        }
        return new Rect(0, 0, canvasWidth, canvasHeight);
    }

    @Override
    protected void renderFrame(Frame<WebPReader, WebPWriter> frame) {
        if (frame == null || fullRect == null) {
            return;
        }
        if (fullRect.width() <= 0 || fullRect.height() <= 0) {
            return;
        }
        Bitmap bitmap = obtainBitmap(fullRect.width() / sampleSize, fullRect.height() / sampleSize);
        if (bitmap == null) {
            return;
        }
        Canvas canvas = cachedCanvas.get(bitmap);
        if (canvas == null) {
            canvas = new Canvas(bitmap);
            cachedCanvas.put(bitmap, canvas);
        }
        // 从缓存中恢复当前帧
        frameBuffer.rewind();
        bitmap.copyPixelsFromBuffer(frameBuffer);

        if (this.frameIndex == 0) {
            if (this.alpha) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
            } else {
                canvas.drawColor(backgroundColor, PorterDuff.Mode.SRC);
            }
        } else {
            Frame<WebPReader, WebPWriter> preFrame = frames.get(this.frameIndex - 1);
            //Dispose to background color. Fill the rectangle on the canvas covered by the current frame with background color specified in the ANIM chunk.
            if (preFrame instanceof AnimationFrame
                    && ((AnimationFrame) preFrame).disposalMethod) {
                final float left = (float) preFrame.frameX * 2 / (float) sampleSize;
                final float top = (float) preFrame.frameY * 2 / (float) sampleSize;
                final float right = (float) (preFrame.frameX * 2 + preFrame.frameWidth) / (float) sampleSize;
                final float bottom = (float) (preFrame.frameY * 2 + preFrame.frameHeight) / (float) sampleSize;
                canvas.drawRect(left, top, right, bottom, mTransparentFillPaint);
            }
        }
        Bitmap inBitmap = null;
        if (frame.frameWidth > 0 && frame.frameHeight > 0) {
            inBitmap = obtainBitmap(frame.frameWidth / sampleSize, frame.frameHeight / sampleSize);
        }
        recycleBitmap(frame.draw(canvas, paint, sampleSize, inBitmap, getWriter()));
        recycleBitmap(inBitmap);
        frameBuffer.rewind();
        bitmap.copyPixelsToBuffer(frameBuffer);
        recycleBitmap(bitmap);
    }
}
