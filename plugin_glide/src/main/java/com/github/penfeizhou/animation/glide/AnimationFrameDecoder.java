package com.github.penfeizhou.animation.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.io.ByteBufferReader;
import com.github.penfeizhou.animation.loader.ByteBufferLoader;
import com.github.penfeizhou.animation.loader.Loader;
import com.github.penfeizhou.animation.webp.decode.WebPDecoder;
import com.github.penfeizhou.animation.webp.decode.WebPParser;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: com.github.penfeizhou.animation.glide
 * @Author: pengfei.zhou
 * @CreateDate: 2020/8/20
 */
class AnimationFrameDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {
    private final BitmapPool bitmapPool;

    AnimationFrameDecoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) throws IOException {
        return (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER) && WebPParser.isAWebP(new ByteBufferReader(source)));
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new ByteBufferLoader() {
            @Override
            public ByteBuffer getByteBuffer() {
                source.position(0);
                return source;
            }
        };
        final WebPDecoder decoder = new WebPDecoder(loader, new FrameSeqDecoder.RenderListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onRender(ByteBuffer byteBuffer) {
            }

            @Override
            public void onEnd() {

            }
        });
        Bitmap bitmap = decoder.getFrameBitmap(0);
        return BitmapResource.obtain(bitmap, bitmapPool);
    }
}
