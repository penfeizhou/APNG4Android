package com.github.penfeizhou.animation.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.penfeizhou.animation.awebpencoder.WebPEncoder;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class APNGTestActivity extends Activity {
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
                WebPEncoder webPEncoder = new WebPEncoder();
                final ByteBuffer ret = webPEncoder.fromGif(new AssetStreamLoader(APNGTestActivity.this, "2.gif"));
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(imageView)
                                .load(ret.array())
                                .into(imageView);
                    }
                });
            }
        }).start();
    }
}