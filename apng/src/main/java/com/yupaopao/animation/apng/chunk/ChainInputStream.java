package com.yupaopao.animation.apng.chunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class ChainInputStream extends InputStream {
    protected InputStream[] streams;
    protected int curOffset;
    protected int curs;

    public ChainInputStream(InputStream... inputStreams) {
        streams = inputStreams;
        curs = 0;
    }

    @Override
    public int available() throws IOException {
        int c = 0;
        for (int i = curs, n = streams.length; i < n; i++) {
            InputStream s = streams[i];
            int a = s.available();
            if (a <= 0)
                return 0;
            c += a;
        }
        return c - curOffset;
    }

    @Override
    public synchronized void close() throws IOException {
        for (InputStream s : streams) {
            s.close();
        }
        curOffset = 0;
        curs = 0;
    }

    @Override
    public synchronized void mark(int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        InputStream s = streams[curs];
        int r = s.read();
        if (r < 0) {
            if (curs < streams.length - 1) {
                curs++;
                curOffset = 0;
                return read();
            } else {
                return r;
            }
        } else {
            ++curOffset;
            return r;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return readInline(b, off, len);
    }

    private int readInline(byte[] b, int off, int len) throws IOException {
        InputStream s = streams[curs];
        int r = s.read(b, off, len);
        if (r < 0) {
            if (curs < streams.length - 1) {
                curs++;
                curOffset = 0;
                return readInline(b, off, len);
            } else {
                return r;
            }
        } else {
            curOffset += r;
            if (r < len) {
                return r + readInline(b, off + r, len - r);
            }
            return r;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized void reset() throws IOException {
        for (InputStream s : streams) {
            s.reset();
        }
        curOffset = 0;
        curs = 0;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("unsupported operation: skip");
    }
}
