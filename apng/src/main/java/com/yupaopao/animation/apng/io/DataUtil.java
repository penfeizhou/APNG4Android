package com.yupaopao.animation.apng.io;

import android.text.TextUtils;

/**
 * @Description: DataUtil
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class DataUtil {
    public static int fourCCToInt(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            return 0xbadeffff;
        }
        return (fourCC.charAt(0) & 0xff)
                | (fourCC.charAt(1) & 0xff) << 8
                | (fourCC.charAt(2) & 0xff) << 16
                | (fourCC.charAt(3) & 0xff) << 24
                ;
    }

    public static byte[] fourCCToByte(String fourCC) {
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


    public static byte[] fourCCToByte(int val) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (val & 0xff);
        bytes[1] = (byte) ((val >> 8) & 0xff);
        bytes[2] = (byte) ((val >> 16) & 0xff);
        bytes[3] = (byte) ((val >> 24) & 0xff);
        return bytes;
    }

    public static byte[] intToByte(int val) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((val >> 24) & 0xff);
        bytes[1] = (byte) ((val >> 16) & 0xff);
        bytes[2] = (byte) ((val >> 8) & 0xff);
        bytes[3] = (byte) (val & 0xff);
        return bytes;
    }

}
