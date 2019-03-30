package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

/**
 * @Description: 帧信息中缓存解码后的Bitmap，速度最快，但native占用内存高
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
class SpeedFirstFrame extends BalancedFrame {
    Bitmap bitmap;

    SpeedFirstFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    int toByteArray(byte[] buffer) {
        int offset = 0;
        System.arraycopy(sPNGSignatures, 0, buffer, offset, sPNGSignatures.length);
        offset += sPNGSignatures.length;

        ihdrChunk.copy(buffer, offset);
        offset += ihdrChunk.getRawDataLength();

        for (Chunk chunk : otherChunks) {
            chunk.copy(buffer, offset);
            offset += chunk.getRawDataLength();
        }
        for (Chunk idatChunk : idatChunks) {
            idatChunk.copy(buffer, offset);
            offset += idatChunk.getRawDataLength();
        }
        System.arraycopy(sPNGEndChunk, 0, buffer, offset, sPNGEndChunk.length);
        offset += sPNGEndChunk.length;
        return offset;
    }

    @Override
    Bitmap draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff) {
        if (bitmap == null || bitmap.isRecycled()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            int length = toByteArray(byteBuff);
            bitmap = BitmapFactory.decodeByteArray(byteBuff, 0, length);
            otherChunks.clear();
            idatChunks.clear();
        }
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return null;
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
}
