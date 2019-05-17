package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class CommentExtension extends ExtensionBlock {
    private List<DataSubBlock> dataSubBlocks = new ArrayList<>();

    @Override
    public void receive(GifReader reader) throws IOException {
        DataSubBlock dataSubBlock;
        while (!(dataSubBlock = DataSubBlock.retrive(reader)).isTerminal()) {
            dataSubBlocks.add(dataSubBlock);
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
