package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: FileTypeBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class FileTypeBox extends Box {
    public static final String ID = "ftyp";

    public int major_brand;

    public int minor_version;

    public String[] compatible_brands;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        major_brand = reader.readUInt32();
        minor_version = reader.readUInt32();
        int brandCount = size / 4 - 4;
        compatible_brands = new String[brandCount];
        for (int i = 0; i < brandCount; i++) {
            compatible_brands[i] = reader.readString(4);
        }
    }
}
