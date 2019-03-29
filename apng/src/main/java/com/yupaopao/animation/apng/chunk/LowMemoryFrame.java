package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 帧每次绘制时实时从流种读取APNG原始信息并解码，速度最慢，java内存及memory占用内存都低
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
class LowMemoryFrame extends AbstractFrame {
    LowMemoryFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    List<IDATChunk> getChunkChain() {
        List<IDATChunk> chunkChain = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = streamLoader.getInputStream();
            inputStream.skip(startPos);
            Chunk chunk;
            while ((chunk = Chunk.read(inputStream, false)) != null) {
                if (chunk instanceof IENDChunk) {
                    break;
                } else if (chunk instanceof FCTLChunk) {
                    break;
                } else if (chunk instanceof FDATChunk) {
                    chunkChain.add(new FakedIDATChunk((FDATChunk) chunk));
                } else if (chunk instanceof IDATChunk) {
                    chunkChain.add((IDATChunk) chunk);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return chunkChain;
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
}
