package com.yupaopao.animation.apng.chunk;

import java.nio.ByteBuffer;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27fcTL.27:_The_Frame_Control_Chunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FCTLChunk extends Chunk {
    static final String ID = "fcTL";
    int sequence_number;
    int width;
    int height;
    int x_offset;
    int y_offset;
    short delay_num;
    short delay_den;
    byte dispose_op;
    byte blend_op;

    @Override
    void parse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        sequence_number = byteBuffer.getInt();
        width = byteBuffer.getInt();
        height = byteBuffer.getInt();
        x_offset = byteBuffer.getInt();
        y_offset = byteBuffer.getInt();
        delay_num = byteBuffer.getShort();
        delay_den = byteBuffer.getShort();
        dispose_op = byteBuffer.get();
        blend_op = byteBuffer.get();
    }
}
