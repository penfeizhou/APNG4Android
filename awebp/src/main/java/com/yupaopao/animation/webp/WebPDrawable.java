package com.yupaopao.animation.webp;


import android.content.Context;

import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.loader.AssetStreamLoader;
import com.yupaopao.animation.loader.FileLoader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.loader.ResourceStreamLoader;
import com.yupaopao.animation.loader.StreamLoader;
import com.yupaopao.animation.webp.decode.WebPDecoder;

/**
 * @Description: Animated webp drawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class WebPDrawable extends FrameAnimationDrawable {

    public WebPDrawable(Loader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return new WebPDecoder(streamLoader, listener);
    }

    public static WebPDrawable fromAsset(Context context, String assetPath) {
        AssetStreamLoader assetStreamLoader = new AssetStreamLoader(context, assetPath);
        return new WebPDrawable(assetStreamLoader);
    }

    public static WebPDrawable fromFile(String filePath) {
        FileLoader fileLoader = new FileLoader(filePath);
        return new WebPDrawable(fileLoader);
    }

    public static WebPDrawable fromResource(Context context, int resId) {
        ResourceStreamLoader resourceStreamLoader = new ResourceStreamLoader(context, resId);
        return new WebPDrawable(resourceStreamLoader);
    }
}