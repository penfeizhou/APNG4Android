package com.yupaopao.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;


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
        };
        for (String url : urls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
            Glide.with(imageView).load(url).into(imageView);
        }
    }
}