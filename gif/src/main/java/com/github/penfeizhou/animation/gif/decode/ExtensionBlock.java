package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public abstract class ExtensionBlock implements Block {
    public static ExtensionBlock retrieve(GifReader reader) throws IOException {
        byte extensionLabel = reader.peek();
        ExtensionBlock extensionBlock;
        switch (extensionLabel) {
            case (byte) 0xf9:
                extensionBlock = new GraphicControlExtension();
                break;
            case (byte) 0xfe:
                extensionBlock = new CommentExtension();
                break;
            case (byte) 0x01:
                extensionBlock = new PlaintTextExtension();
                break;
            case (byte) 0xff:
                extensionBlock = new ApplicationExtension();
                break;
            default:
                throw new GifParser.FormatException();

        }
        return extensionBlock;
    }
}
