package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.reader.Reader;

import java.io.IOException;

/**
 * Alpha
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      ChunkHeader('ALPH')                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Rsv| P | F | C |     Alpha Bitstream...                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class ALPHChunk extends BaseChunk {
    static final int ID = BaseChunk.fourCCToInt("ALPH");
    byte flags;
    /**
     * Pre-processing (P): 2 bits
     * These INFORMATIVE bits are used to signal the pre-processing that has been performed during compression. The decoder can use this information to e.g. dither the values or smooth the gradients prior to display.
     * <p>
     * 0: no pre-processing
     * 1: level reduction
     */
    private static final int FLAG_PRE_PROCESSING = 0x30;

    /**
     * Filtering method (F): 2 bits
     * The filtering method used:
     * <p>
     * 0: None.
     * 1: Horizontal filter.
     * 2: Vertical filter.
     * 3: Gradient filter.
     */
    private static final int FLAG_FILTERING_METHOD = 0xc;

    /**
     * Compression method (C): 2 bits
     * The compression method used:
     * <p>
     * 0: No compression.
     * 1: Compressed using the WebP lossless format.
     */
    private static final int FLAG_COMPRESSING_METHOD = 0x3;

    @Override
    void innerParse(Reader reader) throws IOException {
        this.flags = reader.peek();
    }
}
