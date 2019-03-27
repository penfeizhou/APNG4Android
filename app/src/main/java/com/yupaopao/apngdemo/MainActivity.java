package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.ApngImageUtil;
import com.yupaopao.animation.apng.ApngLoader;
import com.yupaopao.animation.apng.chunk.ApngReader;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApngLoader.init(getApplicationContext());
        ApngLoader.loadImage(ApngImageUtil.Scheme.ASSETS.wrap("wheel.png"), (ImageView) findViewById(R.id.imageView), null);
        final ImageView imageView = findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApngLoader.loadRepeatCountImage(
                        ApngImageUtil.Scheme.ASSETS.wrap("wheel.png"), imageView, 1, null);
            }
        });
        try {
            InputStream inputStream = getAssets().open("sample.png");
            ApngReader apngReader = new ApngReader(inputStream);
            apngReader.work();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
