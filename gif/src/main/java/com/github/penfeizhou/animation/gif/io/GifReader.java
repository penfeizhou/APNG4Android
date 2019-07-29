package com.github.penfeizhou.animation.gif.io;


import com.github.penfeizhou.animation.io.FilterReader;
import com.github.penfeizhou.animation.io.Reader;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifReader extends FilterReader {
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();


    private static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    public GifReader(Reader in) {
        super(in);
    }

    public int readUInt16() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 2);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8;
    }
}
