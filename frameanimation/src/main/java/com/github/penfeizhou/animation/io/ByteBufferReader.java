package com.github.penfeizhou.animation.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferReader implements Reader {

    protected final ByteBuffer byteBuffer;

    public ByteBufferReader(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        byteBuffer.position(0);
    }

    @Override
    public long skip(long total) throws IOException {
        byteBuffer.position((int) (byteBuffer.position() + total));
        return total;
    }

    @Override
    public byte peek() throws IOException {
        return byteBuffer.get();
    }

    @Override
    public void reset() throws IOException {
        byteBuffer.position(0);
    }

    @Override
    public int position() {
        return byteBuffer.position();
    }

    @Override
    public int read(byte[] buffer, int start, int byteCount) throws IOException {
        byteBuffer.get(buffer, start, byteCount);
        return byteCount;
    }

    @Override
    public int available() throws IOException {
        return byteBuffer.limit() - byteBuffer.position();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public InputStream toInputStream() throws IOException {
        return new ByteArrayInputStream(byteBuffer.array());
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
