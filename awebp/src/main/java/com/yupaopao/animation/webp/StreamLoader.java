package com.yupaopao.animation.webp;

import com.yupaopao.animation.webp.reader.Reader;
import com.yupaopao.animation.webp.reader.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader {
    public abstract InputStream getInputStream() throws IOException;

    public Reader obtain() throws IOException {
        return new StreamReader(getInputStream());
    }

    /**
     * @return is webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#webp_file_header}
     */
    public boolean isWebp() {
        Reader reader = null;
        try {
            reader = obtain();
            return reader.matchFourCC("RIFF") && reader.skip(4) == 4 && reader.matchFourCC("WEBP");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * @return is animated webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#extended_file_format}
     */
    public boolean isAnimatedWebp() {
        Reader reader = null;
        try {
            reader = obtain();
            return reader.matchFourCC("RIFF")
                    && reader.skip(4) == 4
                    && reader.matchFourCC("WEBP")
                    && reader.matchFourCC("VP8X")
                    && (reader.peek() & 0x2) == 0x2
                    ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
