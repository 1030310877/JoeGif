package com.joe.giflibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.joe.giflibrary.model.GifDrawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description
 * Created by chenqiao on 2016/12/30.
 */
public class GifImageView extends ImageView {

    private int gifSrcId = -1;

    private DealThread thread;

    private GifDrawable drawable;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifImageView);
        gifSrcId = a.getResourceId(R.styleable.GifImageView_srcId, -1);
        a.recycle();
        readGifResource();
    }

    public void setGifImageResource(@DrawableRes int resId) {
        gifSrcId = resId;
        readGifResource();
    }

    private void readGifResource() {
        if (gifSrcId > 0) {
            if (thread != null) {
                thread.interrupt();
            }
            thread = new DealThread();
            thread.start();
        }
    }

    private class DealThread extends Thread {
        @Override
        public void run() {
            if (gifSrcId > 0) {
                InputStream inputStream = getResources().openRawResource(gifSrcId);
                try {
                    GifDrawable temp = GifFactory.readGifResource(inputStream);
                    if (!isInterrupted()) {
                        drawable = temp;
                        postDelayed(displayRunnable, 100);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable displayRunnable = new Runnable() {
        @Override
        public void run() {
            if (drawable != null) {
                setImageDrawable(drawable);
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (thread != null) {
            thread.interrupt();
            removeCallbacks(displayRunnable);
            drawable = null;
        }
    }
}