package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: FullBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class FullBox extends Box {
    public int version;
    public int flags;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        version = reader.readUInt8();
        flags = reader.readUInt24();
    }
}
