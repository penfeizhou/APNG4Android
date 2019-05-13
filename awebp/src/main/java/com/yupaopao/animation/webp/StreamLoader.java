package com.yupaopao.animation.webp;

import com.yupaopao.animation.webp.reader.StreamReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader {
    protected abstract InputStream getInputStream() throws IOException;

    private InputStream in;

    public synchronized InputStream obtain() throws IOException {
        if (in == null) {
            in = new StreamReader(getInputStream());
        }
        return in;
    }

    public synchronized void release() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }
}
