package com.yupaopao.animation.webp.chunk;

import java.util.zip.CRC32;

/**
 * @Description: Transformed from FDATChunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FakedIDATChunk extends IDATChunk {
    FakedIDATChunk(FDATChunk fDatChunk) {
        this.length = fDatChunk.length - 4;
        this.type = IDATChunk.ID;
        this.data = fDatChunk.data;
        CRC32 crc32 = new CRC32();
        crc32.update(Chunk.readIntByByte(this.type, 0));
        crc32.update(Chunk.readIntByByte(this.type, 1));
        crc32.update(Chunk.readIntByByte(this.type, 2));
        crc32.update(Chunk.readIntByByte(this.type, 3));
        crc32.update(this.data, 4, this.length);
        crc = (int) crc32.getValue();
    }

    @Override
    int peekData(int i) {
        return super.peekData(i + 4);
    }

    @Override
    void copyData(byte[] dst, int offset) {
        System.arraycopy(data, 4, dst, offset, length);
    }

}
