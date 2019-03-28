package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APNGDrawable apngDrawable = new APNGDrawable(
                        new APNGAssetLoader(MainActivity.this,
                                "feeds_apng_like_red.png"));
                imageView.setImageDrawable(apngDrawable);
            }
        });
    }
}
