package com.yupaopao.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

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
            writer.reset(frameWidth * frameHeight);
            int clearCode = 1 << lzwMinCodeSize;
            int endCode = clearCode + 1;
            int dataLeftCount = 0;
            int codeSize = lzwMinCodeSize + 1;
            byte[] dataBlock = sDataBlock.get();
            if (dataBlock == null) {
                dataBlock = new byte[0xff];
                sDataBlock.set(dataBlock);
            }
            int dataIndex = 0;
            int bits = 0;
            int datum = 0;
            int code;
            int prefix = -1;
            int curW = 0;
            LinkedList<Point> stringTable = new LinkedList<>();
            while (writer.position() < frameWidth * frameHeight) {
                if (dataLeftCount == 0) {
                    dataLeftCount = reader.peek() & 0xff;
                    reader.read(dataBlock, 0, dataLeftCount);
                    dataIndex = 0;
                }
                datum += (dataBlock[dataIndex] & 0xff) << bits;
                bits += 8;
                dataIndex++;
                dataLeftCount--;
                while (bits >= codeSize) {
                    // Get Code
                    code = datum & ((1 << codeSize) - 1);
                    // Dispose preVal
                    datum >>= codeSize;
                    bits -= codeSize;

                    if (code == clearCode) {
                        codeSize = lzwMinCodeSize + 1;
                        if (prefix >= 0) {
                            output(writer, stringTable, prefix, endCode);
                        }
                        stringTable.clear();
                        prefix = -1;
                        continue;
                    } else if (code == endCode) {
                        break;
                    } else {
                        if (prefix < 0) {
                            //ignore first char
                        } else {
                            curW = code;
                            while (curW > endCode) {
                                if (curW - endCode > stringTable.size()) {
                                    curW = prefix;
                                } else {
                                    curW = stringTable.get(curW - endCode - 1).x;
                                }
                            }
                            Point point = new Point(prefix, curW);
                            stringTable.add(point);
                            if (stringTable.size() >= (1 << codeSize) - endCode - 1) {
                                codeSize++;
                            }
                            //output
                            output(writer, stringTable, prefix, endCode);
                        }
                        prefix = code;
                    }

                }
            }
            int[] colors = new int[frameWidth * frameHeight];
            byte[] pixels = writer.toByteArray();
            for (int i = 0; i < frameWidth * frameHeight; i++) {
                int idx = pixels[i] & 0xff;
                colors[i] = colorTable.getColor(idx);
            }
            Bitmap bitmap = Bitmap.createBitmap(colors, frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
            Log.d("OSBORN", "Create Bitmap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void output(GifWriter gifWriter, LinkedList<Point> stringTable, int idx, int endCode) {
        if (idx > endCode) {
            Point point = stringTable.get(idx - endCode - 1);
            output(gifWriter, stringTable, point.x, endCode);
            output(gifWriter, stringTable, point.y, endCode);
        } else {
            gifWriter.putByte((byte) (idx & 0xff));
        }
    }
}
