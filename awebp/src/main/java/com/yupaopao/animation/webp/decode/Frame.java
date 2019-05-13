package com.yupaopao.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.yupaopao.animation.webp.reader.Reader;
import com.yupaopao.animation.webp.writer.Writer;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public abstract class Frame {
    protected final Reader reader;
    int frameWidth;
    int frameHeight;
    int frameX;
    int frameY;
    int frameDuration;

    protected Frame(Reader reader) {
        this.reader = reader;
    }

    abstract Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, Writer writer);
}
