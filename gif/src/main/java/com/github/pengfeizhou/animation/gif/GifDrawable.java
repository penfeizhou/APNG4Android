package com.github.pengfeizhou.animation.gif;

import android.content.Context;

import com.github.pengfeizhou.animation.FrameAnimationDrawable;
import com.github.pengfeizhou.animation.decode.FrameSeqDecoder;
import com.github.pengfeizhou.animation.gif.decode.GifDecoder;
import com.github.pengfeizhou.animation.loader.AssetStreamLoader;
import com.github.pengfeizhou.animation.loader.FileLoader;
import com.github.pengfeizhou.animation.loader.Loader;
import com.github.pengfeizhou.animation.loader.ResourceStreamLoader;

/**
 * @Description: GifDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifDrawable extends FrameAnimationDrawable {
    public GifDrawable(Loader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(Loader loader, FrameSeqDecoder.RenderListener listener) {
        return new GifDecoder(loader, listener);
    }


    public static GifDrawable fromAsset(Context context, String assetPath) {
        AssetStreamLoader assetStreamLoader = new AssetStreamLoader(context, assetPath);
        return new GifDrawable(assetStreamLoader);
    }

    public static GifDrawable fromFile(String filePath) {
        FileLoader fileLoader = new FileLoader(filePath);
        return new GifDrawable(fileLoader);
    }

    public static GifDrawable fromResource(Context context, int resId) {
        ResourceStreamLoader resourceStreamLoader = new ResourceStreamLoader(context, resId);
        return new GifDrawable(resourceStreamLoader);
    }
}
