package com.github.penfeizhou.animation.avif.decode;


import android.graphics.Rect;

import com.github.penfeizhou.animation.avif.io.AVIFReader;
import com.github.penfeizhou.animation.avif.io.AVIFWriter;
import com.github.penfeizhou.animation.decode.Frame;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.io.Reader;
import com.github.penfeizhou.animation.loader.Loader;

import java.io.IOException;

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

    @Override
    protected AVIFWriter getWriter() {
        return null;
    }

    @Override
    protected AVIFReader getReader(Reader reader) {
        return null;
    }

    @Override
    protected int getLoopCount() {
        return 0;
    }

    @Override
    protected void release() {

    }

    @Override
    protected Rect read(AVIFReader reader) throws IOException {
        return null;
    }

    @Override
    protected void renderFrame(Frame<AVIFReader, AVIFWriter> frame) {

    }
}
