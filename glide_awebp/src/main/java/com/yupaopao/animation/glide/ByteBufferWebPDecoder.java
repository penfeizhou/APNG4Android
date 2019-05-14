package com.yupaopao.animation.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.loader.StreamLoader;
import com.yupaopao.animation.webp.WebPDrawable;
import com.yupaopao.animation.webp.decode.WebPParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        return false;
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        StreamLoader streamLoader = new StreamLoader() {
            @Override
            protected InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(source.array());
            }
        };
        FrameAnimationDrawable drawable = new WebPDrawable(streamLoader);
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
