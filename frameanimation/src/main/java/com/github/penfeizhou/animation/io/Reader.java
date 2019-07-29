package com.github.penfeizhou.animation.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @link {https://developers.google.com/speed/webp/docs/riff_container#terminology_basics}
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public interface Reader {
    long skip(long total) throws IOException;

    byte peek() throws IOException;

    void reset() throws IOException;

    int position();

    int read(byte[] buffer, int start, int byteCount) throws IOException;

    int available() throws IOException;

    /**
     * close io
     */
    void close() throws IOException;

    InputStream toInputStream() throws IOException;
}
