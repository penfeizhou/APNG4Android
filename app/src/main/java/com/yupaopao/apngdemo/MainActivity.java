package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.ApngImageUtil;
import com.yupaopao.animation.apng.ApngLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApngLoader.init(getApplicationContext());
        ApngLoader.loadImage(ApngImageUtil.Scheme.ASSETS.wrap("sample.png"), (ImageView) findViewById(R.id.imageView), null);
        final ImageView imageView = findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApngLoader.loadRepeatCountImage(
                        ApngImageUtil.Scheme.ASSETS.wrap("feeds_apng_like_red.png"), imageView, 1, null);
            }
        });
    }
}
