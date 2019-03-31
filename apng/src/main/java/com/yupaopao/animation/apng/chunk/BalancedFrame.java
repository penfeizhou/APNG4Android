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
class BalancedFrame extends Frame {
    List<IDATChunk> idatChunks = new ArrayList<>();
    byte[] data = null;

    BalancedFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    int toByteArray(byte[] buffer) {
        if (data != null) {
            return data.length;
        }
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
        otherChunks.clear();
        idatChunks.clear();
        data = new byte[offset];
        System.arraycopy(buffer, 0, data, 0, offset);
        return offset;
    }

    @Override
    Bitmap draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        options.inBitmap = reusedBitmap;
        int length = toByteArray(byteBuff);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, length, options);
        assert bitmap != null;
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return bitmap;
    }

    @Override
    void recycle() {
        super.recycle();
        idatChunks.clear();
    }
}
