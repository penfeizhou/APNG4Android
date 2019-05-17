package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: GraphicRenderingBlock
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class GraphicRenderingBlock implements Block {
    private List<ExtensionBlock> extensionBlocks = new ArrayList<>();
    private ImageDescriptor imageDescriptor = new ImageDescriptor();

    @Override
    public void receive(GifReader reader) throws IOException {
        byte identify;
        while ((identify = reader.peek()) == 0x21) {
            extensionBlocks.add(ExtensionBlock.retrive(reader));
        }
        if (identify == 0x2c) {
            imageDescriptor.receive(reader);
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
