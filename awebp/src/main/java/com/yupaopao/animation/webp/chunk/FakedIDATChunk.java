package com.yupaopao.animation.webp.chunk;

import java.util.zip.CRC32;

/**
 * @Description: Transformed from FDATChunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FakedIDATChunk extends IDATChunk {
    FakedIDATChunk(FDATChunk fDatChunk) {
        this.size = fDatChunk.size - 4;
        this.fourCC = IDATChunk.ID;
        this.data = fDatChunk.data;
        CRC32 crc32 = new CRC32();
        crc32.update(Chunk.readIntByByte(this.fourCC, 0));
        crc32.update(Chunk.readIntByByte(this.fourCC, 1));
        crc32.update(Chunk.readIntByByte(this.fourCC, 2));
        crc32.update(Chunk.readIntByByte(this.fourCC, 3));
        crc32.update(this.data, 4, this.size);
        crc = (int) crc32.getValue();
    }

    @Override
    int peekData(int i) {
        return super.peekData(i + 4);
    }

    @Override
    void copyData(byte[] dst, int offset) {
        System.arraycopy(data, 4, dst, offset, size);
    }

}
