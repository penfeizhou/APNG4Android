package com.yupaopao.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;

/**
 * @Description: GifFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifFrame extends Frame<GifReader, GifWriter> {
    public final int disposalMethod;
    public final int transparentColorIndex;
    public final ColorTable colorTable;

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
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, GifWriter writer) {
        return null;
    }
}
