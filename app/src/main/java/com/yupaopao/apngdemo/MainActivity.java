package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.APNGStreamProvider;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        APNGDrawable apngDrawable = new APNGDrawable(new APNGStreamProvider() {
            @Override
            public InputStream getInputStream() throws IOException {
                return getAssets().open("wheel.png");
            }
        });
        imageView.setImageDrawable(apngDrawable);
        apngDrawable.start();
    }
}
