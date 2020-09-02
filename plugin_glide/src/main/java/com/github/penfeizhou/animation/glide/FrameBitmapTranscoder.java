package com.github.penfeizhou.animation.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;

import java.io.IOException;

/**
 * @Description: com.github.penfeizhou.animation.glide
 * @Author: pengfei.zhou
 * @CreateDate: 2020/8/21
 */
class FrameBitmapTranscoder implements ResourceTranscoder<FrameSeqDecoder, Bitmap> {
    private final BitmapPool bitmapPool;

    FrameBitmapTranscoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Nullable
    @Override
    public Resource<Bitmap> transcode(@NonNull Resource<FrameSeqDecoder> toTranscode, @NonNull Options options) {
        FrameSeqDecoder frameSeqDecoder = toTranscode.get();
        try {
            Bitmap bitmap = frameSeqDecoder.getFrameBitmap(0);
            return BitmapResource.obtain(bitmap, bitmapPool);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
