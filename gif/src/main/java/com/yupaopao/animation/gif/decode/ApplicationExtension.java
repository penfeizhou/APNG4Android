package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: ApplicationExtension
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class ApplicationExtension extends ExtensionBlock {
    @Override
    public void receive(GifReader reader) throws IOException {
        int blockSize = reader.peek();
        reader.skip(blockSize);
        DataSubBlock dataSubBlock;
        do {
            dataSubBlock = DataSubBlock.retrive(reader);
        } while (!dataSubBlock.isTerminal());
    }

    @Override
    public int size() {
        return 0;
    }
}
