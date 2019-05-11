package com.yupaopao.animation.webp.chunk;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class VP8LChunk {
    static final int ID = ('V' & 0xFF) << 24
            | ('P' & 0xFF) << 16
            | ('8' & 0xFF) << 8
            | ('L' & 0xFF);
}
