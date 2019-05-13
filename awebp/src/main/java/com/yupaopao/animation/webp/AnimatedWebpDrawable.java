package com.yupaopao.animation.webp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.Animatable2Compat;

import com.yupaopao.animation.loader.StreamLoader;
import com.yupaopao.animation.webp.decode.AnimatedWebpDecoder;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: Animated webp drawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class AnimatedWebpDrawable extends Drawable implements Animatable2Compat, AnimatedWebpDecoder.RenderListener {
    private static final String TAG = AnimatedWebpDrawable.class.getSimpleName();
    private final Paint paint = new Paint();
    private final AnimatedWebpDecoder animatedWebpDecoder;
    private DrawFilter drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Matrix matrix = new Matrix();
    private Set<AnimationCallback> animationCallbacks = new HashSet<>();
    private Bitmap bitmap;
    private static final int MSG_ANIMATION_START = 1;
    private static final int MSG_ANIMATION_END = 2;
    private Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIMATION_START:
                    for (AnimationCallback animationCallback : animationCallbacks) {
                        animationCallback.onAnimationStart(AnimatedWebpDrawable.this);
                    }
                    break;
                case MSG_ANIMATION_END:
                    for (AnimationCallback animationCallback : animationCallbacks) {
                        animationCallback.onAnimationEnd(AnimatedWebpDrawable.this);
                    }
                    break;
            }
        }
    };
    private Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };

    public AnimatedWebpDrawable(StreamLoader provider) {
        paint.setAntiAlias(true);
        animatedWebpDecoder = new AnimatedWebpDecoder(provider, this);
    }

    /**
     * @param loopLimit <=0为无限播放,>0为实际播放次数
     */
    public void setLoopLimit(int loopLimit) {
        animatedWebpDecoder.setLoopLimit(loopLimit);
    }

    public void reset() {
        animatedWebpDecoder.reset();
    }

    public void pause() {
        animatedWebpDecoder.pause();
    }

    public void resume() {
        animatedWebpDecoder.resume();
    }

    public boolean isPaused() {
        return animatedWebpDecoder.isPaused();
    }

    @Override
    public void start() {
        animatedWebpDecoder.start();
    }

    @Override
    public void stop() {
        animatedWebpDecoder.stop();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    public boolean isRunning() {
        return animatedWebpDecoder.isRunning();
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
        animatedWebpDecoder.setDesiredSize(getBounds().width(), getBounds().height());
        matrix.setScale(
                1.0f * getBounds().width() * animatedWebpDecoder.getSampleSize() / animatedWebpDecoder.getBounds().width(),
                1.0f * getBounds().height() * animatedWebpDecoder.getSampleSize() / animatedWebpDecoder.getBounds().height());
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
        Message.obtain(uiHandler, MSG_ANIMATION_START).sendToTarget();
    }

    @Override
    public void onRender(ByteBuffer byteBuffer) {
        if (!isRunning()) {
            return;
        }
        if (this.bitmap == null || this.bitmap.isRecycled()) {
            this.bitmap = Bitmap.createBitmap(
                    animatedWebpDecoder.getBounds().width() / animatedWebpDecoder.getSampleSize(),
                    animatedWebpDecoder.getBounds().height() / animatedWebpDecoder.getSampleSize(),
                    Bitmap.Config.ARGB_8888);
        }
        byteBuffer.rewind();
        this.bitmap.copyPixelsFromBuffer(byteBuffer);
        uiHandler.post(invalidateRunnable);
    }

    @Override
    public void onEnd() {
        Message.obtain(uiHandler, MSG_ANIMATION_END).sendToTarget();
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
        return animatedWebpDecoder.getBounds().width();
    }

    @Override
    public int getIntrinsicHeight() {
        return animatedWebpDecoder.getBounds().height();
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
