package com.yupaopao.animation.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.decode.APNGParser;
import com.yupaopao.animation.gif.GifDrawable;
import com.yupaopao.animation.gif.decode.GifParser;
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
        return (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER) && WebPParser.isAWebP(new ByteBufferReader(source)))
                || (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER) && APNGParser.isAPNG(new ByteBufferReader(source)))
                || (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_GIF_DECODER) && GifParser.isGif(new ByteBufferReader(source)));
    }

    @Nullable
    @Override
    public Resource<Drawable> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new ByteBufferLoader() {
            @Override
            public ByteBuffer getByteBuffer() {
                source.position(0);
                return source;
            }
        };
        FrameAnimationDrawable drawable;
        if (WebPParser.isAWebP(new ByteBufferReader(source))) {
            drawable = new WebPDrawable(loader);
        } else if (APNGParser.isAPNG(new ByteBufferReader(source))) {
            drawable = new APNGDrawable(loader);
        } else if (GifParser.isGif(new ByteBufferReader(source))) {
            drawable = new GifDrawable(loader);
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
                ((FrameAnimationDrawable) drawable).stop();
            }
        };
    }
}
