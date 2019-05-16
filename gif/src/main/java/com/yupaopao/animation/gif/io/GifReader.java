package com.yupaopao.animation.gif.io;

import com.yupaopao.animation.io.Reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifReader implements Reader {
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();

    private Reader reader;

    protected static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    public GifReader(Reader in) {
        this.reader = in;
    }


    @Override
    public long skip(long total) throws IOException {
        return reader.skip(total);
    }

    @Override
    public byte peek() throws IOException {
        return reader.peek();
    }

    @Override
    public void reset() throws IOException {
        reader.reset();
    }

    @Override
    public int position() {
        return reader.position();
    }

    @Override
    public int read(byte[] buffer, int start, int byteCount) throws IOException {
        return reader.read(buffer, start, byteCount);
    }

    @Override
    public int available() throws IOException {
        return reader.available();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public InputStream toInputStream() throws IOException {
        reset();
        return reader.toInputStream();
    }
}
