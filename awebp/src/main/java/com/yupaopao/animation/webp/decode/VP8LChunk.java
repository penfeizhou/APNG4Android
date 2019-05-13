package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.io.DataUtil;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
class VP8LChunk extends BaseChunk {
    static final int ID = DataUtil.fourCCToInt("VP8L");
}
