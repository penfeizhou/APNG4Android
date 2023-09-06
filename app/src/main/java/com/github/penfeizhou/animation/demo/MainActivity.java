package com.github.penfeizhou.animation.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
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
        findViewById(R.id.tv_6).setOnClickListener(this);
        findViewById(R.id.tv_7).setOnClickListener(this);
        findViewById(R.id.tv_8).setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_1) {
            Intent intent = new Intent(this, AnimationTestActivity.class);
            intent.putExtra("files", new String[]{
                    "apng_detail_guide.png",
            });
            startActivity(intent);
        } else if (id == R.id.tv_2) {
            Intent intent = new Intent(this, AnimationTestActivity.class);
            intent.putExtra("files", new String[]{
                    "test.png",
            });
            startActivity(intent);
        } else if (id == R.id.tv_3) {
            Intent intent = new Intent(this, AnimationTestActivity.class);
            intent.putExtra("files", new String[]{
                    "world-cup.webp",
            });
            startActivity(intent);
        } else if (id == R.id.tv_4) {
            Intent intent = new Intent(this, AnimationTestActivity.class);
            intent.putExtra("files", new String[]{
                    "lossless.webp",
            });
            startActivity(intent);
        } else if (id == R.id.tv_5) {
            Intent intent = new Intent(this, APNGTestActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_6) {
            Intent intent = new Intent(this, AnimationTestActivity.class);
            intent.putExtra("files", new String[]{
                    "world-cup.gif",
                    "1.gif",
                    "2.gif",
                    "3.gif",
                    "4.gif",
                    "5.gif",
                    "6.gif",
            });
            startActivity(intent);
        } else if (id == R.id.tv_7) {
            Intent intent = new Intent(this, EncoderTestActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_8) {
            Intent intent = new Intent(this, APNGRecyclerViewTestActivity.class);
            startActivity(intent);
        }
    }
}
