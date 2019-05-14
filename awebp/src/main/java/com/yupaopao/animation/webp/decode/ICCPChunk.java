package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.io.DataUtil;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class ICCPChunk extends BaseChunk {
    static final int ID = DataUtil.fourCCToInt("ICCP");
}
