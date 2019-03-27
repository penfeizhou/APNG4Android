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
        int ret;
        if (curOffset >= 0 && curOffset <= 3) {
            ret = readIntByByte(mChunk.length, curOffset);
        } else if (curOffset >= 4 && curOffset <= 7) {
            ret = mChunk.typeCode.getBytes()[curOffset - 4];
        } else if (curOffset >= mChunk.getRawDataLength()) {
            return -1;
        } else if (curOffset >= mChunk.getRawDataLength() - 4) {
            ret = readIntByByte(mChunk.crc, curOffset + 4 - mChunk.getRawDataLength());
        } else {
            ret = mChunk.peekData(curOffset - 8);
        }
        curOffset++;
        return ret;
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
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        if (mChunk.getRawDataLength() <= curOffset) {
            return -1;
        }
        int left = len;
        if (curOffset < 8) {
            int consumed = Math.min(8 - curOffset, left);
            for (int i = 0; i < consumed; i++) {
                b[off + i] = (byte) read();
            }
            left -= consumed;
            off += consumed;
        }
        if (left > 0) {
            int startPos = curOffset - 8;
            if (mChunk.data != null && mChunk.length > 0 && startPos < mChunk.length) {
                int consumed = Math.min(mChunk.length - startPos, left);
                System.arraycopy(mChunk.data, startPos, b, off, consumed);
                left -= consumed;
                off += consumed;
                curOffset += consumed;
            }
        }
        if (left > 0) {
            int consumed = Math.min(4, left);
            for (int i = 0; i < consumed; i++) {
                b[off + i] = (byte) read();
            }
            left -= consumed;
        }
        return len - left;
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
