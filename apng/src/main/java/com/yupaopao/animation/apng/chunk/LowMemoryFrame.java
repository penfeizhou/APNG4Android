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
            inputStream.skip(8);
            Chunk chunk;
            int lastSeq = -1;
            while ((chunk = Chunk.read(inputStream, false)) != null) {
                if (chunk instanceof IENDChunk) {
                    break;
                } else if (chunk instanceof FCTLChunk) {
                    if (lastSeq >= sequence_number) {
                        break;
                    }
                    lastSeq++;
                } else if (chunk instanceof FDATChunk) {
                    if (lastSeq == sequence_number) {
                        chunkChain.add(new FakedIDATChunk((FDATChunk) chunk));
                    }
                } else if (chunk instanceof IDATChunk) {
                    if (lastSeq == sequence_number) {
                        chunkChain.add((IDATChunk) chunk);
                    }
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
        options.inBitmap = reusedBitmap;
        InputStream inputStream = toInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        assert bitmap != null;
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        if (reusedBitmap != bitmap) {
            bitmap.recycle();
        }
    }
}
