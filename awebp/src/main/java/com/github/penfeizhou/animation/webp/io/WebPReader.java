package com.github.penfeizhou.animation.webp.io;

import android.text.TextUtils;

import com.github.penfeizhou.animation.io.FilterReader;
import com.github.penfeizhou.animation.io.Reader;

import java.io.IOException;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class WebPReader extends FilterReader {
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
        super(reader);
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
}
