package com.github.penfeizhou.animation.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class StreamReader extends FilterInputStream implements Reader {
    private int position;

    public StreamReader(InputStream in) {
        super(in);
        try {
            in.reset();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    @Override
    public byte peek() throws IOException {
        byte ret = (byte) read();
        position++;
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret = super.read(b, off, len);
        position += Math.max(0, ret);
        return ret;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        position = 0;
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = n;

        while (toSkip > 0) {
            long skipped = super.skip(toSkip);
            if (skipped > 0) {
                toSkip -= skipped;
            } else {
                // Skip has no specific contract as to what happens when you reach the end of
                // the stream. To differentiate between temporarily not having more data and
                // having finished the stream, we read a single byte when we fail to skip any
                // amount of data.
                int testEofByte = super.read();
                if (testEofByte == -1) {
                    break;
                } else {
                    toSkip--;
                }
            }
        }

        position += n - toSkip;
        return n - toSkip;
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public InputStream toInputStream() throws IOException {
        return this;
    }
}
