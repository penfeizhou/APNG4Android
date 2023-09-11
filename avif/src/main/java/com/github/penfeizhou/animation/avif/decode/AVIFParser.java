package com.github.penfeizhou.animation.avif.decode;

import android.content.Context;

import com.github.penfeizhou.animation.avif.io.AVIFReader;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.StreamReader;

import org.aomedia.avif.android.AvifDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            return AvifDecoder.isAvifImage(reader.toDirectByteBuffer());
        } catch (IOException e) {
            return false;
        }
    }
}
