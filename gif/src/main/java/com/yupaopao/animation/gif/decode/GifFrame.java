package com.yupaopao.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;

import java.io.IOException;

/**
 * @Description: GifFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifFrame extends Frame<GifReader, GifWriter> {
    public final int disposalMethod;
    public final int transparentColorIndex;
    public final ColorTable colorTable;
    private final int imageDataOffset;
    private final int lzwMinCodeSize;
    private static final ThreadLocal<byte[]> sDataBlock = new ThreadLocal<>();

    static {
        sDataBlock.set(new byte[0xff]);
    }

    public GifFrame(GifReader reader,
                    ColorTable globalColorTable,
                    @Nullable GraphicControlExtension graphicControlExtension,
                    ImageDescriptor imageDescriptor) {
        super(reader);
        if (graphicControlExtension != null) {
            this.disposalMethod = graphicControlExtension.disposalMethod();
            frameDuration = graphicControlExtension.delayTime * 10;
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
        if (imageDescriptor.localColorTableFlag()) {
            colorTable = imageDescriptor.localColorTable;
        } else {
            colorTable = globalColorTable;
        }
        this.lzwMinCodeSize = imageDescriptor.lzwMinimumCodeSize;
        imageDataOffset = imageDescriptor.imageDataOffset;
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, GifWriter writer) {
        try {
            reader.reset();
            reader.skip(imageDataOffset);
            writer.reset(frameWidth * frameHeight / (sampleSize * sampleSize) * 4 + 1);
            int clearCode = 1 << lzwMinCodeSize;
            int endCode = clearCode + 1;
            int dataBlockSize;

            byte[] dataBlock = sDataBlock.get();

            while ((dataBlockSize = (reader.peek() & 0xff)) != 0) {
                for (int i = 0; i < dataBlockSize; i++) {

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
