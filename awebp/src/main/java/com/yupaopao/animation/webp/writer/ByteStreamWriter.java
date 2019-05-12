package com.yupaopao.animation.webp.writer;

import com.yupaopao.animation.webp.DataUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class ByteStreamWriter implements Writer {
    private final ByteArrayOutputStream byteArrayOutputStream;

    public ByteStreamWriter() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void reset() {
        this.byteArrayOutputStream.reset();
    }

    @Override
    public void putByte(byte b) {
        this.byteArrayOutputStream.write(b);
    }

    @Override
    public void putBytes(byte[] b) {
        try {
            this.byteArrayOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putUInt16(int i) {
        try {
            byteArrayOutputStream.write(DataUtil.uInt16ToByte(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putUInt24(int i) {
        try {
            byteArrayOutputStream.write(DataUtil.uInt24ToByte(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putUInt32(int i) {
        try {
            byteArrayOutputStream.write(DataUtil.uInt32ToByte(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put1Based(int i) {
        try {
            byteArrayOutputStream.write(DataUtil.oneBasedToByte(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFourCC(String v) {
        try {
            byteArrayOutputStream.write(DataUtil.fourCCToByte(v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int position() {
        return byteArrayOutputStream.size();
    }

    @Override
    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void close() throws IOException {
        byteArrayOutputStream.close();
    }
}
