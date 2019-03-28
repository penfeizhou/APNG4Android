package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 一帧图片所需信息及动画控制参数
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Frame {
    FCTLChunk fctlChunk;
    List<IDATChunk> idatChunks = new ArrayList<>();
    int sequence_number;
    List<Chunk> otherChunks = new ArrayList<>();
    private static final byte[] sPNGSignatures = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    private static final byte[] sPNGEndChunk = {0, 0, 0, 0, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82};
    Bitmap bitmap;
    Rect dstRect;
    Rect srcRect;
    byte blend_op;
    byte dispose_op;
    int delay;
    int sampleSize = 1;

    private InputStream toInputStream() {
        updateIHDR();
        List<InputStream> inputStreams = new ArrayList<>();
        InputStream signatureStream = new ByteArrayInputStream(sPNGSignatures);
        inputStreams.add(signatureStream);
        for (Chunk chunk : otherChunks) {
            inputStreams.add(chunk.toInputStream());
        }
        for (Chunk idatChunk : idatChunks) {
            inputStreams.add(idatChunk.toInputStream());
        }

        inputStreams.add(new ByteArrayInputStream(sPNGEndChunk));
        return new ChainInputStream(inputStreams.toArray(new InputStream[0]));
    }

    private void updateIHDR() {
        for (Chunk chunk : otherChunks) {
            if (chunk instanceof FakedIHDRChunk) {
                break;
            }
            if (chunk instanceof IHDRChunk) {
                int index = otherChunks.indexOf(chunk);
                otherChunks.remove(index);
                chunk = new FakedIHDRChunk((IHDRChunk) chunk, fctlChunk.width, fctlChunk.height);
                otherChunks.add(index, chunk);
                break;
            }
        }
    }

    private Bitmap createBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeStream(toInputStream(), null, options);
    }

    void prepare() {
        if (bitmap == null) {
            bitmap = createBitmap();
        }
        if (fctlChunk != null) {
            srcRect = new Rect(0, 0, fctlChunk.width / sampleSize, fctlChunk.height / sampleSize);
            dstRect = new Rect(fctlChunk.x_offset / sampleSize, fctlChunk.y_offset / sampleSize,
                    (fctlChunk.x_offset + fctlChunk.width) / sampleSize, (fctlChunk.y_offset + fctlChunk.height) / sampleSize);
            blend_op = fctlChunk.blend_op;
            dispose_op = fctlChunk.dispose_op;
            delay = fctlChunk.delay_num * 1000 / (fctlChunk.delay_den == 0 ? 100 : fctlChunk.delay_den);
            otherChunks.clear();
            idatChunks.clear();
            fctlChunk = null;
        }
    }

}
