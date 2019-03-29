package com.yupaopao.apngdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.app.imageloader.apng.ApngImageUtil;
import com.app.imageloader.apng.ApngLoader;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
public class APNGLib2Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnglib2);
        ApngLoader.init(getApplicationContext());
        final ImageView imageView = findViewById(R.id.imageView);
        ApngLoader.loadImage(ApngImageUtil.Scheme.ASSETS.wrap("wheel.png"), (ImageView) findViewById(R.id.imageView), null);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApngLoader.loadImage(
                        ApngImageUtil.Scheme.ASSETS.wrap("wheel.png"), imageView, null);
            }
        });
    }

}
