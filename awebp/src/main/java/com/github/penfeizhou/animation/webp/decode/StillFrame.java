package com.github.penfeizhou.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.Writer;
import com.github.penfeizhou.animation.webp.io.WebPReader;
import com.github.penfeizhou.animation.webp.io.WebPWriter;

import java.io.IOException;

/**
 * @Description: StillFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class StillFrame extends Frame<WebPReader, WebPWriter> {
    public StillFrame(WebPReader reader, int width, int height) {
        super(reader);
        this.frameWidth = width;
        this.frameHeight = height;
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, WebPWriter writer) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        options.inBitmap = reusedBitmap;
        Bitmap bitmap = null;
        try {

            try {
                bitmap = BitmapFactory.decodeStream(reader.toInputStream(), null, options);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                // Problem decoding into existing bitmap when on Android 4.2.2 & 4.3
                BitmapFactory.Options optionsFixed = new BitmapFactory.Options();
                optionsFixed.inJustDecodeBounds = false;
                optionsFixed.inSampleSize = sampleSize;
                optionsFixed.inMutable = true;
                bitmap = BitmapFactory.decodeStream(reader.toInputStream(), null, optionsFixed);
            }
            assert bitmap != null;
            paint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
