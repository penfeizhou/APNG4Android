package com.yupaopao.animation.webp.chunk;

import java.nio.ByteBuffer;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27acTL.27:_The_Animation_Control_Chunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class ACTLChunk extends Chunk {
    static final int ID = ('a' & 0xFF) << 24
            | ('c' & 0xFF) << 16
            | ('T' & 0xFF) << 8
            | ('L' & 0xFF);
    int num_frames;
    int num_plays;

    @Override
    void parse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        num_frames = byteBuffer.getInt();
        num_plays = byteBuffer.getInt();
    }
}
