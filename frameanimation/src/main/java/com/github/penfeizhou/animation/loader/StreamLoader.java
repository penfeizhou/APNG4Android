package com.github.penfeizhou.animation.loader;


import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader implements Loader {
    protected abstract InputStream getInputStream() throws IOException;


    public final synchronized Reader obtain() throws IOException {
        return new StreamReader(getInputStream());
    }
}
