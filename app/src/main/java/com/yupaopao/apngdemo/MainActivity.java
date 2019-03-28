package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGDrawable;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        try {
            InputStream inputStream = getAssets().open("wheel.png");
            APNGDrawable apngDrawable = new APNGDrawable(inputStream);
            imageView.setImageDrawable(apngDrawable);
            apngDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
