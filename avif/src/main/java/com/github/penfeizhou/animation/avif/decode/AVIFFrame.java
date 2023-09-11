package com.github.penfeizhou.animation.avif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.penfeizhou.animation.avif.io.AVIFReader;
import com.github.penfeizhou.animation.avif.io.AVIFWriter;
import com.github.penfeizhou.animation.decode.Frame;

/**
 * @Description: AVIFFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2023/9/11
 */
public class AVIFFrame extends Frame<AVIFReader, AVIFWriter> {

    public int index = 0;

    public AVIFFrame(AVIFReader reader) {
        super(reader);
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, AVIFWriter writer) {
        return null;
    }
}
