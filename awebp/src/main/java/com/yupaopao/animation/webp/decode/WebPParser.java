package com.yupaopao.animation.webp.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yupaopao.animation.webp.DataUtil;
import com.yupaopao.animation.webp.reader.Reader;
import com.yupaopao.animation.webp.reader.StreamReader;
import com.yupaopao.animation.webp.writer.ByteBufferWriter;
import com.yupaopao.animation.webp.writer.Writer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        List<BaseChunk> chunks = new ArrayList<>();
        while (reader.available() > 0) {
            chunks.add(parseChunk(reader));
        }
        Log.d("test", "done");
        VP8XChunk vp8xChunk = null;
        for (BaseChunk chunk : chunks) {
            if (chunk instanceof VP8XChunk) {
                vp8xChunk = (VP8XChunk) chunk;
            }
            if (chunk instanceof ANMFChunk) {
                int vp8chunkSize = chunk.payloadSize - 16;
                ByteBuffer byteBuffer = ByteBuffer.allocate(12 + vp8xChunk.getContentLength() + vp8chunkSize);
                Writer writer = new ByteBufferWriter(byteBuffer);
                writer.putFourCC("RIFF");
                writer.putUInt32(12 + vp8xChunk.getContentLength() + vp8chunkSize);
                writer.putFourCC("WEBP");
                writer.putUInt32(VP8XChunk.ID);
                writer.putUInt32(vp8xChunk.payloadSize);
                writer.putUInt32(0);
                writer.put1Based(((ANMFChunk) chunk).frameWidth);
                writer.put1Based(((ANMFChunk) chunk).frameHeight);
                reader.reset();
                reader.skip(chunk.offset + 8 + 16);
                reader.read(writer.toByteArray(), writer.position(), vp8chunkSize);
                Bitmap bitmap = BitmapFactory.decodeByteArray(writer.toByteArray(), 0, byteBuffer.array().length);
                assert bitmap != null;
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
                Reader reader1 = new StreamReader(byteArrayInputStream);
                parse(reader1);
                File file = new File("/data/user/0/com.yupaopao.apngdemo/cache/1");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(byteBuffer.array());
                fileOutputStream.flush();
                break;
            }
        }
    }

    static BaseChunk parseChunk(Reader reader) throws IOException {
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
