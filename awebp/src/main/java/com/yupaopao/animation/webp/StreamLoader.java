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

    private Reader mReader;

    public synchronized Reader obtain() throws IOException {
        if (mReader == null) {
            mReader = new StreamReader(getInputStream());
        }
        return mReader;
    }

    public synchronized void release() throws IOException {
        if (mReader != null) {
            mReader.close();
            mReader = null;
        }
    }

    /**
     * @return is webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#webp_file_header}
     */
    public boolean isWebp() {
        try {
            Reader reader = obtain();
            return reader.matchFourCC("RIFF") && reader.skip(4) == 4 && reader.matchFourCC("WEBP");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @return is animated webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#extended_file_format}
     */
    public boolean isAnimatedWebp() {
        try {
            Reader reader = obtain();
            return reader.matchFourCC("RIFF")
                    && reader.skip(4) == 4
                    && reader.matchFourCC("WEBP")
                    && reader.matchFourCC("VP8X")
                    && (reader.peek() & 0x2) == 0x2
                    ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
