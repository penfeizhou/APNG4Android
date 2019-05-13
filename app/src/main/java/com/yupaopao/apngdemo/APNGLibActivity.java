package com.yupaopao.apngdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yupaopao.animation.loader.AssetStreamLoader;
import com.yupaopao.animation.webp.AnimatedWebpDrawable;


/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class APNGLibActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnglib);
        LinearLayout linearLayout = findViewById(R.id.layout);
        String[] assetFiles = {
                "test.png",
                "test2.png",
                "test3.png",
                "test4.png",
                "test5.png",
                "wheel.png",
        };
//        for (String assetFile : assetFiles) {
//            APNGAssetLoader loader = new APNGAssetLoader(this, assetFile);
//            APNGDrawable apngDrawable = new APNGDrawable(loader);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageDrawable(apngDrawable);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.bottomMargin = 50;
//            layoutParams.topMargin = 50;
//            linearLayout.addView(imageView, layoutParams);
//        }
//        {
//            AssetStreamLoader loader = new AssetStreamLoader(this, "animation.webp");
//            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageDrawable(webpDrawable);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.bottomMargin = 50;
//            layoutParams.topMargin = 50;
//            linearLayout.addView(imageView, layoutParams);
//        }
        {
            AssetStreamLoader loader = new AssetStreamLoader(this, "1.webp");
            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(webpDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
        {
            AssetStreamLoader loader = new AssetStreamLoader(this, "2.webp");
            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(webpDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
        {
            AssetStreamLoader loader = new AssetStreamLoader(this, "animation.webp");
            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(webpDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
        {
            AssetStreamLoader loader = new AssetStreamLoader(this, "example.webp");
            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(webpDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
    }
}
