package com.yupaopao.animation.apng;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 提供解码流
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public interface APNGStreamProvider {
    InputStream getInputStream() throws IOException;
}
