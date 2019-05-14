package com.yupaopao.animation.apng.decode;

import com.yupaopao.animation.apng.io.DataUtil;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class IENDChunk extends Chunk {
    static final int ID = DataUtil.fourCCToInt("IEND");
}
