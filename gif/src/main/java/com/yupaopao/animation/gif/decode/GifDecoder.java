package com.yupaopao.animation.gif.decode;

import android.graphics.Rect;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;
import com.yupaopao.animation.io.Reader;
import com.yupaopao.animation.loader.Loader;

import java.io.IOException;

/**
 * @Description: GifDecoder
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifDecoder extends FrameSeqDecoder<GifReader, GifWriter> {
    /**
     * @param loader         webp的reader
     * @param renderListener 渲染的回调
     */
    public GifDecoder(Loader loader, RenderListener renderListener) {
        super(loader, renderListener);
    }

    @Override
    protected GifWriter getWriter() {
        return null;
    }

    @Override
    protected GifReader getReader(Reader reader) {
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
    protected Rect read(GifReader reader) throws IOException {
        return null;
    }

    @Override
    protected void renderFrame(Frame frame) {

    }
}
