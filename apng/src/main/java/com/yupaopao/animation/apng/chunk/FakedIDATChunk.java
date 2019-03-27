package com.yupaopao.animation.apng.chunk;

/**
 * @Description: Transformed from FDATChunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class FakedIDATChunk extends IDATChunk {
    FakedIDATChunk(FDATChunk fDatChunk) {
        this.length = fDatChunk.length - 1;
        this.typeCode = IDATChunk.ID;
        this.data = fDatChunk.data;
        this.crc = fDatChunk.crc;
    }

    @Override
    int peekData(int i) {
        return super.peekData(i + 1);
    }

    @Override
    void copyData(byte[] dst, int offset) {
        System.arraycopy(data, 1, dst, offset, length);
    }
}
