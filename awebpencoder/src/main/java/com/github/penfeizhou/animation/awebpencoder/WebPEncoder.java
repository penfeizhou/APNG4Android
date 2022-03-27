package com.github.penfeizhou.animation.awebpencoder;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.WorkerThread;

import android.util.Log;

import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.gif.decode.ApplicationExtension;
import com.github.penfeizhou.animation.gif.decode.Block;
import com.github.penfeizhou.animation.gif.decode.ColorTable;
import com.github.penfeizhou.animation.gif.decode.GifFrame;
import com.github.penfeizhou.animation.gif.decode.GifParser;
import com.github.penfeizhou.animation.gif.decode.GraphicControlExtension;
import com.github.penfeizhou.animation.gif.decode.ImageDescriptor;
import com.github.penfeizhou.animation.gif.decode.LogicalScreenDescriptor;
import com.github.penfeizhou.animation.gif.io.GifReader;
import com.github.penfeizhou.animation.gif.io.GifWriter;
import com.github.penfeizhou.animation.io.ByteBufferReader;
import com.github.penfeizhou.animation.loader.Loader;
import com.github.penfeizhou.animation.webp.decode.BaseChunk;
import com.github.penfeizhou.animation.webp.decode.ICCPChunk;
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

    @Deprecated
    public static WebPEncoder fromGif(Loader loader) {
        WebPEncoder webPEncoder = new WebPEncoder();
        webPEncoder.loadGif(loader);
        return webPEncoder;
    }

    public static WebPEncoder fromDecoder(FrameSeqDecoder<?, ?> decoder) {
        WebPEncoder webPEncoder = new WebPEncoder();
        webPEncoder.loadDecoder(decoder);
        return webPEncoder;
    }

    private void loadDecoder(FrameSeqDecoder<?, ?> decoder) {
        decoder.getBounds();
        int frameCount = decoder.getFrameCount();
        List<Integer> delay = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            delay.add(decoder.getFrame(i).frameDuration);
        }
        for (int i = 0; i < frameCount; i++) {
            try {
                Bitmap bitmap = decoder.getFrameBitmap(i);
                FrameInfo frameInfo = new FrameBuilder()
                        .bitmap(bitmap).offsetX(0).offsetY(0).duration(delay.get(i))
                        .blending(false).disposal(true)
                        .build();
                addFrame(frameInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGif(Loader loader) {
        try {
            GifReader reader = new GifReader(loader.obtain());
            List<Block> blocks = GifParser.parse(reader);
            ColorTable globalColorTable = null;
            List<GifFrame> frames = new ArrayList<>();
            GraphicControlExtension graphicControlExtension = null;
            int bgColorIndex = -1;
            for (Block block : blocks) {
                if (block instanceof LogicalScreenDescriptor) {
                    width = ((LogicalScreenDescriptor) block).screenWidth;
                    height = ((LogicalScreenDescriptor) block).screenHeight;
                    if (((LogicalScreenDescriptor) block).gColorTableFlag()) {
                        bgColorIndex = ((LogicalScreenDescriptor) block).bgColorIndex & 0xff;
                    }
                } else if (block instanceof ColorTable) {
                    globalColorTable = (ColorTable) block;
                } else if (block instanceof GraphicControlExtension) {
                    graphicControlExtension = (GraphicControlExtension) block;
                } else if (block instanceof ImageDescriptor) {
                    GifFrame gifFrame = new GifFrame(reader, globalColorTable, graphicControlExtension, (ImageDescriptor) block);
                    frames.add(gifFrame);
                } else if (block instanceof ApplicationExtension && "NETSCAPE2.0".equals(((ApplicationExtension) block).identifier)) {
                    loopCount = ((ApplicationExtension) block).loopCount;
                }
            }
            if (globalColorTable != null && bgColorIndex > 0) {
                int abgr = globalColorTable.getColorTable()[bgColorIndex];
                this.bgColor = Color.rgb(abgr & 0xff, (abgr >> 8) & 0xff, (abgr >> 16) & 0xff);
            }
            GifWriter writer = new GifWriter();
            for (GifFrame frame : frames) {
                writer.reset(frame.frameWidth * frame.frameHeight);
                int[] pixels = writer.asIntArray();
                frame.encode(pixels, 1);
                Bitmap bitmap = Bitmap.createBitmap(frame.frameWidth, frame.frameHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(writer.asBuffer().rewind());
                FrameBuilder frameBuilder = new FrameBuilder();
                boolean disposal = false, blending = false;
                switch (frame.disposalMethod) {
                    case 0:
                    case 1:
                        disposal = false;
                        blending = true;
                        break;
                    case 2:
                        disposal = true;
                        blending = true;
                        break;
                    case 3:
                        disposal = true;
                        blending = true;
                        break;
                }
                frameBuilder
                        .bitmap(bitmap)
                        .duration(frame.frameDuration)
                        .offsetX(frame.frameX)
                        .offsetY(frame.frameY)
                        .disposal(disposal)
                        .blending(blending);
                frameInfoList.add(frameBuilder.build());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public byte[] build() {
        // 10M
        writer.reset(1000 * 1000 * 10);
        int vp8xPayloadSize = 10;
        int size = 4;

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
        //ANIM
        writer.putFourCC("ANIM");
        writer.putUInt32(6);
        writer.putUInt32(this.bgColor);
        writer.putUInt16(this.loopCount);

        //ANMF
        for (FrameInfo frameInfo : frameInfoList) {
            encodeFrame(frameInfo);
        }

        byte[] bytes = writer.toByteArray();
        size = writer.position() - 8;
        bytes[4] = (byte) (size & 0xff);
        bytes[5] = (byte) ((size >> 8) & 0xff);
        bytes[6] = (byte) ((size >> 16) & 0xff);
        bytes[7] = (byte) ((size >> 24) & 0xff);
        ByteBuffer ret = ByteBuffer.allocate(writer.position());
        ret.put(bytes, 0, writer.position());
        return ret.array();
    }

    private int encodeFrame(FrameInfo frameInfo) {
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
            int width = frameInfo.bitmap.getWidth();
            int height = frameInfo.bitmap.getHeight();
            for (BaseChunk chunk : chunks) {
                if (chunk instanceof VP8XChunk) {
                    width = ((VP8XChunk) chunk).canvasWidth;
                    height = ((VP8XChunk) chunk).canvasHeight;
                    continue;
                }
                if (chunk instanceof ICCPChunk) {
                    continue;
                }
                payLoadSize += chunk.payloadSize + 8;
                payLoadSize += payLoadSize & 1;
            }
            writer.putUInt32(BaseChunk.fourCCToInt("ANMF"));
            writer.putUInt32(payLoadSize);
            writer.putUInt24(frameInfo.frameX / 2);
            writer.putUInt24(frameInfo.frameY / 2);
            writer.put1Based(width);
            writer.put1Based(height);
            writer.putUInt24(frameInfo.duration);
            writer.putByte((byte) ((frameInfo.blending ? 0x2 : 0) | (frameInfo.disposal ? 0x1 : 0)));
            for (BaseChunk chunk : chunks) {
                if (chunk instanceof VP8XChunk) {
                    continue;
                }
                if (chunk instanceof ICCPChunk) {
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

    private void writeChunk(WebPWriter writer, WebPReader reader, BaseChunk chunk) throws IOException {
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

        public FrameBuilder offsetX(int x) {
            frameInfo.frameX = x;
            return this;
        }

        public FrameBuilder offsetY(int y) {
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
