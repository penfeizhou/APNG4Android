package com.yupaopao.animation.webp.decode;

import com.yupaopao.animation.webp.io.StreamReader;

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

    public static List<BaseChunk> parse(StreamReader reader) throws IOException {
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

    static BaseChunk parseChunk(StreamReader reader) throws IOException {
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
