package com.yupaopao.animation.loader;

import com.yupaopao.animation.io.Reader;

import java.io.IOException;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public interface Loader {
    Reader obtain() throws IOException;

    void release() throws IOException;
}
