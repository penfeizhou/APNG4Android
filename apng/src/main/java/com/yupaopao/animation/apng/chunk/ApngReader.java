package com.yupaopao.animation.apng.chunk;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class ApngReader {
    private static final String TAG = ApngReader.class.getSimpleName();

    public ApngReader(InputStream inputStream) {
        byte[] sigBytes = new byte[8];
        try {
            inputStream.read(sigBytes);
            String signature = new String(sigBytes);
            Log.d(TAG, "read signature:" + signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Chunk> chunks = new ArrayList<>();
        Chunk chunk;
        while ((chunk = Chunk.read(inputStream)) != null) {
            chunks.add(chunk);
            if (chunk instanceof IENDChunk) {
                break;
            }
        }
        Log.d(TAG, "end");
    }

    public void work() {
    }
}
