package com.yupaopao.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yupaopao.animation.glide.AnimationDecoderOption;


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
        String[] urls = new String[]{
                "https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png",
                "https://isparta.github.io/compare-webp/image/gif_webp/webp/2.webp",
                "file:///android_asset/1.gif",
                "file:///android_asset/5.gif",
                "file:///android_asset/6.gif",
                "https://misc.aotu.io/ONE-SUNDAY/world_cup_2014_42.webp",
                "https://misc.aotu.io/ONE-SUNDAY/BladeRunner.webp",
                "https://misc.aotu.io/ONE-SUNDAY/SteamEngine.webp",
                "https://misc.aotu.io/ONE-SUNDAY/SteamEngine_lossy.webp",
        };
        for (String url : urls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
            GlideApp.with(imageView)
                    .load(url)
                    .set(AnimationDecoderOption.DISABLE_ANIMATION_GIF_DECODER, false)
                    .into(imageView);
        }
//        FileLoader fileLoader = new FileLoader("/data/data/com.yupaopao.animation.demo/cache/image_manager_disk_cache/b6dd68d837b8d1e8f24edb0a0df5213b7accebb16c22c8202e95363f7227740e.0");
//
//        ImageView imageView = new ImageView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.bottomMargin = 50;
//        layoutParams.topMargin = 50;
//        linearLayout.addView(imageView, layoutParams);
//        imageView.setImageDrawable(new APNGDrawable(fileLoader));

    }
}