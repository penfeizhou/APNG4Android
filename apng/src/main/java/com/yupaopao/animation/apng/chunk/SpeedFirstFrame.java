package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

/**
 * @Description: 帧信息中保存图像原始数据，缓存解码后的Bitmap，速度最快，java内存和native内存都很高
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class SpeedFirstFrame extends BalancedFrame {
    Bitmap bitmap;

    SpeedFirstFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    void draw(Canvas canvas, Paint paint, Bitmap reusedBitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
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
