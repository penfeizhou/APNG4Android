package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        APNGDrawable apngDrawable = new APNGDrawable(new APNGAssetLoader(this, "wheel.png"));
        imageView.setImageDrawable(apngDrawable);
        apngDrawable.start();
    }
}
