package com.joe.giflibrary;

import android.util.Log;

import com.joe.giflibrary.model.GifDrawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description
 * Created by chenqiao on 2016/11/10.
 */
public class GifFactory {

    static GifDrawable readGifResource(InputStream gifIn) {
        GifDrawable drawable = new GifDrawable();
        return readGifResource(drawable, gifIn);
    }

    static GifDrawable readGifResource(GifDrawable drawable, InputStream gifIn) {
        if (drawable == null) {
            drawable = new GifDrawable();
        }
        try {
            if (GifDecoder.isGif(drawable, GifDecoder.readHeader(gifIn))) {
                GifDecoder.setGifParams(drawable, GifDecoder.readGifParamsBlock(gifIn));
                if (drawable.isGlobalColorTableFlag()) {
                    GifDecoder.setGlobalColorTable(drawable, gifIn);
                }
                GifDecoder.readDataStream(drawable, gifIn);
            }
            gifIn.close();
        } catch (IOException e) {
            Log.e("GifFactory", "readGifResource: ", e);
        }
        return drawable;
    }
}