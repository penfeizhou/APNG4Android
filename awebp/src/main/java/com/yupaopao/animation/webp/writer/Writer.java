package com.yupaopao.animation.webp.writer;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public interface Writer {

    void putByte(byte b);

    void putBytes(byte[] b);

    void putUInt16(int i);

    void putUInt24(int i);

    void putUInt32(int i);

    void put1Based(int i);

    void putFourCC(String v);

    int position();

    byte[] toByteArray();

    void close() throws IOException;
}
