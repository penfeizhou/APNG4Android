package com.yupaopao.animation;

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
import android.util.Log;

import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.loader.Loader;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: Frame animation drawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameAnimationDrawable extends Drawable implements Animatable2Compat, FrameSeqDecoder.RenderListener {
    private static final String TAG = FrameAnimationDrawable.class.getSimpleName();
    private final Paint paint = new Paint();
    private final FrameSeqDecoder frameSeqDecoder;
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
                        animationCallback.onAnimationStart(FrameAnimationDrawable.this);
                    }
                    break;
                case MSG_ANIMATION_END:
                    for (AnimationCallback animationCallback : animationCallbacks) {
                        animationCallback.onAnimationEnd(FrameAnimationDrawable.this);
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

    public FrameAnimationDrawable(Loader provider) {
        paint.setAntiAlias(true);
        frameSeqDecoder = createFrameSeqDecoder(provider, this);
    }

    protected abstract FrameSeqDecoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener);

    /**
     * @param loopLimit <=0为无限播放,>0为实际播放次数
     */
    public void setLoopLimit(int loopLimit) {
        frameSeqDecoder.setLoopLimit(loopLimit);
    }

    public void reset() {
        frameSeqDecoder.reset();
    }

    public void pause() {
        frameSeqDecoder.pause();
    }

    public void resume() {
        frameSeqDecoder.resume();
    }

    public boolean isPaused() {
        return frameSeqDecoder.isPaused();
    }

    @Override
    public void start() {
        if (FrameSeqDecoder.DEBUG) {
            Log.d(TAG, this.toString() + ",start");
        }
        frameSeqDecoder.start();
    }

    @Override
    public void stop() {
        if (FrameSeqDecoder.DEBUG) {
            Log.d(TAG, this.toString() + ",stop");
        }
        frameSeqDecoder.stop();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    public boolean isRunning() {
        return frameSeqDecoder.isRunning();
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
        frameSeqDecoder.setDesiredSize(getBounds().width(), getBounds().height());
        matrix.setScale(
                1.0f * getBounds().width() * frameSeqDecoder.getSampleSize() / frameSeqDecoder.getBounds().width(),
                1.0f * getBounds().height() * frameSeqDecoder.getSampleSize() / frameSeqDecoder.getBounds().height());
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
                    frameSeqDecoder.getBounds().width() / frameSeqDecoder.getSampleSize(),
                    frameSeqDecoder.getBounds().height() / frameSeqDecoder.getSampleSize(),
                    Bitmap.Config.ARGB_8888);
        }
        byteBuffer.rewind();

        if (this.bitmap.isRecycled()) {
            return;
        }
        this.bitmap.copyPixelsFromBuffer(byteBuffer);
        uiHandler.post(invalidateRunnable);
    }

    @Override
    public void onEnd() {
        Message.obtain(uiHandler, MSG_ANIMATION_END).sendToTarget();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if (FrameSeqDecoder.DEBUG) {
            Log.d(TAG, this.toString() + ",visible:" + visible + ",restart:" + restart);
        }
        if (visible) {
            if (!isRunning()) {
                start();
            }
        } else if (isRunning()) {
            stop();
        }
        return super.setVisible(visible, restart);
    }

    @Override
    public int getIntrinsicWidth() {
        try {
            return frameSeqDecoder.getBounds().width();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public int getIntrinsicHeight() {
        try {
            return frameSeqDecoder.getBounds().height();
        } catch (Exception ex) {
            return 0;
        }
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
