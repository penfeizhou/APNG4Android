package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.io.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 * @see <a href="https://www.w3.org/Graphics/GIF/spec-gif89a.txt">Gif Spec</a>
 */
public class GifParser {
    static class FormatException extends IOException {
        FormatException() {
            super("Gif Format error");
        }
    }

    public static boolean isGif(Reader reader) {
        try {
            GifReader gifReader = reader instanceof GifReader ? (GifReader) reader : new GifReader(reader);
            checkHeader(gifReader);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Block> parse(GifReader reader) throws IOException {
        checkHeader(reader);
        List<Block> blocks = new ArrayList<>();
        // Logical Screen Descriptor
        LogicalScreenDescriptor logicalScreenDescriptor = new LogicalScreenDescriptor();
        logicalScreenDescriptor.receive(reader);
        blocks.add(logicalScreenDescriptor);
        if (logicalScreenDescriptor.gColorTableFlag()) {
            ColorTable globalColorTable = new ColorTable(logicalScreenDescriptor.gColorTableSize());
            globalColorTable.receive(reader);
            blocks.add(globalColorTable);
        }
        byte flag;
        while ((flag = reader.peek()) != 0x3B) {
            Block block = null;
            switch (flag) {
                case 0x21:
                    block = ExtensionBlock.retrieve(reader);
                    break;
                case 0x2c:
                    block = new ImageDescriptor();
                    break;
            }
            if (block != null) {
                block.receive(reader);
                blocks.add(block);
            } else {
                throw new FormatException();
            }
        }

        return blocks;
    }


    private static void checkHeader(GifReader reader) throws IOException {
        byte a;
        if (reader.peek() != 'G'
                || reader.peek() != 'I'
                || reader.peek() != 'F'
                || reader.peek() != '8'
                || ((a = reader.peek()) != '7' && a != '9')
                || reader.peek() != 'a') {
            throw new FormatException();
        }
    }

}
