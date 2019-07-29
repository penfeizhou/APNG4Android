package com.github.penfeizhou.animation.webp.decode;

import com.github.penfeizhou.animation.webp.io.WebPReader;

import java.io.IOException;

/**
 * For an animated image, this chunk contains the global parameters of the animation.
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      ChunkHeader('ANIM')                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Background Color                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Loop Count           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class ANIMChunk extends BaseChunk {
    static final int ID = BaseChunk.fourCCToInt("ANIM");
    /**
     * The default background color of the canvas in [Blue, Green, Red, Alpha] byte order.
     * This color MAY be used to fill the unused space on the canvas around the frames, as well as the transparent pixels of the first frame.
     * Background color is also used when disposal method is 1.
     */
    int backgroundColor;
    /**
     * The number of times to loop the animation. 0 means infinitely.
     */
    int loopCount;

    @Override
    void innerParse(WebPReader reader) throws IOException {
        this.backgroundColor = reader.getUInt32();
        this.loopCount = reader.getUInt16();
    }
}
