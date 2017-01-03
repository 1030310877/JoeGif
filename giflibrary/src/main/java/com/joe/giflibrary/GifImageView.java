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

    private boolean isLowMemory;

    private boolean asyncShow = true;

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
        asyncShow = a.getBoolean(R.styleable.GifImageView_syncShow, true);
        isLowMemory = a.getBoolean(R.styleable.GifImageView_lowMemory, false);
        drawable = new GifDrawable();
        drawable.setLowMemory(isLowMemory);
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
        if (asyncShow) {
            postDelayed(displayRunnable, 100);
        }
    }

    private class DealThread extends Thread {
        private InputStream inputStream;

        @Override
        public void run() {
            if (gifSrcId > 0) {
                inputStream = getResources().openRawResource(gifSrcId);
                GifFactory.readGifResource(drawable, inputStream);
                if (!isInterrupted() && drawable != null) {
                    postDelayed(displayRunnable, 100);
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (inputStream != null) {
                try {
                    inputStream.close();
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