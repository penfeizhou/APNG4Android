package com.yupaopao.animation.apng.chunk;

import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG解码器
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class ApngDecoder {
    private static final String TAG = ApngDecoder.class.getSimpleName();
    private ACTLChunk actlChunk;
    private SparseArray<Frame> frames = new SparseArray<>();
    private List<Chunk> otherChunks = new ArrayList<>();

    public ApngDecoder(InputStream inputStream) {
        byte[] sigBytes = new byte[8];
        try {
            inputStream.read(sigBytes);
            String signature = new String(sigBytes);
            Log.d(TAG, "read signature:" + signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Chunk chunk;
        int lastSeq = -1;
        while ((chunk = Chunk.read(inputStream)) != null) {
            if (chunk instanceof IENDChunk) {
                break;
            } else if (chunk instanceof ACTLChunk) {
                this.actlChunk = (ACTLChunk) chunk;
            } else if (chunk instanceof FCTLChunk) {
                lastSeq++;
                Frame frame = new Frame();
                frame.otherChunks.addAll(otherChunks);
                frames.put(lastSeq, frame);
                frame.sequence_number = lastSeq;
                frame.fctlChunk = (FCTLChunk) chunk;
            } else if (chunk instanceof FDATChunk) {
                Frame frame = frames.get(lastSeq);
                if (frame != null) {
                    frame.idatChunk = new FakedIDATChunk((FDATChunk) chunk);
                }
            } else if (chunk instanceof IDATChunk) {
                Frame frame = frames.get(lastSeq);
                if (frame != null && frame.idatChunk == null) {
                    frame.idatChunk = (IDATChunk) chunk;
                }
            } else {
                otherChunks.add(chunk);
            }
        }
    }

    public Frame getFrame(int index) {
        return frames.get(index);
    }

}
