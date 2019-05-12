package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.reader.Reader;

import java.io.IOException;

/**
 * @Description: BaseChunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class BaseChunk {
    public int chunkFourCC;
    public int payloadSize;
    public int offset;
    public static final int CHUNCK_HEADER_OFFSET = 8;

    final void parse(Reader reader) throws IOException {
        int available = reader.available();
        innerParse(reader);
        int offset = available - reader.available();
        /**
         * Chunk Payload: Chunk Size bytes
         * The data payload. If Chunk Size is odd, a single padding byte -- that SHOULD be 0 -- is added.
         * */
        int payloadSizePadded = payloadSize + (payloadSize & 1);
        if (offset > payloadSizePadded) {
            throw new IOException("Out of chunk area");
        } else if (offset < payloadSizePadded) {
            reader.skip(payloadSizePadded - offset);
        }
    }

    /**
     * Parse chunk data here
     *
     * @param reader current reader
     */
    void innerParse(Reader reader) throws IOException {
    }

    int getContentLength() {
        return payloadSize + CHUNCK_HEADER_OFFSET + (payloadSize & 1);
    }
}
