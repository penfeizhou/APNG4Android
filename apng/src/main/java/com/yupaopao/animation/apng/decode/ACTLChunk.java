package com.yupaopao.animation.apng.decode;

import com.yupaopao.animation.apng.io.APNGReader;
import com.yupaopao.animation.apng.io.DataUtil;

import java.io.IOException;

/**
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27acTL.27:_The_Animation_Control_Chunk
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class ACTLChunk extends Chunk {
    static final int ID = DataUtil.fourCCToInt("acTL");
    int num_frames;
    int num_plays;

    @Override
    void innerParse(APNGReader apngReader) throws IOException {
        num_frames = apngReader.readInt();
        num_plays = apngReader.readInt();
    }
}
