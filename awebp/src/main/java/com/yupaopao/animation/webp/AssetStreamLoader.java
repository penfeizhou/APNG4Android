package com.yupaopao.animation.webp;

import android.content.Context;


import com.yupaopao.animation.webp.chunk.StreamLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 从Asset中读取流
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class AssetStreamLoader extends StreamLoader {

    private final Context mContext;
    private final String mAssetName;

    public AssetStreamLoader(Context context, String assetName) {
        mContext = context.getApplicationContext();
        mAssetName = assetName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getAssets().open(mAssetName);
    }
}
