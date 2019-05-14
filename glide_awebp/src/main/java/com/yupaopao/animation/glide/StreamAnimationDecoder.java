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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class StreamAnimationDecoder implements ResourceDecoder<InputStream, Drawable> {
    private final Context context;
    private final List<ImageHeaderParser> parsers;
    private final ArrayPool byteArrayPool;

    public StreamAnimationDecoder(
            Context context, List<ImageHeaderParser> parsers, ArrayPool arrayPool) {
        this.context = context.getApplicationContext();
        this.parsers = parsers;
        this.byteArrayPool = arrayPool;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        return !options.get(GifOptions.DISABLE_ANIMATION)
                && (ImageHeaderParserUtils.getType(parsers, source, byteArrayPool) == ImageHeaderParser.ImageType.WEBP
                || ImageHeaderParserUtils.getType(parsers, source, byteArrayPool) == ImageHeaderParser.ImageType.WEBP_A);
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final InputStream source, int width, int height, @NonNull Options options) throws IOException {
        StreamLoader streamLoader = new StreamLoader() {
            @Override
            protected InputStream getInputStream() throws IOException {
                return source;
            }
        };
        FrameAnimationDrawable drawable = new WebPDrawable(streamLoader);
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
