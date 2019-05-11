package com.yupaopao.apngdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.webp.AnimatedWebpDrawable;
import com.yupaopao.animation.webp.AssetStreamLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

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
        for (String assetFile : assetFiles) {
            APNGAssetLoader loader = new APNGAssetLoader(this, assetFile);
            APNGDrawable apngDrawable = new APNGDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(apngDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
        {
            ImageView imageView = new ImageView(this);
            InputStream inputStream = null;
            try {
                inputStream = getAssets().open("example.webp");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = 50;
                layoutParams.topMargin = 50;
                linearLayout.addView(imageView, layoutParams);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        {
            AssetStreamLoader loader = new AssetStreamLoader(this, "animation.webp");
            Log.d("test", "" + loader.isAnimatedWebp());
            AnimatedWebpDrawable webpDrawable = new AnimatedWebpDrawable(loader);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(webpDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 50;
            layoutParams.topMargin = 50;
            linearLayout.addView(imageView, layoutParams);
        }
        //https://yphoto.eryufm.cn/upload/86ced8c0-4cd5-4080-abc7-96a45a150f9c.jpg?imageView2/format/webp
        //https://yvideo.eryufm.cn/video/gif/26f3ff37-4db3-4666-8001-6acaaee1a13c.gif?imageView2/0/format/webp
    }
}
