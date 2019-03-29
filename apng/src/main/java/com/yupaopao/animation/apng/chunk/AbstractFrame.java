package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
abstract class AbstractFrame {
    static final String TAG = AbstractFrame.class.getSimpleName();
    final APNGStreamLoader streamLoader;
    public int startPos;
    public int endPos;
    int sequence_number;
    static final byte[] sPNGSignatures = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    static final byte[] sPNGEndChunk = {0, 0, 0, 0, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82};
    final Rect dstRect;
    final Rect srcRect;
    final byte blend_op;
    final byte dispose_op;
    final int delay;
    final FCTLChunk fctlChunk;
    final FakedIHDRChunk ihdrChunk;
    final List<Chunk> otherChunks;
    final int sampleSize;

    AbstractFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk,
                  List<Chunk> otherChunks,
                  int sampleSize, APNGStreamLoader streamLoader) {
        this.ihdrChunk = new FakedIHDRChunk(ihdrChunk, fctlChunk.width, fctlChunk.height);
        this.fctlChunk = fctlChunk;
        this.otherChunks = otherChunks;
        this.sampleSize = sampleSize;
        this.streamLoader = streamLoader;

        blend_op = fctlChunk.blend_op;
        dispose_op = fctlChunk.dispose_op;
        delay = fctlChunk.delay_num * 1000 / (fctlChunk.delay_den == 0 ? 100 : fctlChunk.delay_den);
        srcRect = new Rect(0, 0, fctlChunk.width / sampleSize, fctlChunk.height / sampleSize);
        dstRect = new Rect(fctlChunk.x_offset / sampleSize, fctlChunk.y_offset / sampleSize,
                (fctlChunk.x_offset + fctlChunk.width) / sampleSize, (fctlChunk.y_offset + fctlChunk.height) / sampleSize);
    }

    InputStream toInputStream() {
        List<InputStream> inputStreams = new ArrayList<>();
        InputStream signatureStream = new ByteArrayInputStream(sPNGSignatures);
        inputStreams.add(signatureStream);
        List<IDATChunk> chunkChain = getChunkChain();
        inputStreams.add(ihdrChunk.toInputStream());
        for (Chunk chunk : otherChunks) {
            inputStreams.add(chunk.toInputStream());
        }
        for (Chunk chunk : chunkChain) {
            inputStreams.add(chunk.toInputStream());
        }
        inputStreams.add(new ByteArrayInputStream(sPNGEndChunk));
        return new ChainInputStream(inputStreams.toArray(new InputStream[0]));
    }

    void recycle() {

    }

    abstract List<IDATChunk> getChunkChain();

    abstract void draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff);
}
