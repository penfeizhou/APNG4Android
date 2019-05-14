package com.yupaopao.animation.demo;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                100);

        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
        findViewById(R.id.tv_5).setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_1: {
                Intent intent = new Intent(this, AnimationTestActivity.class);
                intent.putExtra("files", new String[]{
                        "wheel.png",
                });
                startActivity(intent);
            }
            break;
            case R.id.tv_2: {
                Intent intent = new Intent(this, AnimationTestActivity.class);
                intent.putExtra("files", new String[]{
                        "test.png",
                });
                startActivity(intent);
            }
            break;
            case R.id.tv_3: {
                Intent intent = new Intent(this, AnimationTestActivity.class);
                intent.putExtra("files", new String[]{
                        "1.webp",
                });
                startActivity(intent);
            }
            break;
            case R.id.tv_4: {
                Intent intent = new Intent(this, AnimationTestActivity.class);
                intent.putExtra("files", new String[]{
                        "lossless.webp",
                });
                startActivity(intent);
            }
            break;
            case R.id.tv_5: {
                Intent intent = new Intent(this, APNGTestActivity.class);
                startActivity(intent);
            }
        }
    }
}
