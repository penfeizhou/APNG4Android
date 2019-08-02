package com.github.penfeizhou.animation.webp.decode;

import android.text.TextUtils;

import com.github.penfeizhou.animation.webp.io.WebPReader;

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

    public static int fourCCToInt(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            return 0xbadeffff;
        }
        return (fourCC.charAt(0) & 0xff)
                | (fourCC.charAt(1) & 0xff) << 8
                | (fourCC.charAt(2) & 0xff) << 16
                | (fourCC.charAt(3) & 0xff) << 24
                ;
    }

    final void parse(WebPReader reader) throws IOException {
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
    void innerParse(WebPReader reader) throws IOException {
    }
}
