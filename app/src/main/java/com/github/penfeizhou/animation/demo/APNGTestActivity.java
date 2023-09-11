package com.github.penfeizhou.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.penfeizhou.animation.glide.AnimationDecoderOption;

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
                "file:///android_asset/test.avif",
                "file:///android_asset/wheel.avif",
                "file:///android_asset/world-cup.avif",
                "file:///android_asset/apng_detail_guide.png",
                "file:///android_asset/1.gif",
                "file:///android_asset/2.gif",
                "file:///android_asset/3.gif",
                "file:///android_asset/4.gif",
                "file:///android_asset/5.gif",
                "file:///android_asset/1.webp",
                "file:///android_asset/2.webp",
        };
        for (String url : urls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
            Glide.with(imageView)
                    .load(url)
//                    .set(AnimationDecoderOption.NO_ANIMATION_BOUNDS_MEASURE, true)
                    .into(imageView);
        }
    }
}
