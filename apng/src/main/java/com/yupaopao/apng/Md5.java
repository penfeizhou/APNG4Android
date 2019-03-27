package com.yupaopao.apng;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class Md5 {

    public static String toMD5(String s) {
        if (s != null) {
            try {
                byte[] bs = s.getBytes("UTF-8");
                return encrypt(bs);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private synchronized static String encrypt(byte[] obj) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(obj);
            byte[] bs = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Integer.toHexString((0x000000ff & bs[i]) | 0xffffff00).substring(6));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
