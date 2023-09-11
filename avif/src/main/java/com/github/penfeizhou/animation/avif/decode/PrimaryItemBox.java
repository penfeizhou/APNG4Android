package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: Primary Item Box
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class PrimaryItemBox extends FullBox {
    public static final String ID = "pitm";
    public int item_ID;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
        item_ID = version == 0 ? reader.readUInt16() : reader.readUInt32();
    }
}
