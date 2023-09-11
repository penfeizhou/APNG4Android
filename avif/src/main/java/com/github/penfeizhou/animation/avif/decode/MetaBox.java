package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: MetaBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class MetaBox extends FullBox {
    public static final String ID = "meta";

    public Box[] other_boxes;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
        List<Box> cached = new ArrayList<>();
        while (reader.position() < offset + size) {
            cached.add(AVIFParser.parseBox(reader));
        }
        other_boxes = cached.toArray(new Box[0]);
    }
}
