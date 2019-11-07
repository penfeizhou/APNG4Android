package com.github.penfeizhou.animation.webp.decode;


import com.github.penfeizhou.animation.webp.io.WebPReader;

import java.io.IOException;

/**
 * @Description: @link {https://developers.google.com/speed/webp/docs/riff_container#riff_file_format}
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class VP8XChunk extends BaseChunk {
    static final int ID = BaseChunk.fourCCToInt("VP8X");
    /**
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                   WebP file header (12 bytes)                 |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                      ChunkHeader('VP8X')                      |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |Rsv|I|L|E|X|A|R|                   Reserved                    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |          Canvas Width Minus One               |             ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * ...  Canvas Height Minus One    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */

    private static final int FLAG_ANIMATION = 0x2;
    private static final int FLAG_ALPHA = 0x10;
    /**
     * Reserved (Rsv): 2 bits
     * SHOULD be 0.
     * ICC profile (I): 1 bit
     * Set if the file contains an ICC profile.
     * Alpha (L): 1 bit
     * Set if any of the frames of the image contain transparency information ("alpha").
     * EXIF metadata (E): 1 bit
     * Set if the file contains EXIF metadata.
     * XMP metadata (X): 1 bit
     * Set if the file contains XMP metadata.
     * Animation (A): 1 bit
     * Set if this is an animated image. Data in 'ANIM' and 'ANMF' chunks should be used to control the animation.
     * Reserved (R): 1 bit
     * SHOULD be 0
     */
    byte flags;

    /**
     * Canvas Width Minus One: 24 bits
     * 1-based width of the canvas in pixels. The actual canvas width is '1 + Canvas Width Minus One'
     */
    public int canvasWidth;
    /**
     * Canvas Height Minus One: 24 bits
     * 1-based height of the canvas in pixels. The actual canvas height is '1 + Canvas Height Minus One'
     */
    public int canvasHeight;

    void innerParse(WebPReader reader) throws IOException {
        flags = reader.peek();
        reader.skip(3);
        canvasWidth = reader.get1Based();
        canvasHeight = reader.get1Based();
    }

    /**
     * @return Set if this is an animated image. Data in 'ANIM' and 'ANMF' chunks should be used to control the animation.
     */
    boolean animation() {
        return (flags & FLAG_ANIMATION) == FLAG_ANIMATION;
    }

    /**
     * @return Set if any of the frames of the image contain transparency information ("alpha").
     */
    boolean alpha() {
        return (flags & FLAG_ALPHA) == FLAG_ALPHA;
    }
}
