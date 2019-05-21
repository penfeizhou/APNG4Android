package com.yupaopao.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;
import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.loader.Loader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Description: GifDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifDecoder extends FrameSeqDecoder<GifReader, GifWriter> {

    private GifWriter mGifWriter = new GifWriter();
    private final Paint paint = new Paint();
    private int bgColor = Color.TRANSPARENT;
    private SnapShot snapShot = new SnapShot();

    private class SnapShot {
        ByteBuffer byteBuffer;
    }

    /**
     * @param loader         webp的reader
     * @param renderListener 渲染的回调
     */
    public GifDecoder(Loader loader, RenderListener renderListener) {
        super(loader, renderListener);
        paint.setAntiAlias(true);
    }

    @Override
    protected GifWriter getWriter() {
        if (mGifWriter == null) {
            mGifWriter = new GifWriter();
        }
        return mGifWriter;
    }

    @Override
    protected GifReader getReader(Reader reader) {
        return new GifReader(reader);
    }

    @Override
    protected int getLoopCount() {
        return 0;
    }

    @Override
    protected void release() {
        snapShot.byteBuffer = null;
        mGifWriter = null;
    }

    @Override
    protected Rect read(GifReader reader) throws IOException {
        List<Block> blocks = GifParser.parse(reader);
        int canvasWidth = 0, canvasHeight = 0;
        ColorTable globalColorTable = null;
        GraphicControlExtension graphicControlExtension = null;
        int bgColorIndex = -1;
        for (Block block : blocks) {
            if (block instanceof LogicalScreenDescriptor) {
                canvasWidth = ((LogicalScreenDescriptor) block).screenWidth;
                canvasHeight = ((LogicalScreenDescriptor) block).screenHeight;
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
            }
        }
        frameBuffer = ByteBuffer.allocate((canvasWidth * canvasHeight / (sampleSize * sampleSize) + 1) * 4);
        snapShot.byteBuffer = ByteBuffer.allocate((canvasWidth * canvasHeight / (sampleSize * sampleSize) + 1) * 4);
        if (globalColorTable != null && bgColorIndex > 0) {
            int abgr = globalColorTable.getColorTable()[bgColorIndex];
            this.bgColor = Color.rgb(abgr & 0xff, (abgr >> 8) & 0xff, (abgr >> 16) & 0xff);
        }
        return new Rect(0, 0, canvasWidth, canvasHeight);
    }

    @Override
    protected void renderFrame(Frame frame) {
        GifFrame gifFrame = (GifFrame) frame;
        Bitmap bitmap = obtainBitmap(fullRect.width() / sampleSize, fullRect.height() / sampleSize);
        Canvas canvas = cachedCanvas.get(bitmap);
        if (canvas == null) {
            canvas = new Canvas(bitmap);
            cachedCanvas.put(bitmap, canvas);
        }
        frameBuffer.rewind();
        bitmap.copyPixelsFromBuffer(frameBuffer);

        if (frameIndex == 0) {
            bitmap.eraseColor(bgColor);
        } else {
            GifFrame preFrame = (GifFrame) frames.get(frameIndex - 1);
            canvas.save();
            canvas.clipRect(preFrame.frameX,
                    preFrame.frameY,
                    preFrame.frameX + preFrame.frameWidth,
                    preFrame.frameY + preFrame.frameHeight);
            switch (preFrame.disposalMethod) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);
                    break;
                case 3:
                    snapShot.byteBuffer.rewind();
                    Bitmap preBitmap = obtainBitmap(fullRect.width() / sampleSize, fullRect.height() / sampleSize);
                    preBitmap.copyPixelsFromBuffer(snapShot.byteBuffer);
                    canvas.drawBitmap(preBitmap, 0, 0, paint);
                    recycleBitmap(preBitmap);
                    break;
            }
            canvas.restore();
            if (gifFrame.disposalMethod == 3) {
                if (preFrame.disposalMethod != 3) {
                    frameBuffer.rewind();
                    snapShot.byteBuffer.rewind();
                    snapShot.byteBuffer.put(frameBuffer);
                }
            }
        }
        Bitmap reused = obtainBitmap(frame.frameWidth, frame.frameHeight);
        gifFrame.draw(canvas, paint, sampleSize, reused, getWriter());
        recycleBitmap(reused);
        frameBuffer.rewind();
        bitmap.copyPixelsToBuffer(frameBuffer);
        recycleBitmap(bitmap);
    }
}
