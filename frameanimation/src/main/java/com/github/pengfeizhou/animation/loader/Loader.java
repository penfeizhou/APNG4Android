package com.github.pengfeizhou.animation.loader;

import com.github.pengfeizhou.animation.io.Reader;

import java.io.IOException;

/**
 * @Description: Loader
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
public interface Loader {
    Reader obtain() throws IOException;
}
