package com.joe.giflibrary;

import com.joe.giflibrary.model.GifDrawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description
 * Created by chenqiao on 2016/11/10.
 */
public class GifFactory {

    public static GifDrawable readGifResource(InputStream gifIn) throws IOException {
        GifDrawable drawable = new GifDrawable();
        if (GifDecoder.isGif(drawable, GifDecoder.readHeader(gifIn))) {
            GifDecoder.setGifParams(drawable, GifDecoder.readGifParamsBlock(gifIn));
            if (drawable.isGlobalColorTableFlag()) {
                GifDecoder.setGlobalColorTable(drawable, gifIn);
            }
            GifDecoder.readDataStream(drawable, gifIn);
        }
        gifIn.close();
        return drawable;
    }
}