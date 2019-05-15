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
import com.yupaopao.animation.io.ByteBufferReader;
import com.yupaopao.animation.loader.ByteBufferLoader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.webp.WebPDrawable;
import com.yupaopao.animation.webp.decode.WebPParser;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: ByteBufferWebPDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferWebPDecoder implements ResourceDecoder<ByteBuffer, Drawable> {

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return !options.get(GifOptions.DISABLE_ANIMATION)
                && WebPParser.isAWebP(new ByteBufferReader(source));
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
        FrameAnimationDrawable drawable = new WebPDrawable(loader);
        final int size = source.limit();
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
