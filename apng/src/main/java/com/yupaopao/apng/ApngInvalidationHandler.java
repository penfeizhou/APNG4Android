package com.yupaopao.apng;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author jiahongyu
 * @description
 * @date 18-6-19
 */
public class ApngInvalidationHandler extends Handler {
    private final WeakReference<ApngDrawable> mDrawableRef;

    public ApngInvalidationHandler(ApngDrawable apngDrawable) {
        super(Looper.getMainLooper());
        mDrawableRef = new WeakReference<>(apngDrawable);
    }

    @Override
    public void handleMessage(Message msg) {
        final ApngDrawable apngDrawable = mDrawableRef.get();
        if (apngDrawable != null) {
            apngDrawable.invalidateSelf();
        }
    }
}
