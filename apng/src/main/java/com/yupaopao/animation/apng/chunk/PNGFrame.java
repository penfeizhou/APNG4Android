package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @Description: PNG帧
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
class PNGFrame extends Frame {
    private BitmapFactory.Options options = new BitmapFactory.Options();

    PNGFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    Bitmap draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff) {
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(streamLoader.getInputStream());
            assert bitmap != null;
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
