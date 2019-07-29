package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class PlaintTextExtension extends ExtensionBlock {
    private List<DataSubBlock> plainTextData = new ArrayList<>();

    @Override
    public void receive(GifReader reader) throws IOException {
        int blockSize = reader.peek();
        int x = reader.readUInt16();
        int y = reader.readUInt16();
        int width = reader.readUInt16();
        int height = reader.readUInt16();
        int characterCellWidth = reader.peek();
        int characterCellHeight = reader.peek();
        int fgColorIndex = reader.peek();
        int bgColorIndex = reader.peek();
        DataSubBlock dataSubBlock;
        while (!(dataSubBlock = DataSubBlock.retrieve(reader)).isTerminal()) {
            plainTextData.add(dataSubBlock);
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
