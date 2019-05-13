package com.yupaopao.animation.loader;


import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader {
    protected abstract InputStream getInputStream() throws IOException;

    private InputStream in;

    public final synchronized InputStream obtain() throws IOException {
        if (in == null) {
            in = getInputStream();
        }
        return in;
    }

    public final synchronized void release() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }
}
