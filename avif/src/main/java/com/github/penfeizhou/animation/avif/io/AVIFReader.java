package com.github.penfeizhou.animation.avif.io;

import android.text.TextUtils;

import com.github.penfeizhou.animation.io.FilterReader;
import com.github.penfeizhou.animation.io.Reader;

import java.io.IOException;
import java.util.List;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2023-09-07
 */
public class AVIFReader extends FilterReader {
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();

    protected static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    public AVIFReader(Reader reader) {
        super(reader);
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

    public long readUInt64() throws IOException {
        int high = readUInt32();
        int low = readUInt32();
        return (long) high << 32 | low;
    }

    public int readUInt32() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[3] & 0xFF |
                (buf[2] & 0xFF) << 8 |
                (buf[1] & 0xFF) << 16 |
                (buf[0] & 0xFF) << 24;
    }

    public int readUInt8() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 1);
        return buf[0] & 0xFF;
    }

    public short readUInt16() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 2);
        return (short) (buf[1] & 0xFF |
                (buf[0] & 0xFF) << 8);
    }

    public String readString() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte b;
        while ((b = peek()) != 0) {
            stringBuilder.append(b);
        }
        return stringBuilder.toString();
    }

    public int readUInt24() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 3);
        return buf[2] & 0xff | (buf[1] & 0xff) << 8 | (buf[0] & 0xff) << 16;
    }

    public int readFourCC() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
    }

    public String readString(int length) throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return new String(buf, 0, 4);
    }
}
