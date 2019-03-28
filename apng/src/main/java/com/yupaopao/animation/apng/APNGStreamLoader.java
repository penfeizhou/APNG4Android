package com.yupaopao.animation.apng;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG 流加载器接口，可实现该接口加载APNG文件
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public interface APNGStreamLoader {
    /**
     * 打开APNG的文件流，读取完后会自动close
     */
    InputStream getInputStream() throws IOException;
}
