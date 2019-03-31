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
public class APNGTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apngtest);
        final ImageView imageView = findViewById(R.id.imageView);
        final APNGAssetLoader assetLoader = new APNGAssetLoader(this, "wheel.png");
        final int mode = getIntent().getIntExtra("mode", 0);
        imageView.setImageDrawable(
                new APNGDrawable(
                        assetLoader,
                        mode == 0 ? APNGDecoder.Mode.MODE_SPEED
                                : (mode == 1 ? APNGDecoder.Mode.MODE_BALANCED
                                : APNGDecoder.Mode.MODE_MEMORY)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(
                        new APNGDrawable(
                                assetLoader,
                                mode == 0 ? APNGDecoder.Mode.MODE_SPEED
                                        : (mode == 1 ? APNGDecoder.Mode.MODE_BALANCED
                                        : APNGDecoder.Mode.MODE_MEMORY)));
            }
        });
    }
}
