package com.github.penfeizhou.animation.avif.decode;

import android.content.Context;
import android.text.TextUtils;

import com.github.penfeizhou.animation.avif.io.AVIFReader;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @link {https://aomediacodec.github.io/av1-spec/av1-spec.pdf}
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class AVIFParser {
    static class FormatException extends IOException {
        FormatException() {
            super("AVIF Format error");
        }
    }

    public static boolean isAVIF(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return isAVIF(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isAVIF(Context context, String assetPath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            return isAVIF(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isAVIF(Context context, int resId) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            return isAVIF(new StreamReader(inputStream));
        } catch (Exception e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isAVIF(Reader in) {
        AVIFReader reader = (in instanceof AVIFReader) ? (AVIFReader) in : new AVIFReader(in);
        try {
            reader.skip(4);
            return reader.matchFourCC("ftyp");
        } catch (IOException e) {
            return false;
        }
    }

    public static List<Box> parse(AVIFReader reader) throws IOException {
        List<Box> boxes = new ArrayList<>();
        while (reader.available() > 0) {
            boxes.add(parseBox(reader));
        }
        return boxes;
    }

    public static boolean matchFourCC(String chars, int fourCC) throws IOException {
        if (TextUtils.isEmpty(chars) || chars.length() != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (((fourCC >> (i * 8)) & 0xff) != chars.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    static Box parseBox(AVIFReader reader) throws IOException {
        int offset = reader.position();
        int size = reader.readUInt32();
        String type = reader.readString(4);
        Box box;
        switch (type) {
            case FileTypeBox.ID:
                box = new FileTypeBox();
                break;
            case MetaBox.ID:
                box = new MetaBox();
                break;
            case HandlerBox.ID:
                box = new HandlerBox();
                break;
            case PrimaryItemBox.ID:
                box = new PrimaryItemBox();
                break;
            case ItemLocationBox.ID:
                box = new ItemLocationBox();
                break;
            case ItemInformationBox.ID:
                box = new ItemInformationBox();
                break;
            case ItemInformationBox.INFE_ID:
                box = new ItemInformationBox.ItemInfoEntry();
                break;
            default:
                box = new Box();
                break;
        }
        box.offset = offset;
        box.size = size;
        box.type = type;
        box.parse(reader);
        return box;
    }
}
