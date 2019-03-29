package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.yupaopao.animation.apng.APNGAssetLoader;
import com.yupaopao.animation.apng.APNGDrawable;
import com.yupaopao.animation.apng.APNGResourceLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() instanceof APNGDrawable) {
                    ((APNGDrawable) imageView.getDrawable()).stop();
                }
                APNGDrawable apngDrawable = new APNGDrawable(
                        new APNGResourceLoader(MainActivity.this, R.drawable.sample));
                imageView.setImageDrawable(apngDrawable);
                apngDrawable.setLoopLimit(0);
            }
        });
        if (imageView.getDrawable() instanceof APNGDrawable) {
            ((APNGDrawable) imageView.getDrawable()).stop();
        }
        APNGDrawable apngDrawable = new APNGDrawable(
                new APNGResourceLoader(MainActivity.this, R.drawable.wheel));
        imageView.setImageDrawable(apngDrawable);
        APNGAssetLoader apngAssetLoader = new APNGAssetLoader(MainActivity.this,
                "wheel.png");
        Log.d("test", "wheel check apng" + apngAssetLoader.isAPNG());
    }
}
