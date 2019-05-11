package com.yupaopao.animation.webp.chunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class VP8XChunk {
    static final int ID = ('V' & 0xFF) << 24
            | ('P' & 0xFF) << 16
            | ('8' & 0xFF) << 8
            | ('X' & 0xFF);

    private final boolean animation;

    public VP8XChunk(InputStream inputStream) throws IOException {
        inputStream.skip(6);
        animation = inputStream.read() == 1;
    }
}
