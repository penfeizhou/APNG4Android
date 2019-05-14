package com.yupaopao.animation.webp.io;

import com.yupaopao.animation.io.ByteBufferWriter;

/**
 * @Description: WebPWriter
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class WebPWriter extends ByteBufferWriter {

    public void putUInt16(int i) {
        putBytes(DataUtil.uInt16ToByte(i));
    }

    public void putUInt24(int i) {
        putBytes(DataUtil.uInt24ToByte(i));
    }

    public void putUInt32(int i) {
        putBytes(DataUtil.uInt32ToByte(i));
    }

    public void put1Based(int i) {
        putBytes(DataUtil.oneBasedToByte(i));
    }

    public void putFourCC(String v) {
        putBytes(DataUtil.fourCCToByte(v));
    }
}
