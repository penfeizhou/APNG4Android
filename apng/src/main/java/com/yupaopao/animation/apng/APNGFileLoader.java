package com.yupaopao.animation.apng;

import com.yupaopao.animation.apng.chunk.APNGStreamLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG文件加载器，从本地file中加载文件
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public class APNGFileLoader extends APNGStreamLoader {

    private final File mFile;

    public APNGFileLoader(String path) {
        mFile = new File(path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(mFile);
    }
}
