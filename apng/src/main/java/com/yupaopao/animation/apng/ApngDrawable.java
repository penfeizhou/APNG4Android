package com.yupaopao.animation.apng;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.Animatable2Compat;

import com.yupaopao.animation.apng.chunk.APNGDecoder;
import com.yupaopao.animation.apng.chunk.APNGStreamLoader;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: APNGDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDrawable extends Drawable implements Animatable2Compat, APNGDecoder.RenderListener {
    private static final String TAG = APNGDrawable.class.getSimpleName();
    private final Paint paint = new Paint();
    private final APNGDecoder apngDecoder;
    private DrawFilter drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Matrix matrix = new Matrix();
    private Set<AnimationCallback> animationCallbacks = new HashSet<>();
    private Bitmap bitmap;
    private ByteBuffer byteBuffer;

    @Deprecated
    public APNGDrawable(APNGStreamLoader provider, APNGDecoder.Mode mode) {
        paint.setAntiAlias(true);
        apngDecoder = new APNGDecoder(provider, this, mode);
    }

    public APNGDrawable(APNGStreamLoader provider) {
        paint.setAntiAlias(true);
        apngDecoder = new APNGDecoder(provider, this);
    }

    /**
     * @param loopLimit <=0为无限播放,>0为实际播放次数
     */
    public void setLoopLimit(int loopLimit) {
        apngDecoder.setLoopLimit(loopLimit);
    }

    @Override
    public void start() {
        apngDecoder.start();
    }

    @Override
    public void stop() {
        apngDecoder.stop();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (byteBuffer != null) {
            byteBuffer = null;
        }
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
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        apngDecoder.setDesiredSize(getBounds().width(), getBounds().height());
        matrix.setScale(
                1.0f * getBounds().width() * apngDecoder.getSampleSize() / apngDecoder.getBounds().width(),
                1.0f * getBounds().height() * apngDecoder.getSampleSize() / apngDecoder.getBounds().height());
        if (!isRunning()) {
            start();
        }
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
    public void onStart() {
        for (AnimationCallback animationCallback : animationCallbacks) {
            animationCallback.onAnimationStart(this);
        }
    }

    @Override
    public void onRender(Bitmap ret) {
        if (!isRunning()) {
            return;
        }
        if (this.bitmap == null || this.bitmap.isRecycled()) {
            this.bitmap = Bitmap.createBitmap(ret.getWidth(), ret.getHeight(), Bitmap.Config.ARGB_8888);
        }
        if (byteBuffer == null || byteBuffer.limit() < ret.getByteCount()) {
            byteBuffer = ByteBuffer.allocate(ret.getByteCount());
        }
        byteBuffer.rewind();
        ret.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        this.bitmap.copyPixelsFromBuffer(byteBuffer);
        this.invalidateSelf();
    }

    @Override
    public void onEnd() {
        for (AnimationCallback animationCallback : animationCallbacks) {
            animationCallback.onAnimationEnd(this);
        }
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if (visible && !restart) {
            start();
        } else if (isRunning()) {
            stop();
        }
        return super.setVisible(visible, restart);
    }

    @Override
    public int getIntrinsicWidth() {
        return apngDecoder.getBounds().width();
    }

    @Override
    public int getIntrinsicHeight() {
        return apngDecoder.getBounds().height();
    }

    @Override
    public void registerAnimationCallback(@NonNull AnimationCallback animationCallback) {
        this.animationCallbacks.add(animationCallback);
    }

    @Override
    public boolean unregisterAnimationCallback(@NonNull AnimationCallback animationCallback) {
        return this.animationCallbacks.remove(animationCallback);
    }

    @Override
    public void clearAnimationCallbacks() {
        this.animationCallbacks.clear();
    }
}
