package com.yupaopao.apngdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.APNGFileLoader;
import com.yupaopao.animation.apng.APNGResourceLoader;
import com.yupaopao.animation.apng.chunk.APNGDecoder;

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
        final ImageView imageView = findViewById(R.id.imageView);
        final APNGAssetLoader assetLoader = new APNGAssetLoader(this, "wheel.png");
        final APNGResourceLoader resourceLoader = new APNGResourceLoader(APNGLibActivity.this, R.drawable.sample);
        final APNGFileLoader fileLoader = new APNGFileLoader("/sdcard/Pictures/wheel.png");
        imageView.setImageDrawable(
                new APNGDrawable(
                        assetLoader,
                        true,
                        APNGDecoder.Mode.MODE_SPEED));

        final ImageView imageView2 = findViewById(R.id.imageView2);
        imageView2.setImageDrawable(
                new APNGDrawable(
                        assetLoader,
                        true,
                        APNGDecoder.Mode.MODE_BALANCED));

        final ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setImageDrawable(
                new APNGDrawable(
                        assetLoader, true,
                        APNGDecoder.Mode.MODE_MEMORY));
    }
//    imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView.setImageDrawable(
//                        new APNGDrawable(
//                                new APNGFileLoader("/sdcard/Pictures/wheel.png"),
//                                true,
//                                APNGDecoder.Mode.MODE_BALANCED));
//            }
//        });
}
