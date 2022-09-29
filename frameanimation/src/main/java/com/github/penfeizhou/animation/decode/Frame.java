package com.github.penfeizhou.animation.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.Writer;


/**
 * @Description: One frame in an animation
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public abstract class Frame<R extends Reader, W extends Writer> {
    protected final R reader;
    public int frameWidth;
    public int frameHeight;
    public int frameX;
    public int frameY;
    public int frameDuration;

    protected final Rect srcRect = new Rect();
    protected final Rect dstRect = new Rect();

    public Frame(R reader) {
        this.reader = reader;
    }

    public abstract Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, W writer);
}
