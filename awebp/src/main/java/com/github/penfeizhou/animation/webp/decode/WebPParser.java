package com.github.penfeizhou.animation.webp.decode;

import android.content.Context;

import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.io.StreamReader;
import com.github.penfeizhou.animation.webp.io.WebPReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class WebPParser {
    static class FormatException extends IOException {
        FormatException() {
            super("WebP Format error");
        }
    }

    public static boolean isAWebP(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return isAWebP(new StreamReader(inputStream));
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

    public static boolean isAWebP(Context context, String assetPath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            return isAWebP(new StreamReader(inputStream));
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

    public static boolean isAWebP(Context context, int resId) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            return isAWebP(new StreamReader(inputStream));
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

    public static boolean isAWebP(Reader in) {
        WebPReader reader = (in instanceof WebPReader) ? (WebPReader) in : new WebPReader(in);
        try {
            if (!reader.matchFourCC("RIFF")) {
                return false;
            }
            reader.skip(4);
            if (!reader.matchFourCC("WEBP")) {
                return false;
            }
            while (reader.available() > 0) {
                BaseChunk chunk = parseChunk(reader);
                if (chunk instanceof VP8XChunk) {
                    return ((VP8XChunk) chunk).animation();
                }
            }
        } catch (IOException e) {
            if (!(e instanceof FormatException)) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<BaseChunk> parse(WebPReader reader) throws IOException {
        //@link {https://developers.google.com/speed/webp/docs/riff_container#webp_file_header}
        if (!reader.matchFourCC("RIFF")) {
            throw new FormatException();
        }
        reader.skip(4);
        if (!reader.matchFourCC("WEBP")) {
            throw new FormatException();
        }
        List<BaseChunk> chunks = new ArrayList<>();
        while (reader.available() > 0) {
            chunks.add(parseChunk(reader));
        }
        return chunks;
    }

    static BaseChunk parseChunk(WebPReader reader) throws IOException {
        //@link {https://developers.google.com/speed/webp/docs/riff_container#riff_file_format}
        int offset = reader.position();
        int chunkFourCC = reader.getFourCC();
        int chunkSize = reader.getUInt32();
        BaseChunk chunk;
        if (VP8XChunk.ID == chunkFourCC) {
            chunk = new VP8XChunk();
        } else if (ANIMChunk.ID == chunkFourCC) {
            chunk = new ANIMChunk();
        } else if (ANMFChunk.ID == chunkFourCC) {
            chunk = new ANMFChunk();
        } else if (ALPHChunk.ID == chunkFourCC) {
            chunk = new ALPHChunk();
        } else if (VP8Chunk.ID == chunkFourCC) {
            chunk = new VP8Chunk();
        } else if (VP8LChunk.ID == chunkFourCC) {
            chunk = new VP8LChunk();
        } else if (ICCPChunk.ID == chunkFourCC) {
            chunk = new ICCPChunk();
        } else if (XMPChunk.ID == chunkFourCC) {
            chunk = new XMPChunk();
        } else if (EXIFChunk.ID == chunkFourCC) {
            chunk = new EXIFChunk();
        } else {
            chunk = new BaseChunk();
        }
        chunk.chunkFourCC = chunkFourCC;
        chunk.payloadSize = chunkSize;
        chunk.offset = offset;
        chunk.parse(reader);
        return chunk;
    }
}
