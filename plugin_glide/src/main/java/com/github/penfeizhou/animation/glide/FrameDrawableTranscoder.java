package com.github.penfeizhou.animation.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.apng.decode.APNGDecoder;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.gif.GifDrawable;
import com.github.penfeizhou.animation.gif.decode.GifDecoder;
import com.github.penfeizhou.animation.webp.WebPDrawable;
import com.github.penfeizhou.animation.webp.decode.WebPDecoder;

/**
 * @Description: com.github.penfeizhou.animation.glide
 * @Author: pengfei.zhou
 * @CreateDate: 2020/8/21
 */
class FrameDrawableTranscoder implements ResourceTranscoder<FrameSeqDecoder, Drawable> {

    @Nullable
    @Override
    public Resource<Drawable> transcode(@NonNull Resource<FrameSeqDecoder> toTranscode, @NonNull Options options) {
        FrameSeqDecoder frameSeqDecoder = toTranscode.get();
        if (frameSeqDecoder instanceof APNGDecoder) {
            APNGDrawable apngDrawable = new APNGDrawable((APNGDecoder) frameSeqDecoder);
            apngDrawable.setAutoPlay(false);
            return new DrawableResource<Drawable>(apngDrawable) {
                @NonNull
                @Override
                public Class<Drawable> getResourceClass() {
                    return Drawable.class;
                }

                @Override
                public int getSize() {
                    return 0;
                }

                @Override
                public void recycle() {
                }

                @Override
                public void initialize() {
                    super.initialize();
                }
            };
        } else if (frameSeqDecoder instanceof WebPDecoder) {
            WebPDrawable webPDrawable = new WebPDrawable((WebPDecoder) frameSeqDecoder);
            webPDrawable.setAutoPlay(false);
            return new DrawableResource<Drawable>(webPDrawable) {
                @NonNull
                @Override
                public Class<Drawable> getResourceClass() {
                    return Drawable.class;
                }

                @Override
                public int getSize() {
                    return 0;
                }

                @Override
                public void recycle() {
                }

                @Override
                public void initialize() {
                    super.initialize();
                }
            };
        } else if (frameSeqDecoder instanceof GifDecoder) {
            GifDrawable gifDrawable = new GifDrawable((GifDecoder) frameSeqDecoder);
            gifDrawable.setAutoPlay(false);
            return new DrawableResource<Drawable>(gifDrawable) {
                @NonNull
                @Override
                public Class<Drawable> getResourceClass() {
                    return Drawable.class;
                }

                @Override
                public int getSize() {
                    return 0;
                }

                @Override
                public void recycle() {
                }

                @Override
                public void initialize() {
                    super.initialize();
                }
            };
        } else {
            return null;
        }
    }
}
