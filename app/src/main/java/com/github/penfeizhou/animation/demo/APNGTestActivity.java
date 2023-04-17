package com.github.penfeizhou.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.RequestOptions;
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
                "https://raw.githubusercontent.com/penfeizhou/APNG4Android/master/app/src/main/assets/test2.png"};
        for (String url : urls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
            GlideApp.with(imageView)
                    .load(url)
                    .set(AnimationDecoderOption.NO_ANIMATION_BOUNDS_MEASURE, true)
                    .into(imageView);
        }
    }
}
