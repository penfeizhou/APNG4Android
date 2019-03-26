package com.yupaopao.apng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jiahongyu
 * @description Apng加载器
 * @link https://github.com/kris520/ApngDrawable
 * @date 18-6-19
 */
public class ApngLoader {
    private static class SingletonCreator {
        public static final ApngLoader instance = new ApngLoader();
    }

    private static ApngLoader getInstance() {
        return SingletonCreator.instance;
    }

    private ScheduledThreadPoolExecutor excutor = new ScheduledThreadPoolExecutor(1,
            new ThreadPoolExecutor.DiscardPolicy());
    private Context appContext = null;
    private Handler uiHandler = null;

    public static void init(Context context) {
        getInstance().appContext = context.getApplicationContext();
        getInstance().uiHandler = new Handler(Looper.getMainLooper());
    }

    public static Context getAppContext() {
        return getInstance().appContext;
    }

    /**
     * 原装，只针对本地，不针对网络图片
     */
    public static void loadImage(final String uri, final ImageView imageView,
                                 final ApngPlayListener listener) {
        loadRepeatCountImage(uri, imageView, 0, listener);
    }

    /**
     * 原装，只针对本地，不针对网络图片
     *
     * @param repeatCount apng播放次数，0 = 无限循环
     */
    public static void loadRepeatCountImage(final String uri, final ImageView imageView,
                                            final int repeatCount, final ApngPlayListener listener) {
        getInstance().excutor.execute(new Runnable() {
            @Override
            public void run() {
                ApngImageUtil.Scheme urlType = ApngImageUtil.Scheme.ofUri(uri);
                Bitmap decodeBitmap = null;
                switch (urlType) {
                    case FILE:
                        decodeBitmap = ApngImageUtil.decodeFileToDrawable(uri, null);
                        break;
                    case ASSETS:
                        String filePath = ApngImageUtil.Scheme.ASSETS.crop(uri);
                        try {
                            InputStream inputStream = getAppContext().getAssets().open(filePath);
                            decodeBitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (IOException | OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                Drawable drawable = ApngImageUtil.bitmapToDrawable(uri, imageView, decodeBitmap);

                // 将结果通知ui业务层
                final Drawable finalDrawable = drawable;
                getInstance().uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        playApng(finalDrawable, uri, imageView, repeatCount, listener);
                    }
                });
            }
        });
    }

    private static void playApng(final Drawable finalDrawable, final String uri, final ImageView imageView,
                                 int repeatCount, final ApngPlayListener listener) {
        if (finalDrawable != null) {
            Drawable oldDrawable = imageView.getDrawable();
            if (oldDrawable != finalDrawable && oldDrawable instanceof ApngDrawable) {
                ((ApngDrawable) oldDrawable).stop();
            }
            imageView.setImageDrawable(finalDrawable);

            if (finalDrawable instanceof ApngDrawable) {
                ((ApngDrawable) finalDrawable).setPlayListener(listener);
                ((ApngDrawable) finalDrawable).setNumPlays(repeatCount);
                ((ApngDrawable) finalDrawable).start();
            }
        } else {
            if (listener != null) {
                listener.onAnimationFailed();
            }
        }
    }
}
