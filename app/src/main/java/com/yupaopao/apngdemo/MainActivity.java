package com.yupaopao.apngdemo;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yupaopao.animation.webp.AssetStreamLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);
            }
        });
        findViewById(R.id.tv_mode1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, APNGTestActivity.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_mode2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, APNGTestActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_mode3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, APNGTestActivity.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
            }
        });
        AssetStreamLoader loader = new AssetStreamLoader(this, "animation.webp");
        Log.d("test", "" + loader.isAnimatedWebp());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent(MainActivity.this, APNGLibActivity.class);
        startActivity(intent);
    }
}
