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
import com.yupaopao.animation.io.ByteBufferReader;
import com.yupaopao.animation.loader.ByteBufferLoader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.webp.WebPDrawable;
import com.yupaopao.animation.webp.decode.WebPParser;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: ByteBufferAnimationDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferAnimationDecoder implements ResourceDecoder<ByteBuffer, Drawable> {

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return !options.get(GifOptions.DISABLE_ANIMATION)
                && (WebPParser.isAWebP(new ByteBufferReader(source))
                || APNGParser.isAPNG(new ByteBufferReader(source))
        );
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new ByteBufferLoader() {
            @Override
            public ByteBuffer getByteBuffer() {
                return source;
            }
        };
        FrameAnimationDrawable drawable;
        if (WebPParser.isAWebP(new ByteBufferReader(source))) {
            drawable = new WebPDrawable(loader);
        } else if (APNGParser.isAPNG(new ByteBufferReader(source))) {
            drawable = new APNGDrawable(loader);
        } else {
            return null;
        }
        return new DrawableResource<Drawable>(drawable) {
            @NonNull
            @Override
            public Class<Drawable> getResourceClass() {
                return Drawable.class;
            }

            @Override
            public int getSize() {
                return source.limit();
            }

            @Override
            public void recycle() {
            }
        };
    }
}
