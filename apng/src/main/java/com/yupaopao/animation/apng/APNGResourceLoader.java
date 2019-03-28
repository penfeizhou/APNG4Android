package com.yupaopao.animation.apng;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG 资源加载器，从res中加载APNG
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class APNGResourceLoader implements APNGStreamLoader {
    private final Context mContext;
    private final int mResId;


    public APNGResourceLoader(Context context, int resId) {
        mContext = context.getApplicationContext();
        mResId = resId;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getResources().openRawResource(mResId);
    }
}
