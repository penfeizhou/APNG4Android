package com.github.penfeizhou.animation.avif.io;

import android.text.TextUtils;

import com.github.penfeizhou.animation.io.ByteBufferWriter;

/**
 * @Description: AVIFWriter
 * @Author: pengfei.zhou
 * @CreateDate: 2023-09-07
 */
public class AVIFWriter extends ByteBufferWriter {

    public void putUInt16(int val) {
        putByte((byte) (val & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
    }

    public void putUInt24(int val) {
        putByte((byte) (val & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
    }

    public void putUInt32(int val) {
        putByte((byte) (val & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
        putByte((byte) ((val >> 24) & 0xff));
    }

    public void put1Based(int i) {
        putUInt24(i - 1);
    }

    public void putFourCC(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            skip(4);
            return;
        }
        putByte((byte) (fourCC.charAt(0) & 0xff));
        putByte((byte) (fourCC.charAt(1) & 0xff));
        putByte((byte) (fourCC.charAt(2) & 0xff));
        putByte((byte) (fourCC.charAt(3) & 0xff));
    }
}
