package com.yupaopao.animation.webp.decode;

import android.text.TextUtils;

import com.yupaopao.animation.webp.reader.Reader;

import java.io.IOException;

/**
 * @Description: BaseChunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class BaseChunk {
    public int chunkFourCC;
    public int payloadSize;

    protected static int fourCCToInt(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            return 0xbadeffff;
        }
        return (fourCC.charAt(0) & 0xff)
                | (fourCC.charAt(1) & 0xff) << 8
                | (fourCC.charAt(2) & 0xff) << 16
                | (fourCC.charAt(3) & 0xff) << 24
                ;
    }

    static byte[] fourCCToByte(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            return new byte[4];
        }
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (fourCC.charAt(0) & 0xff);
        bytes[1] = (byte) (fourCC.charAt(1) & 0xff);
        bytes[2] = (byte) (fourCC.charAt(2) & 0xff);
        bytes[3] = (byte) (fourCC.charAt(3) & 0xff);
        return bytes;
    }

    static byte[] uInt16ToByte(int val) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (val & 0xff);
        bytes[1] = (byte) ((val >> 8) & 0xff);
        return bytes;
    }

    static byte[] uInt24ToByte(int val) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (val & 0xff);
        bytes[1] = (byte) ((val >> 8) & 0xff);
        bytes[2] = (byte) ((val >> 16) & 0xff);
        return bytes;
    }

    static byte[] uInt32ToByte(int val) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (val & 0xff);
        bytes[1] = (byte) ((val >> 8) & 0xff);
        bytes[2] = (byte) ((val >> 16) & 0xff);
        bytes[3] = (byte) ((val >> 24) & 0xff);
        return bytes;
    }

    static byte[] oneBasedToByte(int val) {
        return uInt24ToByte(val - 1);
    }

    final void parse(Reader reader) throws IOException {
        int available = reader.available();
        innerParse(reader);
        int offset = available - reader.available();
        /**
         * Chunk Payload: Chunk Size bytes
         * The data payload. If Chunk Size is odd, a single padding byte -- that SHOULD be 0 -- is added.
         * */
        int payloadSizePadded = payloadSize + (payloadSize & 1);
        if (offset > payloadSizePadded) {
            throw new IOException("Out of chunk area");
        } else if (offset < payloadSizePadded) {
            reader.skip(payloadSizePadded - offset);
        }
    }

    /**
     * Parse chunk data here
     *
     * @param reader current reader
     */
    void innerParse(Reader reader) throws IOException {
    }

    int getContentLength() {
        return payloadSize + 8 + (payloadSize & 1);
    }
}
