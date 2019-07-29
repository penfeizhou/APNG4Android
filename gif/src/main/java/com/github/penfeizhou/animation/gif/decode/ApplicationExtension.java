package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: ApplicationExtension
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class ApplicationExtension extends ExtensionBlock {
    public int loopCount = -1;
    public String identifier;

    @Override
    public void receive(GifReader reader) throws IOException {
        int blockSize = reader.peek();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < blockSize; i++) {
            stringBuilder.append((char) reader.peek());
        }
        identifier = stringBuilder.toString();
        if ("NETSCAPE2.0".equals(identifier)) {
            int size = reader.peek() & 0xff;
            if (size == 3 && (reader.peek() & 0xff) == 1) {
                loopCount = reader.readUInt16();
            }
            DataSubBlock dataSubBlock;
            do {
                dataSubBlock = DataSubBlock.retrieve(reader);
            } while (!dataSubBlock.isTerminal());
        } else {
            DataSubBlock dataSubBlock;
            do {
                dataSubBlock = DataSubBlock.retrieve(reader);
            } while (!dataSubBlock.isTerminal());
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
