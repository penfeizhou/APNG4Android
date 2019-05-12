package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.DataUtil;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class VP8Chunk extends BaseChunk {
    static final int ID = DataUtil.fourCCToInt("VP8 ");
}
