package com.joe.gif.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.joe.giflibrary.GifFactory;
import com.joe.giflibrary.model.GifDrawable;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.img);
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in;
                try {
                    in = getAssets().open("my.gif");
                    final GifDrawable drawable = GifFactory.readGifResource(in);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgView.setImageDrawable(drawable);
                        }
                    }, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}