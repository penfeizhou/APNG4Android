package com.yupaopao.animation.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public class ByteBufferReader implements Reader {


    @Override
    public long skip(long total) throws IOException {
        return 0;
    }

    @Override
    public byte peek() throws IOException {
        return 0;
    }

    @Override
    public void reset() throws IOException {

    }

    @Override
    public int position() {
        return 0;
    }

    @Override
    public int read(byte[] buffer, int start, int byteCount) throws IOException {
        return 0;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public InputStream toInputStream() throws IOException {
        return null;
    }
}
