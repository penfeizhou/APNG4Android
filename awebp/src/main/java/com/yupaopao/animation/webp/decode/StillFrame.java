package com.yupaopao.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.io.Writer;

import java.io.IOException;

/**
 * @Description: StillFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class StillFrame extends Frame {
    public StillFrame(Reader reader, int width, int height) {
        super(reader);
        this.frameWidth = width;
        this.frameHeight = height;
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, Writer writer) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        options.inBitmap = reusedBitmap;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(reader.toInputStream(), null, options);
            assert bitmap != null;
            paint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
