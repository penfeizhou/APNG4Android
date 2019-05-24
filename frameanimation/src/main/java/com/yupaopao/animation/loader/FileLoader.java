package com.yupaopao.animation.loader;


import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.io.FileReader;

import java.io.File;
import java.io.IOException;

/**
 * @Description: 从文件加载流
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class FileLoader implements Loader {

    private final File mFile;
    private Reader mReader;

    public FileLoader(String path) {
        mFile = new File(path);
    }

    @Override
    public synchronized Reader obtain() throws IOException {
        if (mReader == null) {
            mReader = new FileReader(mFile);
        }
        return mReader;
    }

    @Override
    public void release() throws IOException {
        if (mReader != null) {
            mReader.close();
            mReader = null;
        }
    }
}
