package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class DataSubBlock implements Block {

    public static final DataSubBlock sBlockTerminal = new DataSubBlock(0);
    private final int blockSize;
    private int offset;

    public DataSubBlock(int blockSize) {
        this.blockSize = blockSize;
    }

    public static DataSubBlock retrieve(GifReader reader) throws IOException {
        int blockSize = reader.peek() & 0xff;
        if (blockSize == 0) {
            return sBlockTerminal;
        }
        DataSubBlock dataSubBlock = new DataSubBlock(blockSize);
        dataSubBlock.offset = reader.position();
        dataSubBlock.receive(reader);
        return dataSubBlock;
    }

    @Override
    public void receive(GifReader reader) throws IOException {
        reader.skip(blockSize);
    }

    @Override
    public int size() {
        return blockSize + 1;
    }

    public boolean isTerminal() {
        return this == sBlockTerminal;
    }
}
