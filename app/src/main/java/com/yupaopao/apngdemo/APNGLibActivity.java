package com.yupaopao.apngdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.APNGResourceLoader;

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
        final APNGAssetLoader apngAssetLoader = new APNGAssetLoader(this, "wheel.png");
        APNGDrawable apngDrawable = new APNGDrawable(apngAssetLoader);
        imageView.setImageDrawable(apngDrawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() instanceof APNGDrawable) {
                    ((APNGDrawable) imageView.getDrawable()).stop();
                }
                APNGDrawable apngDrawable = new APNGDrawable(apngAssetLoader);
                imageView.setImageDrawable(apngDrawable);
                apngDrawable.setLoopLimit(0);
            }
        });
//
//        final ImageView imageView2 = findViewById(R.id.imageView2);
//        final APNGAssetLoader apngAssetLoader2 = new APNGAssetLoader(this, "sample.png");
//        imageView2.setImageDrawable(new APNGDrawable(apngAssetLoader2));
//        imageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imageView2.getDrawable() instanceof APNGDrawable) {
//                    ((APNGDrawable) imageView2.getDrawable()).stop();
//                }
//                APNGDrawable apngDrawable = new APNGDrawable(apngAssetLoader2);
//                imageView2.setImageDrawable(apngDrawable);
//                apngDrawable.setLoopLimit(0);
//            }
//        });
    }
}
