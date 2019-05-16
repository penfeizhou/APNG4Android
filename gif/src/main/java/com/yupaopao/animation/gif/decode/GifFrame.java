package com.yupaopao.animation.gif.decode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yupaopao.animation.decode.Frame;
import com.yupaopao.animation.gif.io.GifReader;
import com.yupaopao.animation.gif.io.GifWriter;

/**
 * @Description: GifFrame
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifFrame extends Frame<GifReader, GifWriter> {
    static {
        System.loadLibrary("gif-decoder");
    }

    public GifFrame(GifReader reader) {
        super(reader);
        Log.d("osborn", nativeDecode());
    }

    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, GifWriter writer) {
        return null;
    }

    private native String nativeDecode();
}
