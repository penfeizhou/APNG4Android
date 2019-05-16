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

    public static void parse(GifReader reader) throws IOException {
        if (reader.peek() != 'G') {
            throw new FormatException();
        }
    }

}
