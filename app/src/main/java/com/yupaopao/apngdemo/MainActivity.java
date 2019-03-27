package com.yupaopao.apngdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.yupaopao.apng.ApngImageUtil;
import com.yupaopao.apng.ApngLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApngLoader.init(getApplicationContext());
        ApngLoader.loadImage(ApngImageUtil.Scheme.ASSETS.wrap("sample.png"), (ImageView) findViewById(R.id.imageView), null);
    }
}
