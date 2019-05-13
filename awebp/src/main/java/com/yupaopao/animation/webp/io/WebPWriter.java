package com.yupaopao.animation.webp.io;

import com.yupaopao.animation.io.ByteBufferWriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Description: WebPWriter
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class WebPWriter extends ByteBufferWriter {

    private ByteBuffer byteBuffer;

    public WebPWriter() {
        super();
    }

    @Override
    public int position() {
        return byteBuffer.position();
    }

    @Override
    public byte[] toByteArray() {
        return byteBuffer.array();
    }

    @Override
    public void close() {
    }

    @Override
    public void reset(int size) {
        if (byteBuffer == null || size > byteBuffer.limit()) {
            byteBuffer = ByteBuffer.allocate(size);
            this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        byteBuffer.clear();
    }
}
