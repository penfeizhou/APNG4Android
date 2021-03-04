package com.github.penfeizhou.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TestMemorySizeActivity extends Activity {

    private static final String TAG = "TestMemorySizeActivity";

    private int index;
    String[] urls = new String[]{
            "https://misc.aotu.io/ONE-SUNDAY/apng_spinfox.png",
            "https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png"
    };

    private ImageView apng_image1;
    private TextView txtClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnglib);
        txtClear = findViewById(R.id.txtClear);
        apng_image1 = findViewById(R.id.apng_image1);

        txtClear.postDelayed(runnable, 2000 * 1);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            updateImageView(apng_image1);
            txtClear.postDelayed(runnable, 100 * 1);
        }
    };

    private String getImageUrl() {
        index++;
        return urls[index % urls.length];
    }

    private void updateImageView(ImageView apng_image) {
        String imageUrl = getImageUrl();
        Glide.with(TestMemorySizeActivity.this).load(imageUrl).into(apng_image);
        Log.d(TAG, "updateImageView imageUrl:" + imageUrl);
    }
}
