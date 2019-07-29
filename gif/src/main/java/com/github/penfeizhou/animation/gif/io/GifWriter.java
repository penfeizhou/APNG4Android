package com.github.penfeizhou.animation.gif.io;

import com.github.penfeizhou.animation.io.Writer;

import java.nio.IntBuffer;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifWriter implements Writer {

    protected IntBuffer intBuffer;

    public GifWriter() {
        reset(10 * 1024);
    }

    @Override
    public void putByte(byte b) {
    }

    @Override
    public void putBytes(byte[] b) {
    }

    @Override
    public int position() {
        return intBuffer.position();
    }

    @Override
    public void skip(int length) {
        intBuffer.position(length + position());
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public void reset(int size) {
        if (intBuffer == null || size > intBuffer.capacity()) {
            intBuffer = IntBuffer.allocate(size);
        }
        intBuffer.clear();
        intBuffer.limit(size);
        intBuffer.position(0);
    }

    public int[] asIntArray() {
        return intBuffer.array();
    }

    public IntBuffer asBuffer() {
        return intBuffer;
    }
}
