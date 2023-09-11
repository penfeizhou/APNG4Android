package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: ItemPropertiesBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class ItemPropertiesBox extends Box {
    public static final String ID = "iprp";

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
    }
}
