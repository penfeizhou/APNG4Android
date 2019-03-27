package com.yupaopao.animation.apng.chunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class ChunkInputStream extends InputStream {
    private final Chunk mChunk;

    public ChunkInputStream(Chunk chunk) {
        mChunk = chunk;
    }

    protected int curOffset;


    @Override
    public int available() throws IOException {
        return mChunk.getRawDataLength() - curOffset;
    }

    @Override
    public synchronized void close() throws IOException {
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (curOffset >= 0 && curOffset <= 3) {
            return readIntByByte(mChunk.length, curOffset++);
        } else if (curOffset >= 4 && curOffset <= 7) {
            return mChunk.typeCode.getBytes()[curOffset++ - 4];
        } else if (curOffset >= mChunk.getRawDataLength()) {
            return -1;
        } else if (curOffset >= mChunk.getRawDataLength() - 4) {
            return readIntByByte(mChunk.crc, curOffset++ + 4 - mChunk.getRawDataLength());
        } else {
            return mChunk.peekData(curOffset++ - 8);
        }
    }

    private int readIntByByte(int val, int index) {
        if (index == 0) {
            return (val >> 24) & 0xff;
        } else if (index == 1) {
            return (val >> 16) & 0xff;
        } else if (index == 2) {
            return (val >> 8) & 0xff;
        } else {
            return val & 0xff;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized void reset() throws IOException {
        curOffset = 0;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("unsupported operation: skip");
    }

}
