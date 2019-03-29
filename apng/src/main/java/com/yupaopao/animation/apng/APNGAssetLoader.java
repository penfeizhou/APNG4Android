package com.yupaopao.animation.apng;

import android.content.Context;

import com.yupaopao.animation.apng.chunk.APNGStreamLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 从Asset中读取APNG
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class APNGAssetLoader extends APNGStreamLoader {

    private final Context mContext;
    private final String mAssetName;

    public APNGAssetLoader(Context context, String assetName) {
        mContext = context.getApplicationContext();
        mAssetName = assetName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getAssets().open(mAssetName);
    }
}
