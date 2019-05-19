package com.yupaopao.animation.gif.io;

import com.yupaopao.animation.io.ByteBufferWriter;

import java.nio.ByteBuffer;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifWriter extends ByteBufferWriter {
    public void putInt(int val) {
        putByte((byte) (val & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
        putByte((byte) ((val >> 24) & 0xff));
    }

    public ByteBuffer toByteBuffer() {
        return byteBuffer;
    }
}
