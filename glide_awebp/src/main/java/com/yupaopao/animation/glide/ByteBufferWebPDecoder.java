package com.yupaopao.animation.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.io.ByteBufferReader;
import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.loader.Loader;
import com.yupaopao.animation.webp.WebPDrawable;
import com.yupaopao.animation.webp.decode.WebPParser;
import com.yupaopao.animation.webp.io.WebPReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferWebPDecoder implements ResourceDecoder<ByteBuffer, Drawable> {

    private final Context context;
    private final List<ImageHeaderParser> parsers;
    private final ArrayPool byteArrayPool;

    public ByteBufferWebPDecoder(
            Context context, List<ImageHeaderParser> parsers, ArrayPool arrayPool) {
        this.context = context.getApplicationContext();
        this.parsers = parsers;
        this.byteArrayPool = arrayPool;
    }

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) throws IOException {
        return !options.get(GifOptions.DISABLE_ANIMATION)
                && WebPParser.isAWebP(new WebPReader(new ByteBufferReader(source)));
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new Loader() {
            @Override
            public Reader obtain() throws IOException {
                Reader reader = new ByteBufferReader(source);
                return reader;
            }

            @Override
            public void release() throws IOException {

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
