package com.yupaopao.animation.io;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public interface Writer {
    void reset(int size);

    void putByte(byte b);

    void putBytes(byte[] b);

    int position();

    byte[] toByteArray();

    void close() throws IOException;
}
