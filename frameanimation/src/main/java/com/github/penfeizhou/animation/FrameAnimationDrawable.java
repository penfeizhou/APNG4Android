package com.github.penfeizhou.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.loader.Loader;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

/**
 * @Description: Frame animation drawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public abstract class FrameAnimationDrawable<Decoder extends FrameSeqDecoder<?, ?>> extends Drawable implements Animatable2Compat, FrameSeqDecoder.RenderListener {
    private static final String TAG = FrameAnimationDrawable.class.getSimpleName();
    private final Paint paint = new Paint();
    private final Decoder frameSeqDecoder;
    private final DrawFilter drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Matrix matrix = new Matrix();
    private final Set<AnimationCallback> animationCallbacks = new HashSet<>();
    private Bitmap bitmap;
    private static final int MSG_ANIMATION_START = 1;
    private static final int MSG_ANIMATION_END = 2;
    private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIMATION_START: {
                    ArrayList<AnimationCallback> callbacks = new ArrayList<>(animationCallbacks);
                    for (AnimationCallback animationCallback : callbacks) {
                        animationCallback.onAnimationStart(FrameAnimationDrawable.this);
                    }
                    break;
                }
                case MSG_ANIMATION_END: {
                    ArrayList<AnimationCallback> callbacks = new ArrayList<>(animationCallbacks);
                    for (AnimationCallback animationCallback : callbacks) {
                        animationCallback.onAnimationEnd(FrameAnimationDrawable.this);
                    }
                    break;
                }
            }
        }
    };
    private final Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };
    private boolean autoPlay = true;

    private final Set<WeakReference<Callback>> obtainedCallbacks = new HashSet<>();

    private boolean noMeasure = false;

    public FrameAnimationDrawable(Decoder frameSeqDecoder) {
        paint.setAntiAlias(true);
        this.frameSeqDecoder = frameSeqDecoder;
    }

    public FrameAnimationDrawable(Loader provider) {
        paint.setAntiAlias(true);
        this.frameSeqDecoder = createFrameSeqDecoder(provider, this);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setNoMeasure(boolean noMeasure) {
        this.noMeasure = noMeasure;
    }

    protected abstract Decoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener);

    /**
     * @param loopLimit <=0为无限播放,>0为实际播放次数
     */
    public void setLoopLimit(int loopLimit) {
        frameSeqDecoder.setLoopLimit(loopLimit);
    }

    public void reset() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.eraseColor(Color.TRANSPARENT);
        }
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
        if (this.frameSeqDecoder.isRunning()) {
            this.frameSeqDecoder.stop();
        }
        this.frameSeqDecoder.reset();
        if (isVisible()) {
            innerStart();
        }
    }

    private void innerStart() {
        if (FrameSeqDecoder.DEBUG) {
            Log.d(TAG, this.toString() + ",start");
        }

        this.frameSeqDecoder.addRenderListener(this);
        if (autoPlay) {
            frameSeqDecoder.start();
        } else {
            if (!this.frameSeqDecoder.isRunning()) {
                this.frameSeqDecoder.start();
            }
        }
    }

    @Override
    public void stop() {
        innerStop();
    }

    private void innerStop() {
        if (FrameSeqDecoder.DEBUG) {
            Log.d(TAG, this.toString() + ",stop");
        }

        this.frameSeqDecoder.removeRenderListener(this);
        if (autoPlay) {
            frameSeqDecoder.stop();
        } else {
            this.frameSeqDecoder.stopIfNeeded();
        }
    }

    @Override
    public boolean isRunning() {
        return frameSeqDecoder.isRunning();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        canvas.setDrawFilter(drawFilter);
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        int oldSampleSize = frameSeqDecoder.getSampleSize();
        int sampleSize = frameSeqDecoder.setDesiredSize(getBounds().width(), getBounds().height());
        matrix.setScale(
                1.0f * getBounds().width() * sampleSize / frameSeqDecoder.getBounds().width(),
                1.0f * getBounds().height() * sampleSize / frameSeqDecoder.getBounds().height());
        if (sampleSize != oldSampleSize)
            this.bitmap = Bitmap.createBitmap(
                    frameSeqDecoder.getBounds().width() / sampleSize,
                    frameSeqDecoder.getBounds().height() / sampleSize,
                    Bitmap.Config.ARGB_8888);
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
        if (byteBuffer.remaining() < this.bitmap.getByteCount()) {
            Log.e(TAG, "onRender:Buffer not large enough for pixels");
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
        hookRecordCallbacks();
        if (this.autoPlay) {
            if (FrameSeqDecoder.DEBUG) {
                Log.d(TAG, this.toString() + ",visible:" + visible + ",restart:" + restart);
            }
            if (visible) {
                if (!isRunning()) {
                    innerStart();
                }
            } else if (isRunning()) {
                innerStop();
            }
        }
        return super.setVisible(visible, restart);
    }

    @Override
    public int getIntrinsicWidth() {
        if (noMeasure) {
            return -1;
        }
        try {
            return frameSeqDecoder.getBounds().width();
        } catch (Exception exception) {
            return 0;
        }
    }

    @Override
    public int getIntrinsicHeight() {
        if (noMeasure) {
            return -1;
        }
        try {
            return frameSeqDecoder.getBounds().height();
        } catch (Exception exception) {
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

    public int getMemorySize() {
        int size = frameSeqDecoder.getMemorySize();
        if (bitmap != null && !bitmap.isRecycled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                size += bitmap.getAllocationByteCount();
            } else {
                size += bitmap.getByteCount();
            }
        }
        return Math.max(1, size);
    }

    @Nullable
    @Override
    public Callback getCallback() {
        return super.getCallback();
    }

    private void hookRecordCallbacks() {
        List<WeakReference<Callback>> lost = new ArrayList<>();
        Callback callback = getCallback();
        boolean recorded = false;
        Set<WeakReference<Callback>> temp = new HashSet<>(obtainedCallbacks);
        for (WeakReference<Callback> ref : temp) {
            Callback cb = ref.get();
            if (cb == null) {
                lost.add(ref);
            } else {
                if (cb == callback) {
                    recorded = true;
                } else {
                    cb.invalidateDrawable(this);
                }
            }
        }
        for (WeakReference<Callback> ref : lost) {
            obtainedCallbacks.remove(ref);
        }
        if (!recorded) {
            obtainedCallbacks.add(new WeakReference<>(callback));
        }
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        Set<WeakReference<Callback>> temp = new HashSet<>(obtainedCallbacks);
        for (WeakReference<Callback> ref : temp) {
            Callback callback = ref.get();
            if (callback != null && callback != getCallback()) {
                callback.invalidateDrawable(this);
            }
        }
    }

    public Decoder getFrameSeqDecoder() {
        return frameSeqDecoder;
    }

    protected Bitmap getBitmap() {
        return bitmap;
    }

    protected DrawFilter getDrawFilter() {
        return drawFilter;
    }
}
