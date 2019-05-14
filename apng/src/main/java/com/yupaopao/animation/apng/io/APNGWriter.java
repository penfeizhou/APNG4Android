package com.yupaopao.animation.apng.io;

import com.yupaopao.animation.io.ByteBufferWriter;

import java.nio.ByteOrder;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class APNGWriter extends ByteBufferWriter {
    public APNGWriter() {
        super();
    }

    public void writeFourCC(int fourcc) {
        putBytes(DataUtil.fourCCToByte(fourcc));
    }

    public void writeInt(int val) {
        putBytes(DataUtil.intToByte(val));
    }

    @Override
    public void reset(int size) {
        super.reset(size);
        this.byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }
}
