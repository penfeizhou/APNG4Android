package com.yupaopao.animation.apng.decode;

import com.yupaopao.animation.apng.io.APNGReader;

import java.io.IOException;

/**
 * @Description: Length (长度)	4字节	指定数据块中数据域的长度，其长度不超过(231－1)字节
 * Chunk Type Code (数据块类型码)	4字节	数据块类型码由ASCII字母(A-Z和a-z)组成
 * Chunk Data (数据块数据)	可变长度	存储按照Chunk Type Code指定的数据
 * CRC (循环冗余检测)	4字节	存储用来检测是否有错误的循环冗余码
 * @Link https://www.w3.org/TR/PNG
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Chunk {
    int length;
    int fourcc;
    int crc;
    int offset;

    void parse(APNGReader reader) throws IOException {
        int available = reader.available();
        innerParse(reader);
        int offset = available - reader.available();
        if (offset > length) {
            throw new IOException("Out of chunk area");
        } else if (offset < length) {
            reader.skip(length - offset);
        }
    }

    void innerParse(APNGReader reader) throws IOException {
    }
}
