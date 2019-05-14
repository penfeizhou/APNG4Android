package com.yupaopao.apngdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.loader.AssetStreamLoader;
import com.yupaopao.animation.webp.AnimatedWebPDrawable;


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
        {
            String[] assetFiles = {
                    "test.png",
                    "test2.png",
                    "test3.png",
                    "test4.png",
                    "test5.png",
                    "wheel.png",
                    "png.png"
            };
            for (String assetFile : assetFiles) {
                AssetStreamLoader loader = new AssetStreamLoader(this, assetFile);
                APNGDrawable apngDrawable = new APNGDrawable(loader);
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(apngDrawable);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = 50;
                layoutParams.topMargin = 50;
                linearLayout.addView(imageView, layoutParams);
            }
        }
        {
            {
                String[] assetFiles = {
                        "1.webp",
                        "2.webp",
                        "example.webp",
                        "lossless.webp",
                        "lossy.png",
                };
                for (String assetFile : assetFiles) {
                    AssetStreamLoader loader = new AssetStreamLoader(this, assetFile);
                    AnimatedWebPDrawable apngDrawable = new AnimatedWebPDrawable(loader);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageDrawable(apngDrawable);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.bottomMargin = 50;
                    layoutParams.topMargin = 50;
                    linearLayout.addView(imageView, layoutParams);
                }
            }
        }
    }
}
