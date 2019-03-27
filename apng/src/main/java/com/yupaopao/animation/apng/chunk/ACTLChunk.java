package com.yupaopao.animation.apng.chunk;

import java.nio.ByteBuffer;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27acTL.27:_The_Animation_Control_Chunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class ACTLChunk extends Chunk {
    final static String ID = "acTL";
    int num_frames;
    int num_plays;

    @Override
    void parse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        num_frames = byteBuffer.getInt();
        num_plays = byteBuffer.getInt();
    }
}
