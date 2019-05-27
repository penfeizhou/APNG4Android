package com.yupaopao.animation.gif;

import android.content.Context;

import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.gif.decode.GifDecoder;
import com.yupaopao.animation.loader.AssetStreamLoader;
import com.yupaopao.animation.loader.FileLoader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.loader.ResourceStreamLoader;

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
