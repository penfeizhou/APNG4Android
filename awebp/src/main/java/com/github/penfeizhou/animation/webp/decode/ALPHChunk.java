package com.github.penfeizhou.animation.webp.decode;

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
}
