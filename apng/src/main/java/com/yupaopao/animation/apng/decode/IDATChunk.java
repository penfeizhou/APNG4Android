package com.yupaopao.animation.apng.decode;

import com.yupaopao.animation.apng.io.DataUtil;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class IDATChunk extends Chunk {
    static final int ID = DataUtil.fourCCToInt("IDAT");
}
