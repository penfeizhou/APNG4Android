package com.github.penfeizhou.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;

import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.gif.io.GifReader;
import com.github.penfeizhou.animation.gif.io.GifWriter;

import java.io.IOException;


/**
 * @Description: GifFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifFrame extends Frame<GifReader, GifWriter> {
    static {
        System.loadLibrary("gif-decoder");
    }

    public final int disposalMethod;
    public final int transparentColorIndex;
    public final ColorTable colorTable;
    private final int imageDataOffset;
    private final int lzwMinCodeSize;
    private static final ThreadLocal<byte[]> sDataBlock = new ThreadLocal<>();
    private static final int DEFAULT_DELAY = 10;
    private final boolean interlace;

    public GifFrame(GifReader reader,
                    ColorTable globalColorTable,
                    @Nullable GraphicControlExtension graphicControlExtension,
                    ImageDescriptor imageDescriptor) {
        super(reader);
        if (graphicControlExtension != null) {
            this.disposalMethod = graphicControlExtension.disposalMethod();
            frameDuration = ((graphicControlExtension.delayTime <= 0)
                    ? DEFAULT_DELAY
                    : graphicControlExtension.delayTime) * 10;
            if (graphicControlExtension.transparencyFlag()) {
                transparentColorIndex = graphicControlExtension.transparentColorIndex;
            } else {
                transparentColorIndex = -1;
            }
        } else {
            disposalMethod = 0;
            transparentColorIndex = -1;
        }
        frameX = imageDescriptor.frameX;
        frameY = imageDescriptor.frameY;
        frameWidth = imageDescriptor.frameWidth;
        frameHeight = imageDescriptor.frameHeight;
        interlace = imageDescriptor.interlaceFlag();
        if (imageDescriptor.localColorTableFlag()) {
            colorTable = imageDescriptor.localColorTable;
        } else {
            colorTable = globalColorTable;
        }
        this.lzwMinCodeSize = imageDescriptor.lzwMinimumCodeSize;
        imageDataOffset = imageDescriptor.imageDataOffset;
    }

    public boolean transparencyFlag() {
        return transparentColorIndex >= 0;
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, GifWriter writer) {
        try {
            writer.reset(frameWidth * frameHeight / (sampleSize * sampleSize));
            int[] pixels = writer.asIntArray();
            encode(pixels, sampleSize);
            reusedBitmap.copyPixelsFromBuffer(writer.asBuffer().rewind());
            canvas.drawBitmap(reusedBitmap, frameX / sampleSize, frameY / sampleSize, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reusedBitmap;
    }

    public void encode(int[] pixels, int sampleSize) throws IOException {
        reader.reset();
        reader.skip(imageDataOffset);
        byte[] dataBlock = sDataBlock.get();
        if (dataBlock == null) {
            dataBlock = new byte[0xff];
            sDataBlock.set(dataBlock);
        }
        uncompressLZW(reader,
                colorTable.getColorTable(),
                transparentColorIndex,
                pixels,
                frameWidth / sampleSize,
                frameHeight / sampleSize,
                lzwMinCodeSize,
                interlace,
                dataBlock);
    }

    private native void uncompressLZW(GifReader gifReader,
                                      int[] colorTable,
                                      int transparentColorIndex,
                                      int[] pixels,
                                      int width,
                                      int height,
                                      int lzwMinCodeSize,
                                      boolean interlace,
                                      byte[] buffer);
}
