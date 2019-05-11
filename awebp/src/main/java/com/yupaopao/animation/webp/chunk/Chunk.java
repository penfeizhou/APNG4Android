package com.yupaopao.animation.webp.chunk;

import android.text.TextUtils;

import com.yupaopao.animation.webp.reader.Reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Link {https://developers.google.com/speed/webp/docs/riff_container#extended_file_format}
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Chunk {
    int size;
    int fourCC;
    byte[] data;
    int crc;
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();

    void parse() {
    }

    static Chunk read(InputStream inputStream, boolean skipData) throws IOException {
        if (Thread.interrupted()) {
            return null;
        }
        int type = readIntFromInputStream(inputStream);

        int length = readIntFromInputStream(inputStream);
        Chunk chunk = newInstance(type);
        chunk.fourCC = type;
        chunk.size = length;
        if (skipData && (chunk instanceof IDATChunk || chunk instanceof FDATChunk)) {
            inputStream.skip(length + 4);
        } else {
            chunk.data = new byte[(int) chunk.size];
            inputStream.read(chunk.data);
            chunk.crc = readIntFromInputStream(inputStream);
            chunk.parse();
        }
        return chunk;
    }

    private static Chunk newInstance(int typeCode) {
        Chunk chunk;
        switch (typeCode) {
            default:
                chunk = new Chunk();
                break;
        }
        return chunk;
    }

    private static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    static int readIntFromInputStream(InputStream inputStream) throws IOException {
        inputStream.read(ensureBytes());
        return byteArrayToInt(ensureBytes());
    }

    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    int getRawDataLength() {
        return (int) (size + 12);
    }

    int peekData(int i) {
        return data[i];
    }

    void copy(byte[] dst, int offset) {
        copyLength(dst, offset);
        offset += 4;
        copyTypeCode(dst, offset);
        offset += 4;
        if (data != null && data.length > 0) {
            copyData(dst, offset);
            offset += size;
        }
        copyCrc(dst, offset);
    }

    void copyLength(byte[] dst, int offset) {
        dst[offset] = readIntByByte((int) size, 0);
        dst[offset + 1] = readIntByByte((int) size, 1);
        dst[offset + 2] = readIntByByte((int) size, 2);
        dst[offset + 3] = readIntByByte((int) size, 3);
    }

    void copyTypeCode(byte[] dst, int offset) {
        dst[offset] = readIntByByte(fourCC, 0);
        dst[offset + 1] = readIntByByte(fourCC, 1);
        dst[offset + 2] = readIntByByte(fourCC, 2);
        dst[offset + 3] = readIntByByte(fourCC, 3);
    }

    void copyData(byte[] dst, int offset) {
        System.arraycopy(data, 0, dst, offset, (int) size);
    }

    void copyCrc(byte[] dst, int offset) {
        dst[offset] = readIntByByte(crc, 0);
        dst[offset + 1] = readIntByByte(crc, 1);
        dst[offset + 2] = readIntByByte(crc, 2);
        dst[offset + 3] = readIntByByte(crc, 3);
    }


    static byte readIntByByte(int val, int index) {
        if (index == 0) {
            return (byte) ((val >> 24) & 0xff);
        } else if (index == 1) {
            return (byte) ((val >> 16) & 0xff);
        } else if (index == 2) {
            return (byte) ((val >> 8) & 0xff);
        } else {
            return (byte) (val & 0xff);
        }
    }
}
