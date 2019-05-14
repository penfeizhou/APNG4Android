package com.yupaopao.animation.loader;


import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.io.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader implements Loader {
    protected abstract InputStream getInputStream() throws IOException;

    private InputStream in;

    public final synchronized Reader obtain() throws IOException {
        if (in == null) {
            in = getInputStream();
        }
        return new StreamReader(in);
    }

    public final synchronized void release() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }
}
