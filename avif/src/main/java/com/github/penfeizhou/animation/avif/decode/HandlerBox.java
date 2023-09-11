package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: HandlerBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class HandlerBox extends FullBox {
    public static final String ID = "hdlr";
    public int pre_defined;

    public String handler_type;

    public byte[] reserved;

    public String name;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
        pre_defined = reader.readUInt32();
        handler_type = reader.readString(4);
        reserved = new byte[12];
        reader.read(reserved, 0, 12);
        name = reader.readString();
    }
}
