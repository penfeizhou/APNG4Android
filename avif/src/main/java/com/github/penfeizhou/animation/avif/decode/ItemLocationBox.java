package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: ItemLocationBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class ItemLocationBox extends FullBox {
    public static class Extent {
        long index;
        long offset;
        long length;
    }

    public static class ItemEntry {
        public int id;
        public int constructionMethod;
        public int dataReferenceIndex;
        public long baseOffset;
        public Extent[] extents;
    }

    public static final String ID = "iloc";
    public int offset_size;
    public int length_size;
    public int base_offset_size;
    public int index_size;
    public int item_count;

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
        byte b = reader.peek();

        offset_size = b >> 4;
        length_size = b & 0xf;
        b = reader.peek();
        base_offset_size = b >> 4;
        if (version == 1 || version == 2) {
            index_size = b & 0xf;
        }
        item_count = version < 2 ? reader.readUInt16() : reader.readUInt32();
        for (int i = 0; i < item_count; i++) {
            ItemEntry itemEntry = new ItemEntry();
            if (version < 2) {
                itemEntry.id = reader.readUInt16();
            } else {
                itemEntry.id = reader.readUInt32();
            }
            if (version == 1 || version == 2) {
                int v = reader.readUInt16();
                itemEntry.constructionMethod = v & 0xf;
            }
            itemEntry.dataReferenceIndex = reader.readUInt16();
            if (base_offset_size == 4) {
                itemEntry.baseOffset = reader.readUInt32();
            } else if (base_offset_size == 8) {
                itemEntry.baseOffset = reader.readUInt64();
            }
            int extent_count = reader.readUInt16();
            itemEntry.extents = new Extent[extent_count];
            for (int j = 0; j < extent_count; j++) {
                Extent extent = new Extent();
                if (((version == 1) || (version == 2)) && (index_size > 0)) {
                    if (index_size == 4) {
                        extent.index = reader.readUInt32();
                    } else if (index_size == 8) {
                        extent.index = reader.readUInt64();
                    }
                }
                if (offset_size == 4) {
                    extent.offset = reader.readUInt32();
                } else if (offset_size == 8) {
                    extent.offset = reader.readUInt64();
                }

                if (length_size == 4) {
                    extent.length = reader.readUInt32();
                } else if (length_size == 8) {
                    extent.length = reader.readUInt64();
                }
                itemEntry.extents[i] = extent;
            }
        }
    }


}
