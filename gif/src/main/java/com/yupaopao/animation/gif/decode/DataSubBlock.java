package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;

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

    public static DataSubBlock retrive(GifReader reader) throws IOException {
        byte blockSize = reader.peek();
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
