package com.yupaopao.animation.apng;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author jiahongyu
 * Reference: http://www.vogella.com/code/com.vogella.android.drawables.animation/src/com/vogella/android/drawables/animation/ColorAnimationDrawable.html
 * apng解码优化版本
 * apng文件格式
 * http://www.zhangxinxu.com/wordpress/2014/09/apng-history-character-maker-editor/
 * https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG
 */
public class ApngDrawable extends Drawable implements Animatable {
    public static final String TAG = ApngDrawable.class.getSimpleName();
    public static final boolean enableDebugLog = true;
    private static final int INFINITE_LOOP = 0;

    protected final Uri sourceUri;
    // 支持FIT_XY(默认), CENTER_CROP, CENTER_INSIDE
    private final ImageView.ScaleType scaleType;
    protected int currentFrame;
    String workingPath;
    int baseWidth;
    int baseHeight;
    ScheduledThreadPoolExecutor excutor = null;
    ApngFrameDecode frameDecode;
    ApngBitmapCache bitmapCache;
    ApngInvalidationHandler invalidationHandler;
    Bitmap frameBp;
    private Paint paint;
    private ApngPlayListener playListener = null;
    private boolean isRunning = false;
    private RectF canvasRect;
    private int currentLoop;


    /**
     * @param bitmap
     * @param uri
     * @param scaleType // 支持FIT_XY(默认), CENTER_CROP, CENTER_INSIDE
     */
    public ApngDrawable(Bitmap bitmap, Uri uri, ImageView.ScaleType scaleType) {
        super();

        // 解码器
        frameDecode = new ApngFrameDecode(this);
        // 缓存器
        bitmapCache = new ApngBitmapCache();

        this.scaleType = scaleType;
        currentFrame = -1;
        currentLoop = 0;

        paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);


        workingPath = ApngImageUtil.getImageCachePath(ApngLoader.getAppContext());
        sourceUri = uri;

        if (bitmap != null && bitmap.isMutable()) {
            bitmapCache.cacheBitmap(0, bitmap);
        }
        // 图片的宽和高不通过bitmap来获取, 因为bitmap可能由于内存原因而修改sampleSize, 从而影响了图片的宽高
        //baseWidth = bitmap.getWidth();
        //baseHeight = bitmap.getHeight();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceUri.getPath(), options);
        baseWidth = options.outWidth;
        baseHeight = options.outHeight;

        invalidationHandler = new ApngInvalidationHandler(this);
    }

    public static ApngDrawable getFromView(View view) {
        if (view == null || !(view instanceof ImageView)) {
            return null;
        }
        Drawable drawable = ((ImageView) view).getDrawable();
        if (drawable == null || !(drawable instanceof ApngDrawable)) {
            return null;
        }
        return (ApngDrawable) drawable;
    }

    public void setPlayListener(ApngPlayListener listener) {
        playListener = listener;
    }

    /**
     * Specify number of repeating. Note that this will override the value described in APNG file
     *
     * @param numPlays Number of repeating
     */
    public void setNumPlays(int numPlays) {
        frameDecode.playCount = numPlays;
    }

    public void decodePrepare() {
        if (!frameDecode.isPrepared) {
            frameDecode.prepare();
        }
    }

    /**
     * 是否需要再次播放
     */
    boolean needRepeat() {
        currentLoop++;
        if (currentLoop < frameDecode.playCount || frameDecode.playCount == INFINITE_LOOP) {
            // 到ui线程通知动画开始执行
//            invalidationHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (playListener != null) {
//                        playListener.onAnimationRepeat(ApngDrawable.this);
//                    }
//                }
//            });
            return true;
        } else {
            // 结束播放
            invalidationHandler.post(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            });
            return false;
        }
    }

    @Override
    public void start() {
        if (!isRunning()) {
            isRunning = true;
            currentFrame = 0;

            if (excutor != null) {
                excutor.shutdownNow();
            }
            excutor = new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.DiscardPolicy());

            if (!frameDecode.isPrepared) {
                //if (enableDebugLog) Log.d(TAG, "Prepare");
                // 异步进行文件初始化准备
                excutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        frameDecode.prepare();
                    }
                });
            }

            // 开始播放动画
            excutor.execute(new Runnable() {
                @Override
                public void run() {
                    frameDecode.startRenderFrame();
                    // 到ui线程通知动画开始执行
                    invalidationHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (frameDecode.isPrepared) {
                                if (playListener != null) {
                                    playListener.onAnimationStart(ApngDrawable.this);
                                }
                                ApngDrawable.this.invalidateSelf();
                            } else {
                                stop();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            if (enableDebugLog) {
                Log.d(TAG, "stop animation");
            }
            currentLoop = 0;
            //unscheduleSelf(this);
            isRunning = false;
            if (excutor != null) {
                excutor.shutdownNow();
                excutor = null;
            }
            if (playListener != null) {
                playListener.onAnimationEnd(this);
            }
            // 清空缓存
            bitmapCache.clear();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        if (enableDebugLog) {
            Log.d(TAG, "draw frame: " + currentFrame);
        }

        if (currentFrame <= 0) {
            frameBp = bitmapCache.getCacheBitmap(0);
        }
        if (frameBp != null) {
            drawBitmap(canvas, frameBp);
            currentFrame++;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private void drawBitmap(Canvas canvas, Bitmap frameBitmap) {
        if (canvasRect == null) {
            canvasRect = calcuteSanvasRect(canvas);
        }
        //图片抗锯齿需要单独加
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(frameBitmap, null, canvasRect, paint);
    }

    /**
     * 根据scaleType的设置计算画布对应的位置
     */
    private RectF calcuteSanvasRect(Canvas canvas) {

        RectF calcuteResult = null;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        float scalingByWidth = ((float) canvasWidth) / baseWidth;
        float scalingByHeight = ((float) canvasHeight) / baseHeight;

        float x = 0;
        float y = 0;
        float w = 0;
        float h = 0;
        switch (scaleType) {
            case CENTER_CROP:
                if (scalingByWidth > scalingByHeight) {
                    w = canvasWidth;
                    h = baseHeight * scalingByWidth;
                    x = 0;
                    y = 0 - (h - canvasHeight) / 2;
                } else {
                    w = baseWidth * scalingByHeight;
                    h = canvasHeight;
                    x = 0 - (w - canvasWidth) / 2;
                    y = 0;
                }
                break;
            case CENTER_INSIDE:
                if (scalingByWidth > scalingByHeight) {
                    w = baseWidth * scalingByHeight;
                    h = canvasHeight;
                    x = (canvasWidth - w) / 2;
                    y = 0;
                } else {
                    w = canvasWidth;
                    h = baseHeight * scalingByWidth;
                    x = 0;
                    y = (canvasHeight - h) / 2;
                }
                break;
            case FIT_XY:
            default:
                x = 0;
                y = 0;
                w = canvasWidth;
                h = canvasHeight;
                break;
        }
        calcuteResult = new RectF(x, y, x + w, y + h);
        return calcuteResult;
    }

    /**
     * 获取apng图片的路径
     */
    String getImagePathFromUri() {
        if (sourceUri == null) {
            return null;
        }

        String imagePath = null;

        try {
            String filename = sourceUri.getLastPathSegment();

            File file = new File(workingPath, filename);

            if (!file.exists()) {
                ApngImageUtil.copyFile(sourceUri.getPath(), file.getPath(), false);
            }

            imagePath = file.getPath();

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.toString());
        }

        return imagePath;
    }
}
