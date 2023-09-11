package com.github.penfeizhou.animation.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.github.penfeizhou.animation.apng.decode.APNGDecoder;
import com.github.penfeizhou.animation.apng.decode.APNGParser;
import com.github.penfeizhou.animation.avif.decode.AVIFDecoder;
import com.github.penfeizhou.animation.avif.decode.AVIFParser;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.gif.decode.GifDecoder;
import com.github.penfeizhou.animation.gif.decode.GifParser;
import com.github.penfeizhou.animation.io.ByteBufferReader;
import com.github.penfeizhou.animation.loader.ByteBufferLoader;
import com.github.penfeizhou.animation.loader.Loader;
import com.github.penfeizhou.animation.webp.decode.WebPDecoder;
import com.github.penfeizhou.animation.webp.decode.WebPParser;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: ByteBufferAnimationDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferAnimationDecoder implements ResourceDecoder<ByteBuffer, FrameSeqDecoder> {

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER) && WebPParser.isAWebP(new ByteBufferReader(source)))
                || (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER) && APNGParser.isAPNG(new ByteBufferReader(source)))
                || (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_GIF_DECODER) && GifParser.isGif(new ByteBufferReader(source)))
                || (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_AVIF_DECODER) && AVIFParser.isAVIF(new ByteBufferReader(source)));
    }

    @Nullable
    @Override
    public Resource<FrameSeqDecoder> decode(@NonNull final ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        Loader loader = new ByteBufferLoader() {
            @Override
            public ByteBuffer getByteBuffer() {
                source.position(0);
                return source;
            }
        };
        final FrameSeqDecoder decoder;
        if (WebPParser.isAWebP(new ByteBufferReader(source))) {
            decoder = new WebPDecoder(loader, null);
        } else if (APNGParser.isAPNG(new ByteBufferReader(source))) {
            decoder = new APNGDecoder(loader, null);
        } else if (GifParser.isGif(new ByteBufferReader(source))) {
            decoder = new GifDecoder(loader, null);
        } else if (AVIFParser.isAVIF(new ByteBufferReader(source))) {
            decoder = new AVIFDecoder(loader, null);
        } else {
            return null;
        }
        return new FrameSeqDecoderResource(decoder, source.limit());
    }

    private static class FrameSeqDecoderResource implements Resource<FrameSeqDecoder> {
        private final FrameSeqDecoder decoder;
        private final int size;

        FrameSeqDecoderResource(FrameSeqDecoder decoder, int size) {
            this.decoder = decoder;
            this.size = size;
        }

        @NonNull
        @Override
        public Class<FrameSeqDecoder> getResourceClass() {
            return FrameSeqDecoder.class;
        }

        @NonNull
        @Override
        public FrameSeqDecoder get() {
            return this.decoder;
        }

        @Override
        public int getSize() {
            return this.size;
        }

        @Override
        public void recycle() {
            this.decoder.stop();
        }
    }
}
