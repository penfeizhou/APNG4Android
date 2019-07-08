package com.github.pengfeizhou.animation.loader;


import com.github.pengfeizhou.animation.io.Reader;
import com.github.pengfeizhou.animation.io.FileReader;

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
        return new FileReader(mFile);
    }
}
