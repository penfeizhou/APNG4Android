package com.yupaopao.animation.apng.io;

import android.text.TextUtils;

import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.io.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class APNGReader implements Reader {
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

    public APNGReader(Reader in) {
        this.reader = in;
    }

    public int readInt() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[3] & 0xFF |
                (buf[2] & 0xFF) << 8 |
                (buf[1] & 0xFF) << 16 |
                (buf[0] & 0xFF) << 24;
    }

    public short readShort() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 2);
        return (short) (buf[1] & 0xFF |
                (buf[0] & 0xFF) << 8);
    }

    /**
     * @return read FourCC and match chars
     */
    public boolean matchFourCC(String chars) throws IOException {
        if (TextUtils.isEmpty(chars) || chars.length() != 4) {
            return false;
        }
        int fourCC = readFourCC();
        for (int i = 0; i < 4; i++) {
            if (((fourCC >> (i * 8)) & 0xff) != chars.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public int readFourCC() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
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
