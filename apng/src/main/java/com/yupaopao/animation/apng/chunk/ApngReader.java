package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;


import com.yupaopao.animation.apng.ChainInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class ApngReader {
    private static final String TAG = ApngReader.class.getSimpleName();
    private ACTLChunk actlChunk;
    private IENDChunk iendChunk;
    private SparseArray<Frame> frames = new SparseArray<>();
    private List<Chunk> otherChunks = new ArrayList<>();
    private static final byte[] sPNGSignatures = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};

    private InputStream mSigutureStream = new ByteArrayInputStream(sPNGSignatures);

    public ApngReader(InputStream inputStream) {
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
                this.iendChunk = (IENDChunk) chunk;
                break;
            } else if (chunk instanceof ACTLChunk) {
                this.actlChunk = (ACTLChunk) chunk;
            } else if (chunk instanceof FCTLChunk) {
                lastSeq++;
                Frame frame = new Frame();
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
        InputStream inputStream1 = getFrameInputStream(0);
        if (inputStream1 != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            byte[] datas = getFrameData(0);
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(datas));
            Log.d(TAG, "decode end");
            // ApngReader apngReader = new ApngReader(getFrameInputStream(0));
            Log.d(TAG, "decode end");
        }
        Log.d(TAG, "end");
    }

    public void work() {
    }

    private byte[] getFrameData(int index) {
        Frame frame = frames.get(index);
        if (frame == null || frame.idatChunk == null) {
            Log.e(TAG, "Cannot find frame at " + index);
            return null;
        }
        int size = sPNGSignatures.length;
        for (Chunk chunk : otherChunks) {
            size += chunk.getRawDataLength();
        }
        size += frame.idatChunk.getRawDataLength();
        size += iendChunk.getRawDataLength();
        byte[] dst = new byte[size];
        int offset = 0;
        System.arraycopy(sPNGSignatures, 0, dst, offset, sPNGSignatures.length);
        offset += sPNGSignatures.length;
        for (Chunk chunk : otherChunks) {
            chunk.copy(dst, offset);
            offset += chunk.getRawDataLength();
        }
        frame.idatChunk.copy(dst, offset);
        offset += frame.idatChunk.getRawDataLength();
        iendChunk.copy(dst, offset);
        return dst;
    }

    private InputStream getFrameInputStream(int index) {
        Frame frame = frames.get(index);
        if (frame == null || frame.idatChunk == null) {
            Log.e(TAG, "Cannot find frame at " + index);
            return null;
        }
        List<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(mSigutureStream);
        for (Chunk chunk : otherChunks) {
            inputStreams.add(chunk.toInputStream());
        }
        inputStreams.add(frame.idatChunk.toInputStream());
        inputStreams.add(iendChunk.toInputStream());
        return new ChainInputStream(inputStreams.toArray(new InputStream[0]));
    }
}
