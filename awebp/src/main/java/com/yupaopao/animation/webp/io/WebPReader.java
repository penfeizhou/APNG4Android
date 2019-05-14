package com.yupaopao.animation.webp.io;

import android.text.TextUtils;

import com.yupaopao.animation.io.Reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class WebPReader implements Reader {
    private Reader reader;
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();

    protected static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    public WebPReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * @return uint16 A 16-bit, little-endian, unsigned integer.
     */
    public int getUInt16() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 2);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8;
    }

    /**
     * @return uint24 A 24-bit, little-endian, unsigned integer.
     */
    public int getUInt24() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 3);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16;
    }

    /**
     * @return uint32 A 32-bit, little-endian, unsigned integer.
     */
    public int getUInt32() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
    }

    /**
     * @return FourCC A FourCC (four-character code) is a uint32 created by concatenating four ASCII characters in little-endian order.
     */
    public int getFourCC() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
    }


    /**
     * @return 1-based An unsigned integer field storing values offset by -1. e.g., Such a field would store value 25 as 24.
     */
    public int get1Based() throws IOException {
        return getUInt24() + 1;
    }

    /**
     * @return read FourCC and match chars
     */
    public boolean matchFourCC(String chars) throws IOException {
        if (TextUtils.isEmpty(chars) || chars.length() != 4) {
            return false;
        }
        int fourCC = getFourCC();
        for (int i = 0; i < 4; i++) {
            if (((fourCC >> (i * 8)) & 0xff) != chars.charAt(i)) {
                return false;
            }
        }
        return true;
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
