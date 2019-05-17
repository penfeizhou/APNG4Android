package com.yupaopao.animation.gif.decode;

import android.graphics.Rect;
import android.util.Log;

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

    /**
     * @param loader         webp的reader
     * @param renderListener 渲染的回调
     */
    public GifDecoder(Loader loader, RenderListener renderListener) {
        super(loader, renderListener);
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
        mGifWriter = null;
    }

    @Override
    protected Rect read(GifReader reader) throws IOException {
        List<Block> blocks = GifParser.parse(reader);
        int canvasWidth = 0, canvasHeight = 0;
        ColorTable globalColorTable = null;
        GraphicControlExtension graphicControlExtension = null;
        for (Block block : blocks) {
            if (block instanceof LogicalScreenDescriptor) {
                canvasWidth = ((LogicalScreenDescriptor) block).screenWidth;
                canvasHeight = ((LogicalScreenDescriptor) block).screenHeight;
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
        return new Rect(0, 0, canvasWidth, canvasHeight);
    }

    @Override
    protected void renderFrame(Frame frame) {
        GifFrame gifFrame = (GifFrame) frame;
        gifFrame.draw(null, null, sampleSize, null, getWriter());
        Log.d("GifDecode", "draw:" + frameIndex);
    }
}
