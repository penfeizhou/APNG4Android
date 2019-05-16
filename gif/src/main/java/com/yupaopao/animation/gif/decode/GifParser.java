package com.yupaopao.animation.gif.decode;

import com.yupaopao.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: GifParser
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifParser {
    static class FormatException extends IOException {
        FormatException() {
            super("WebP Format error");
        }
    }

    public static boolean isGif(GifReader reader) {
        try {
            checkHeader(reader);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void parse(GifReader reader) throws IOException {
        checkHeader(reader);
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
