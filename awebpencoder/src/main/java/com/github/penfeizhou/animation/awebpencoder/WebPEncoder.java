package com.github.penfeizhou.animation.awebpencoder;

import android.graphics.Bitmap;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.github.penfeizhou.animation.io.ByteBufferReader;
import com.github.penfeizhou.animation.webp.decode.BaseChunk;
import com.github.penfeizhou.animation.webp.decode.VP8XChunk;
import com.github.penfeizhou.animation.webp.decode.WebPParser;
import com.github.penfeizhou.animation.webp.io.WebPReader;
import com.github.penfeizhou.animation.webp.io.WebPWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: com.github.penfeizhou.animation.awebpencoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-08-02
 */
public class WebPEncoder {
    private static final String TAG = WebPEncoder.class.getSimpleName();
    private int bgColor;
    private int loopCount;
    private WebPWriter writer = new WebPWriter();
    private List<FrameInfo> frameInfoList = new ArrayList<>();
    private int quality = 80;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private int width;
    private int height;

    public WebPEncoder() {
    }


    public WebPEncoder loopCount(int loopCount) {
        this.loopCount = loopCount;
        return this;
    }

    public WebPEncoder addFrame(FrameInfo frameInfo) {
        frameInfoList.add(frameInfo);
        width = Math.max(width, frameInfo.bitmap.getWidth());
        height = Math.max(height, frameInfo.bitmap.getHeight());
        return this;
    }

    public WebPEncoder addFrame(Bitmap bitmap, int frameX, int frameY, int duration) {
        FrameInfo frameInfo = new FrameInfo();
        frameInfo.bitmap = bitmap;
        frameInfo.frameX = frameX;
        frameInfo.frameY = frameY;
        frameInfo.duration = duration;
        frameInfoList.add(frameInfo);
        width = Math.max(width, bitmap.getWidth());
        height = Math.max(height, bitmap.getHeight());
        return this;
    }

    public WebPEncoder quality(int quality) {
        this.quality = quality;
        return this;
    }

    @WorkerThread
    public ByteBuffer build() {
        // 10M
        writer.reset(1000 * 1000 * 10);
        int vp8xPayloadSize = 10;
        int size = 12;

        //header
        writer.putFourCC("RIFF");
        writer.putUInt32(size);
        writer.putFourCC("WEBP");

        //VP8X
        writer.putFourCC("VP8X");
        writer.putUInt32(vp8xPayloadSize);
        writer.putByte((byte) (0x10 | 0x2));
        writer.putUInt24(0);
        writer.put1Based(width);
        writer.put1Based(height);
        size += vp8xPayloadSize;
        //ANIM
        writer.putFourCC("ANIM");
        writer.putUInt32(6);
        writer.putUInt32(this.bgColor);
        writer.putUInt16(this.loopCount);
        size += 6;

        //ANMF
        for (FrameInfo frameInfo : frameInfoList) {
            size += encodeFrame(frameInfo);
        }

        byte[] bytes = writer.toByteArray();
        bytes[4] = (byte) (size & 0xff);
        bytes[5] = (byte) ((size >> 8) & 0xff);
        bytes[6] = (byte) ((size >> 16) & 0xff);
        bytes[7] = (byte) ((size >> 24) & 0xff);
        ByteBuffer ret = ByteBuffer.allocate(writer.position());
        ret.put(bytes, 0, writer.position());
        return ret;
    }

    public int encodeFrame(FrameInfo frameInfo) {
        outputStream.reset();
        if (!frameInfo.bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)) {
            Log.e(TAG, "error in encode frame");
            return 0;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(outputStream.toByteArray(), 0, outputStream.size());
        WebPReader reader = new WebPReader(new ByteBufferReader(byteBuffer));

        try {
            List<BaseChunk> chunks = WebPParser.parse(reader);

            int payLoadSize = 16;
            int width = 0;
            int height = 0;
            for (BaseChunk chunk : chunks) {
                if (chunk instanceof VP8XChunk) {
                    width = ((VP8XChunk) chunk).canvasWidth;
                    height = ((VP8XChunk) chunk).canvasHeight;
                    continue;
                }
                payLoadSize += chunk.payloadSize + 8;
                payLoadSize += payLoadSize & 1;
            }
            writer.putUInt32(BaseChunk.fourCCToInt("ANMF"));
            writer.putUInt32(payLoadSize);
            writer.putUInt24(frameInfo.frameX);
            writer.putUInt24(frameInfo.frameY);
            writer.put1Based(width);
            writer.put1Based(height);
            writer.putUInt24(frameInfo.duration);
            writer.putByte((byte) ((frameInfo.blending ? 0x2 : 0) | (frameInfo.disposal ? 0x1 : 0)));
            for (BaseChunk chunk : chunks) {
                if (chunk instanceof VP8XChunk) {
                    //skip
                    continue;
                }
                writeChunk(writer, reader, chunk);
            }
            return payLoadSize;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error in encode frame");
        }
        return 0;
    }

    public WebPEncoder backgroundColor(int backgroundColor) {
        this.bgColor = backgroundColor;
        return this;
    }

    void writeChunk(WebPWriter writer, WebPReader reader, BaseChunk chunk) throws IOException {
        writer.putUInt32(chunk.chunkFourCC);
        writer.putUInt32(chunk.payloadSize);
        reader.reset();
        reader.skip(chunk.offset + 8);
        reader.read(writer.toByteArray(), writer.position(), chunk.payloadSize);
        writer.skip(chunk.payloadSize);
        if ((chunk.payloadSize & 1) == 1) {
            writer.putByte((byte) 0);
        }
    }

    static class FrameInfo {
        Bitmap bitmap;
        int frameX;
        int frameY;
        int duration;
        boolean blending;
        boolean disposal;
    }

    public static class FrameBuilder {
        FrameInfo frameInfo = new FrameInfo();

        public FrameBuilder() {

        }

        public FrameBuilder bitmap(Bitmap bitmap) {
            frameInfo.bitmap = bitmap;
            return this;
        }

        public FrameBuilder x(int x) {
            frameInfo.frameX = x;
            return this;
        }

        public FrameBuilder y(int y) {
            frameInfo.frameY = y;
            return this;
        }

        public FrameBuilder duration(int duration) {
            frameInfo.duration = duration;
            return this;
        }

        public FrameBuilder blending(boolean blending) {
            frameInfo.blending = blending;
            return this;
        }

        public FrameBuilder disposal(boolean disposal) {
            frameInfo.disposal = disposal;
            return this;
        }

        public FrameInfo build() {
            return frameInfo;
        }
    }
}
