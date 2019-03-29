package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 一帧图片所需信息及动画控制参数
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Frame extends AbstractFrame {
    List<IDATChunk> idatChunks = new ArrayList<>();
    Bitmap bitmap;

    Frame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeStream(toInputStream(), null, options);
            otherChunks.clear();
            idatChunks.clear();
        }
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
    }

    @Override
    void recycle() {
        super.recycle();
        idatChunks.clear();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    List<IDATChunk> getChunkChain() {
        return idatChunks;
    }
}
