package com.github.penfeizhou.animation.webp.decode;

import com.github.penfeizhou.animation.webp.io.WebPReader;

import java.io.IOException;

/**
 * ANMF chunk:
 * For animated images, this chunk contains information about a single frame.
 * If the Animation flag is not set, then this chunk SHOULD NOT be present.
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      ChunkHeader('ANMF')                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Frame X                |             ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ...          Frame Y            |   Frame Width Minus One     ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ...             |           Frame Height Minus One              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                 Frame Duration                |  Reserved |B|D|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Frame Data                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class ANMFChunk extends BaseChunk {
    static final int ID = BaseChunk.fourCCToInt("ANMF");
    /**
     * Frame X: 24 bits (uint24)
     * The X coordinate of the upper left corner of the frame is Frame X * 2
     */
    int frameX;

    /**
     * Frame Y: 24 bits (uint24)
     * The Y coordinate of the upper left corner of the frame is Frame Y * 2
     */
    int frameY;
    /**
     * Frame Width Minus One: 24 bits (uint24)
     * The 1-based width of the frame. The frame width is 1 + Frame Width Minus One
     */
    int frameWidth;

    /**
     * Frame Height Minus One: 24 bits (uint24)
     * The 1-based height of the frame. The frame height is 1 + Frame Height Minus One
     */
    int frameHeight;

    /**
     * Frame Duration: 24 bits (uint24)
     * The time to wait before displaying the next frame, in 1 millisecond units.
     * Note the interpretation of frame duration of 0 (and often <= 10) is implementation defined.
     * Many tools and browsers assign a minimum duration similar to GIF.
     */
    int frameDuration;

    byte flags;

    /**
     * Blending method (B): 1 bit
     * Indicates how transparent pixels of the current frame are to be blended with corresponding pixels of the previous canvas:
     * <p>
     * 0: Use alpha blending. After disposing of the previous frame, render the current frame on the canvas using alpha-blending (see below).
     * If the current frame does not have an alpha channel, assume alpha value of 255, effectively replacing the rectangle.
     * <p>
     * 1: Do not blend. After disposing of the previous frame,
     * render the current frame on the canvas by overwriting the rectangle covered by the current frame.
     */
    private static final int FLAG_BLENDING_METHOD = 0x2;

    /**
     * Disposal method (D): 1 bit
     * Indicates how the current frame is to be treated after it has been displayed (before rendering the next frame) on the canvas:
     * <p>
     * 0: Do not dispose. Leave the canvas as is.
     * <p>
     * 1: Dispose to background color. Fill the rectangle on the canvas covered by the current frame with background color specified in the ANIM chunk.
     */
    private static final int FLAG_DISPOSAL_METHOD = 0x1;

    ALPHChunk alphChunk;

    VP8Chunk vp8Chunk;

    VP8LChunk vp8LChunk;

    @Override
    void innerParse(WebPReader reader) throws IOException {
        int available = reader.available();
        this.frameX = reader.getUInt24();
        this.frameY = reader.getUInt24();
        this.frameWidth = reader.get1Based();
        this.frameHeight = reader.get1Based();
        this.frameDuration = reader.getUInt24();
        this.flags = reader.peek();
        long bounds = available - payloadSize;
        while (reader.available() > bounds) {
            BaseChunk chunk = WebPParser.parseChunk(reader);
            if (chunk instanceof ALPHChunk) {
                assert alphChunk == null;
                alphChunk = (ALPHChunk) chunk;
            } else if (chunk instanceof VP8Chunk) {
                assert vp8Chunk == null && vp8LChunk == null;
                vp8Chunk = (VP8Chunk) chunk;
            } else if (chunk instanceof VP8LChunk) {
                assert vp8Chunk == null && vp8LChunk == null;
                vp8LChunk = (VP8LChunk) chunk;
            }
        }
    }

    boolean blendingMethod() {
        return (flags & FLAG_BLENDING_METHOD) == FLAG_BLENDING_METHOD;
    }

    boolean disposalMethod() {
        return (flags & FLAG_DISPOSAL_METHOD) == FLAG_DISPOSAL_METHOD;
    }
}
