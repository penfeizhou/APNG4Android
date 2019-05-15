package com.yupaopao.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.loader.AssetStreamLoader;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class APNGTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apngtest);
        final ImageView imageView = findViewById(R.id.imageView);
//        final AssetStreamLoader assetLoader = new AssetStreamLoader(this, "wheel.png");
//        final APNGDrawable apngDrawable = new APNGDrawable(
//                assetLoader);
//        imageView.setImageDrawable(apngDrawable);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (apngDrawable.isPaused()) {
//                    apngDrawable.resume();
//                } else {
//                    apngDrawable.pause();
//                }
//            }
//        });
        Glide.with(imageView)
                .load("https://yvideo.eryufm.cn/video/gif2/50f0156c-c1b0-4c8e-a861-d177d8393c82.gif?imageview2/0/format/webp")
                .into(imageView);
    }
}
