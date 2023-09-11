package com.github.penfeizhou.animation.avif.decode;

import android.text.TextUtils;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * @Description: ISO BMFF @see https://www.loc.gov/preservation/digital/formats/fdd/fdd000079.shtml#specs
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class Box {
    public int offset;
    public int size;

    public String type;

    static int fourCCToInt(String fourCC) {
        if (TextUtils.isEmpty(fourCC) || fourCC.length() != 4) {
            return 0xbadeffff;
        }
        return (fourCC.charAt(0) & 0xff)
                | (fourCC.charAt(1) & 0xff) << 8
                | (fourCC.charAt(2) & 0xff) << 16
                | (fourCC.charAt(3) & 0xff) << 24
                ;
    }

    void parse(AVIFReader reader) throws IOException {
        int available = reader.available();
        innerParse(reader);
        int offset = available - reader.available();
        int left = size - 8;
        if (offset > left) {
            throw new IOException("Out of box area");
        } else if (offset < left) {
            reader.skip(left - offset);
        }
    }

    void innerParse(AVIFReader reader) throws IOException {
    }

    @NonNull
    @Override
    public String toString() {
        return type + super.toString();
    }
}
