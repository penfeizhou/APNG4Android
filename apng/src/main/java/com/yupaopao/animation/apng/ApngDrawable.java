package com.yupaopao.animation.apng;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import com.yupaopao.animation.apng.chunk.APNGDecoder;

import java.io.InputStream;

/**
 * @Description: APNGDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDrawable extends Drawable implements Animatable, APNGDecoder.RenderListener {
    private static final String TAG = APNGDrawable.class.getSimpleName();
    private final Paint paint = new Paint();
    private final APNGDecoder apngDecoder;
    private DrawFilter drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Bitmap bitmap;

    public APNGDrawable(final InputStream inputStream) {
        paint.setAntiAlias(true);
        apngDecoder = new APNGDecoder(inputStream, this);
    }


    @Override
    public void start() {
        apngDecoder.start();
    }

    @Override
    public void stop() {
        apngDecoder.stop();
    }

    @Override
    public boolean isRunning() {
        return apngDecoder.isRunning();
    }

    @Override
    public void draw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        canvas.setDrawFilter(drawFilter);
        Matrix matrix = new Matrix();
        matrix.setScale(1.0f * getBounds().width() / bitmap.getWidth(), 1.0f * getBounds().height() / bitmap.getHeight());
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        int sample = Math.min(apngDecoder.getWidth() / getBounds().width(), apngDecoder.getHeight() / getBounds().height());
        apngDecoder.setSampleSize(Math.max(1, sample));
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void onRender(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.invalidateSelf();
    }
}
