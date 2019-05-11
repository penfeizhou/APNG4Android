package com.yupaopao.animation.webp.chunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Link {https://developers.google.com/speed/webp/docs/riff_container#extended_file_format}
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Chunk {
    int length;
    int type;
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
        chunk.type = type;
        chunk.length = length;
        if (skipData && (chunk instanceof IDATChunk || chunk instanceof FDATChunk)) {
            inputStream.skip(length + 4);
        } else {
            chunk.data = new byte[chunk.length];
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
        return length + 12;
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
            offset += length;
        }
        copyCrc(dst, offset);
    }

    void copyLength(byte[] dst, int offset) {
        dst[offset] = readIntByByte(length, 0);
        dst[offset + 1] = readIntByByte(length, 1);
        dst[offset + 2] = readIntByByte(length, 2);
        dst[offset + 3] = readIntByByte(length, 3);
    }

    void copyTypeCode(byte[] dst, int offset) {
        dst[offset] = readIntByByte(type, 0);
        dst[offset + 1] = readIntByByte(type, 1);
        dst[offset + 2] = readIntByByte(type, 2);
        dst[offset + 3] = readIntByByte(type, 3);
    }

    void copyData(byte[] dst, int offset) {
        System.arraycopy(data, 0, dst, offset, length);
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
