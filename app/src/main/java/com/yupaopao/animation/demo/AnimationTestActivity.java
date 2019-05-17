package com.yupaopao.animation.demo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.gif.GifDrawable;
import com.yupaopao.animation.loader.AssetStreamLoader;
import com.yupaopao.animation.webp.WebPDrawable;


/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class AnimationTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnglib);
        LinearLayout linearLayout = findViewById(R.id.layout);
        String[] files = getIntent().getStringArrayExtra("files");
        for (String assetFile : files) {
            ImageView imageView = new ImageView(this);
            AssetStreamLoader loader = new AssetStreamLoader(this, assetFile);
            Drawable drawable = null;
            if (assetFile.endsWith("png")) {
                drawable = new APNGDrawable(loader);
            }
            if (assetFile.endsWith("webp")) {
                drawable = new WebPDrawable(loader);
            }
            if (assetFile.endsWith("gif")) {
                drawable = new GifDrawable(loader);
            }
            imageView.setImageDrawable(drawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
    }
}
