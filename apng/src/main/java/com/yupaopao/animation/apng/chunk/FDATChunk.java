package com.yupaopao.animation.apng.chunk;

import java.nio.ByteBuffer;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27fdAT.27:_The_Frame_Data_Chunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FDATChunk extends Chunk {
    static final String ID = "fdAT";
    int sequence_number;

    @Override
    void parse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        sequence_number = byteBuffer.getInt();
    }
}
