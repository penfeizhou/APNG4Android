package com.github.penfeizhou.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.webp.io.WebPReader;
import com.github.penfeizhou.animation.webp.io.WebPWriter;

import java.io.IOException;

/**
 * @Description: AnimationFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class AnimationFrame extends Frame<WebPReader, WebPWriter> {
    final int imagePayloadOffset;
    final int imagePayloadSize;
    final boolean blendingMethod;
    final boolean disposalMethod;
    private final boolean useAlpha;
    private static final PorterDuffXfermode PORTERDUFF_XFERMODE_SRC_OVER = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    private static final PorterDuffXfermode PORTERDUFF_XFERMODE_SRC = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    public AnimationFrame(WebPReader reader, ANMFChunk anmfChunk) {
        super(reader);
        this.frameWidth = anmfChunk.frameWidth;
        this.frameHeight = anmfChunk.frameHeight;
        this.frameX = anmfChunk.frameX;
        this.frameY = anmfChunk.frameY;
        this.frameDuration = anmfChunk.frameDuration;
        if (this.frameDuration == 0) {
            this.frameDuration = 100;
        }
        this.blendingMethod = anmfChunk.blendingMethod();
        this.disposalMethod = anmfChunk.disposalMethod();
        this.imagePayloadOffset = anmfChunk.offset + BaseChunk.CHUNCK_HEADER_OFFSET + 16;
        this.imagePayloadSize = anmfChunk.payloadSize - 16 + (anmfChunk.payloadSize & 1);
        this.useAlpha = anmfChunk.alphChunk != null;
    }

    private int encode(WebPWriter writer) {
        int vp8xPayloadSize = 10;
        int size = 12 + (BaseChunk.CHUNCK_HEADER_OFFSET + vp8xPayloadSize) + this.imagePayloadSize;
        writer.reset(size);
        // Webp Header
        writer.putFourCC("RIFF");
        writer.putUInt32(size);
        writer.putFourCC("WEBP");

        //VP8X
        writer.putUInt32(VP8XChunk.ID);
        writer.putUInt32(vp8xPayloadSize);
        writer.putByte((byte) (useAlpha ? 0x10 : 0));
        writer.putUInt24(0);
        writer.put1Based(frameWidth);
        writer.put1Based(frameHeight);

        //ImageData
        try {
            reader.reset();
            reader.skip(this.imagePayloadOffset);
            reader.read(writer.toByteArray(), writer.position(), this.imagePayloadSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, WebPWriter writer) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        options.inBitmap = reusedBitmap;
        int length = encode(writer);
        byte[] bytes = writer.toByteArray();
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, options);
        } catch (IllegalArgumentException e) {
            // Problem decoding into existing bitmap when on Android 4.2.2 & 4.3
            BitmapFactory.Options optionsFixed = new BitmapFactory.Options();
            optionsFixed.inJustDecodeBounds = false;
            optionsFixed.inSampleSize = sampleSize;
            optionsFixed.inMutable = true;
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, optionsFixed);
        }
        if (bitmap != null) {
            if (blendingMethod) {
                paint.setXfermode(PORTERDUFF_XFERMODE_SRC);
            } else {
                paint.setXfermode(PORTERDUFF_XFERMODE_SRC_OVER);
            }
            srcRect.left = 0;
            srcRect.top = 0;
            srcRect.right = bitmap.getWidth();
            srcRect.bottom = bitmap.getHeight();
            dstRect.left = (int) ((float) frameX * 2 / sampleSize);
            dstRect.top = (int) ((float) frameY * 2 / sampleSize);
            dstRect.right = (int) ((float) frameX * 2 / sampleSize + bitmap.getWidth());
            dstRect.bottom = (int) ((float) frameY * 2 / sampleSize + bitmap.getHeight());

            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        } else {
            bitmap = reusedBitmap;
        }
        return bitmap;
    }
}
