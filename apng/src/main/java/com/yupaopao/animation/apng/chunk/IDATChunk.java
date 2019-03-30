package com.yupaopao.animation.apng.chunk;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class IDATChunk extends Chunk {
    static final int ID = ('I' & 0xFF) << 24
            | ('D' & 0xFF) << 16
            | ('A' & 0xFF) << 8
            | ('T' & 0xFF);
}
