package com.yupaopao.animation.webp.reader;

import java.io.IOException;

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
     * @return uint16 A 16-bit, little-endian, unsigned integer.
     */
    int getUInt16() throws IOException;

    /**
     * @return uint24 A 24-bit, little-endian, unsigned integer.
     */
    int getUInt24() throws IOException;

    /**
     * @return uint32 A 32-bit, little-endian, unsigned integer.
     */
    int getUInt32() throws IOException;

    /**
     * @return FourCC A FourCC (four-character code) is a uint32 created by concatenating four ASCII characters in little-endian order.
     */
    int getFourCC() throws IOException;

    /**
     * @return 1-based An unsigned integer field storing values offset by -1. e.g., Such a field would store value 25 as 24.
     */
    int get1Based() throws IOException;

    /**
     * @return read FourCC and match chars
     */
    boolean matchFourCC(String chars) throws IOException;

    /**
     * close io
     */
    void close() throws IOException;
}
