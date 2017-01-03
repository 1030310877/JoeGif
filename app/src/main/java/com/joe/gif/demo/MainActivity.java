package com.joe.gif.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joe.giflibrary.GifImageView;

public class MainActivity extends AppCompatActivity {

    private GifImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (GifImageView) findViewById(R.id.img);
    }
}