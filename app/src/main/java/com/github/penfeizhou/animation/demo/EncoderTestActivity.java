package com.github.penfeizhou.animation.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.apng.decode.APNGDecoder;
import com.github.penfeizhou.animation.awebpencoder.WebPEncoder;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.gif.GifDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class EncoderTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnglib);
        LinearLayout linearLayout = findViewById(R.id.layout);
        final ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 50;
        layoutParams.topMargin = 50;
        linearLayout.addView(imageView, layoutParams);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final byte[] ret = WebPEncoder.fromDecoder(
                        APNGDrawable.fromAsset(EncoderTestActivity.this,
                                "test2.png").getFrameSeqDecoder()).build();
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(imageView)
                                .load(ret)
                                .into(imageView);
                    }
                });
            }
        }).start();
    }
}