package com.yupaopao.animation.apng.chunk;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class IENDChunk extends Chunk {
    static final int ID = ('I' & 0xFF) << 24
            | ('E' & 0xFF) << 16
            | ('N' & 0xFF) << 8
            | ('D' & 0xFF);
}
