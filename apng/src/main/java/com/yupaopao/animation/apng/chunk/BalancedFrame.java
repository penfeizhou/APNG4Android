package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 帧信息中保存图像原始数据，播放时实时从原始数据中解码
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class BalancedFrame extends AbstractFrame {
    List<IDATChunk> idatChunks = new ArrayList<>();

    BalancedFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    void draw(Canvas canvas, Paint paint, Bitmap reusedBitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        if (reusedBitmap != null
                && !reusedBitmap.isRecycled()) {
            reusedBitmap.reconfigure(srcRect.width(), srcRect.height(), Bitmap.Config.ARGB_8888);
            reusedBitmap.eraseColor(0);
        }
        options.inBitmap = reusedBitmap;
        Bitmap bitmap = BitmapFactory.decodeStream(toInputStream(), null, options);
        assert bitmap != null;
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        if (reusedBitmap != bitmap) {
            bitmap.recycle();
        }
    }

    @Override
    void recycle() {
        super.recycle();
        idatChunks.clear();
    }

    @Override
    List<IDATChunk> getChunkChain() {
        return idatChunks;
    }
}
