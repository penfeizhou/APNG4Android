package com.yupaopao.animation.webp.chunk;

import com.yupaopao.animation.webp.reader.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class WebPParser {
    public static class FormatException extends IOException {
    }

    public static void parse(Reader reader) throws IOException {
        //@link {https://developers.google.com/speed/webp/docs/riff_container#webp_file_header}
        if (!reader.matchFourCC("RIFF")) {
            throw new FormatException();
        }
        long fileSize = reader.getUInt32();
        if (!reader.matchFourCC("WEBP")) {
            throw new FormatException();
        }
        List<Chunk> chunks = new ArrayList<>();
        Chunk chunk;
        while ((chunk = parseChunk(reader)) != null) {
            chunks.add(chunk);
        }
    }

    public static Chunk parseChunk(Reader reader) throws IOException {
        //@link {https://developers.google.com/speed/webp/docs/riff_container#riff_file_format}
        int chunkFourCC = reader.getFourCC();
        long chunkSize = reader.getUInt32();
        Chunk chunk;
        switch (chunkFourCC) {
            case VP8Chunk.ID:
                chunk = new VP8XChunk();
                break;
            default:
                chunk = new Chunk();
                break;
        }
        chunk.fourCC = chunkFourCC;
        chunk.size = chunkSize;
        chunk.parse(reader);
        return chunk;
    }
}
