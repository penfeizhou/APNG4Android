package com.github.pengfeizhou.animation.apng;


import android.content.Context;

import com.github.pengfeizhou.animation.FrameAnimationDrawable;
import com.github.pengfeizhou.animation.apng.decode.APNGDecoder;
import com.github.pengfeizhou.animation.decode.FrameSeqDecoder;
import com.github.pengfeizhou.animation.loader.AssetStreamLoader;
import com.github.pengfeizhou.animation.loader.FileLoader;
import com.github.pengfeizhou.animation.loader.Loader;
import com.github.pengfeizhou.animation.loader.ResourceStreamLoader;

/**
 * @Description: APNGDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDrawable extends FrameAnimationDrawable {
    public APNGDrawable(Loader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return new APNGDecoder(streamLoader, listener);
    }


    public static APNGDrawable fromAsset(Context context, String assetPath) {
        AssetStreamLoader assetStreamLoader = new AssetStreamLoader(context, assetPath);
        return new APNGDrawable(assetStreamLoader);
    }

    public static APNGDrawable fromFile(String filePath) {
        FileLoader fileLoader = new FileLoader(filePath);
        return new APNGDrawable(fileLoader);
    }

    public static APNGDrawable fromResource(Context context, int resId) {
        ResourceStreamLoader resourceStreamLoader = new ResourceStreamLoader(context, resId);
        return new APNGDrawable(resourceStreamLoader);
    }
}
