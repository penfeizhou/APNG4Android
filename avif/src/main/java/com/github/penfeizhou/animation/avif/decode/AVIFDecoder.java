package com.github.penfeizhou.animation.avif.decode;


import android.graphics.Bitmap;
import android.graphics.Rect;

import com.github.penfeizhou.animation.avif.io.AVIFReader;
import com.github.penfeizhou.animation.avif.io.AVIFWriter;
import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.loader.Loader;

import org.aomedia.avif.android.AvifDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.annotation.Nullable;

/**
 * @Description: AVIFDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/7
 */
public class AVIFDecoder extends FrameSeqDecoder<AVIFReader, AVIFWriter> {
    /**
     * @param loader         loader
     * @param renderListener 渲染的回调
     */
    public AVIFDecoder(Loader loader, @Nullable RenderListener renderListener) {
        super(loader, renderListener);
    }

    private AvifDecoder avifDecoder = null;

    @Override
    protected AVIFWriter getWriter() {
        return null;
    }

    @Override
    protected AVIFReader getReader(Reader reader) {
        return new AVIFReader(reader);
    }

    @Override
    protected int getLoopCount() {
        if (avifDecoder == null) {
            return 0;
        }
        if (avifDecoder.getFrameCount() == 1) {
            return 1;
        }
        return avifDecoder.getRepetitionCount();
    }

    @Override
    protected void release() {
        if (avifDecoder != null) {
            avifDecoder.release();
            avifDecoder = null;
        }
    }

    @Override
    protected Rect read(AVIFReader reader) throws IOException {
        ByteBuffer source = reader.toDirectByteBuffer();
        avifDecoder = AvifDecoder.create(source);
        return new Rect(0, 0, avifDecoder.getWidth(), avifDecoder.getHeight());
    }

    @Override
    public int getFrameCount() {
        if (avifDecoder == null) {
            return 0;
        }
        return avifDecoder.getFrameCount();
    }

    @Override
    public Bitmap getFrameBitmap(int index) throws IOException {
        if (avifDecoder == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getBounds().width() / getSampleSize(), getBounds().height() / getSampleSize(), Bitmap.Config.ARGB_8888);
        avifDecoder.nthFrame(index, bitmap);
        return bitmap;
    }

    @Override
    public Frame<AVIFReader, AVIFWriter> getFrame(int index) {
        AVIFFrame avifFrame = new AVIFFrame(null);
        avifFrame.index = index;
        avifFrame.frameDuration = (int) (avifDecoder.getFrameDurations()[index] * 1000);
        return avifFrame;
    }

    @Override
    protected void renderFrame(Frame<AVIFReader, AVIFWriter> frame) {
        Bitmap bitmap = obtainBitmap(avifDecoder.getWidth(), avifDecoder.getHeight());
        if (avifDecoder == null) {
            return;
        }
        if (frameIndex != ((AVIFFrame) frame).index) {
            avifDecoder.nthFrame(((AVIFFrame) frame).index, bitmap);
        } else {
            if (frameIndex == 0) {
                avifDecoder.nthFrame(0, bitmap);
            } else {
                avifDecoder.nextFrame(bitmap);
            }
        }
        frameBuffer.rewind();
        try {
            bitmap.copyPixelsToBuffer(frameBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
    }

    @Override
    public int getSampleSize() {
        return 1;
    }
}
