package com.yupaopao.animation.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.decode.APNGParser;
import com.yupaopao.animation.gif.GifDrawable;
import com.yupaopao.animation.gif.decode.GifParser;
import com.yupaopao.animation.io.ByteBufferReader;
import com.yupaopao.animation.io.StreamReader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.loader.StreamLoader;
import com.yupaopao.animation.webp.WebPDrawable;
import com.yupaopao.animation.webp.decode.WebPParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: StreamAnimationDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class StreamAnimationDecoder implements ResourceDecoder<InputStream, Drawable> {

    public StreamAnimationDecoder() {
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) {
        return !options.get(GifOptions.DISABLE_ANIMATION)
                && (WebPParser.isAWebP(new StreamReader(source))
                || APNGParser.isAPNG(new StreamReader(source))
                || GifParser.isGif(new StreamReader(source)));
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final InputStream source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new StreamLoader() {
            @Override
            protected InputStream getInputStream() {
                try {
                    source.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return source;
            }
        };
        FrameAnimationDrawable drawable;
        if (WebPParser.isAWebP(new StreamReader(source))) {
            drawable = new WebPDrawable(loader);
        } else if (APNGParser.isAPNG(new StreamReader(source))) {
            drawable = new APNGDrawable(loader);
        } else if (GifParser.isGif(new StreamReader(source))) {
            drawable = new GifDrawable(loader);
        } else {
            return null;
        }
        final int size = source.available();
        return new DrawableResource<Drawable>(drawable) {
            @NonNull
            @Override
            public Class<Drawable> getResourceClass() {
                return Drawable.class;
            }

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public void recycle() {
            }
        };
    }
}
