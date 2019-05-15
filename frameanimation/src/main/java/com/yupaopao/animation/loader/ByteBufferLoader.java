package com.yupaopao.animation.loader;

import com.yupaopao.animation.io.ByteBufferReader;
import com.yupaopao.animation.io.Reader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: ByteBufferLoader
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-15
 */
public abstract class ByteBufferLoader implements Loader {
    public abstract ByteBuffer getByteBuffer();

    @Override
    public Reader obtain() throws IOException {
        return new ByteBufferReader(getByteBuffer());
    }

    @Override
    public void release() throws IOException {

    }
}
