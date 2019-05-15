package com.yupaopao.animation.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.yupaopao.animation.io.StreamReader;
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
                && WebPParser.isAWebP(new StreamReader(source));
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final InputStream source, int width, int height, @NonNull Options options) throws IOException {
        StreamLoader streamLoader = new StreamLoader() {
            @Override
            protected InputStream getInputStream() {
                return source;
            }
        };
        Drawable drawable = new WebPDrawable(streamLoader);
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
