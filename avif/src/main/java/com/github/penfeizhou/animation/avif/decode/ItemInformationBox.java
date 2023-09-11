package com.github.penfeizhou.animation.avif.decode;

import com.github.penfeizhou.animation.avif.io.AVIFReader;

import java.io.IOException;

/**
 * @Description: ItemInformationBox
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class ItemInformationBox extends FullBox {
    public static final String ID = "iinf";
    public static final String INFE_ID = "infe";

    public int entry_count;
    public ItemInfoEntry[] item_infos;

    public static class ItemInfoExtension {
        public int extension_type;

        void innerParse(AVIFReader reader) throws IOException {
            extension_type = reader.readUInt32();
        }
    }

    public static class FDItemInfoExtension extends ItemInfoExtension {
        String content_location;
        String content_MD5;
        long content_length;
        long transfer_length;
        int entry_count;
        int[] group_ids;

        void innerParse(AVIFReader reader) throws IOException {
            content_location = reader.readString();
            content_MD5 = reader.readString();
            content_length = reader.readUInt64();
            transfer_length = reader.readUInt64();
            entry_count = reader.readUInt8();
            group_ids = new int[entry_count];
            for (int i = 0; i < entry_count; i++) {
                group_ids[i] = reader.readUInt32();
            }
        }
    }

    public static class ItemInfoEntry extends FullBox {
        public int item_ID;
        public int item_protection_index;
        public String item_type;

        public String item_name;

        public String content_type;

        public String content_encoding;
        public String item_uri_type;

        public int extension_type;
        public ItemInfoExtension itemInfoExtension;

        @Override
        void innerParse(AVIFReader reader) throws IOException {
            super.innerParse(reader);
            if (version == 0 || version == 1) {
                item_ID = reader.readUInt16();
                item_protection_index = reader.readUInt16();
                item_name = reader.readString();
                content_type = reader.readString();
                content_encoding = reader.readString();
            }
            if (version == 1) {
                extension_type = reader.readUInt32();
                if (AVIFParser.matchFourCC("fdel", extension_type)) {
                    itemInfoExtension = new FDItemInfoExtension();
                } else {
                    itemInfoExtension = new ItemInfoExtension();
                }
                itemInfoExtension.innerParse(reader);
            }
            if (version >= 2) {
                if (version == 2) {
                    item_ID = reader.readUInt16();
                } else if (version == 3) {
                    item_ID = reader.readUInt32();
                }
                item_protection_index = reader.readUInt16();
                item_type = reader.readString(4);
                item_name = reader.readString();
                if ("mime".equals(item_type)) {
                    content_type = reader.readString();
                    content_encoding = reader.readString();
                } else if ("uri".equals(item_type)) {
                    item_uri_type = reader.readString();
                }
            }
        }
    }

    @Override
    void innerParse(AVIFReader reader) throws IOException {
        super.innerParse(reader);
        if (version == 0) {
            entry_count = reader.readUInt16();
        } else {
            entry_count = reader.readUInt32();
        }
        item_infos = new ItemInfoEntry[entry_count];
        for (int i = 0; i < entry_count; i++) {
            Box box = AVIFParser.parseBox(reader);
            item_infos[i] = (ItemInfoEntry) box;
        }
    }
}
