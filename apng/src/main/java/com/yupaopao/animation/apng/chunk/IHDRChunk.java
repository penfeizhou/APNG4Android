package com.yupaopao.animation.apng.chunk;

import java.nio.ByteBuffer;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class IHDRChunk extends Chunk {
    static final String ID = "IHDR";
    /**
     * 图像宽度，以像素为单位
     */
    int width;
    /**
     * 图像高度，以像素为单位
     */
    int height;
    /**
     * 图像深度：
     * 索引彩色图像：1，2，4或8
     * 灰度图像：1，2，4，8或16
     * 真彩色图像：8或16
     */
    byte bitDepth;
    /**
     * 颜色类型：
     * 0：灰度图像, 1，2，4，8或16
     * 2：真彩色图像，8或16
     * 3：索引彩色图像，1，2，4或8
     * 4：带α通道数据的灰度图像，8或16
     * 6：带α通道数据的真彩色图像，8或16
     */
    byte colorType;

    @Override
    void parse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        width = byteBuffer.getInt();
        height = byteBuffer.getInt();
        bitDepth = byteBuffer.get();
        colorType = byteBuffer.get();
    }
}
