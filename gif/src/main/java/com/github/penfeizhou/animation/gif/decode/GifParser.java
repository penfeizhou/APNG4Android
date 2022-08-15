package com.github.penfeizhou.animation.gif.decode;

import android.content.Context;

import com.github.penfeizhou.animation.gif.io.GifReader;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static boolean isGif(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return isGif(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isGif(Context context, String assetPath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            return isGif(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isGif(Context context, int resId) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            return isGif(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isGif(Reader reader) {
        try {
            GifReader gifReader = reader instanceof GifReader ? (GifReader) reader : new GifReader(reader);
            checkHeader(gifReader);
            return true;
        } catch (IOException e) {
            if (!(e instanceof FormatException)) {
                e.printStackTrace();
            }
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
        try {
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
        } catch (Exception e) {
            // https://github.com/penfeizhou/APNG4Android/issues/119 To compat with this situation.
            e.printStackTrace();
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
