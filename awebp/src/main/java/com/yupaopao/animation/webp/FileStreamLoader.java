package com.yupaopao.animation.webp;


import com.yupaopao.animation.webp.chunk.StreamLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 从文件加载流
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class FileStreamLoader extends StreamLoader {

    private final File mFile;

    public FileStreamLoader(String path) {
        mFile = new File(path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(mFile);
    }
}
