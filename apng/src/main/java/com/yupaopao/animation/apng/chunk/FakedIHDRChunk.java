package com.yupaopao.animation.apng.chunk;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FakedIHDRChunk extends IHDRChunk {
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

    FakedIHDRChunk(IHDRChunk ihdrChunk, int width, int height) {
        this.length = ihdrChunk.length;
        this.type = ihdrChunk.type;
        this.bitDepth = ihdrChunk.bitDepth;
        this.colorType = ihdrChunk.colorType;
        this.width = width;
        this.height = height;
        this.data = ihdrChunk.data.clone();
        this.data[0] = (byte) ((this.width >> 24) & 0xff);
        this.data[1] = (byte) ((this.width >> 16) & 0xff);
        this.data[2] = (byte) ((this.width >> 8) & 0xff);
        this.data[3] = (byte) (this.width & 0xff);

        this.data[4] = (byte) ((this.height >> 24) & 0xff);
        this.data[5] = (byte) ((this.height >> 16) & 0xff);
        this.data[6] = (byte) ((this.height >> 8) & 0xff);
        this.data[7] = (byte) (this.height & 0xff);
        CRC32 crc32 = new CRC32();
        crc32.update(readIntByByte(this.type, 0));
        crc32.update(readIntByByte(this.type, 1));
        crc32.update(readIntByByte(this.type, 2));
        crc32.update(readIntByByte(this.type, 3));
        crc32.update(this.data, 0, this.length);
        crc = (int) crc32.getValue();
    }
}
