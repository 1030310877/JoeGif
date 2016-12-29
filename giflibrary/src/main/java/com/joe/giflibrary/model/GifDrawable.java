package com.joe.giflibrary.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import com.joe.giflibrary.extend.GifExtendBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/11/10.
 */
public class GifDrawable extends Drawable {
    public static final int HEADER_LENGTH = 6;
    public static final int LOGICAL_SCREEN_DESCRIPTOR_LENGTH = 7;

    public static final String VERSION_87 = "87a";
    public static final String VERSION_89 = "89a";
    public static final byte FLAG_FILE_END = 0x3B;

    private Paint mPaint;

    private String version;
    private int logicalWidth;
    private int logicalHeight;
    private boolean globalColorTableFlag;
    private byte colorResolution;
    private boolean sortFlag;
    private byte pixel;
    private byte backgroundColorIndex;
    private byte pixelAspectRadio;
    private int[] color_table;
    private byte[] extendBlockBytes;
    private List<GifExtendBlock> extendBlocks = new ArrayList<>();
    private ArrayList<GifImagePixelModel> pic_list = new ArrayList<>();

    private Handler handler;

    private LruCache<String, Bitmap> cache;

    public GifDrawable() {
        handler = new Handler(Looper.getMainLooper());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cache = new LruCache<>(maxMemory / 8);
    }

    public int[] getColor_table() {
        return color_table;
    }

    public void setColor_table(int[] color_table) {
        this.color_table = color_table;
    }

    public byte[] getExtendBlockBytes() {
        return extendBlockBytes;
    }

    public void setExtendBlockBytes(byte[] extendBlockBytes) {
        this.extendBlockBytes = extendBlockBytes;
    }

    public void addExtendBlock(GifExtendBlock block) {
        extendBlocks.add(block);
    }

    public List<GifExtendBlock> getExtendBlocks() {
        return extendBlocks;
    }

    public void setExtendBlocks(List<GifExtendBlock> extendBlocks) {
        this.extendBlocks = extendBlocks;
    }

    public byte getPixelAspectRadio() {
        return pixelAspectRadio;
    }

    public void setPixelAspectRadio(byte pixelAspectRadio) {
        this.pixelAspectRadio = pixelAspectRadio;
    }

    public byte getBackgroundColorIndex() {
        return backgroundColorIndex;
    }

    public void setBackgroundColorIndex(byte backgroundColorIndex) {
        this.backgroundColorIndex = backgroundColorIndex;
    }

    public boolean isGlobalColorTableFlag() {
        return globalColorTableFlag;
    }

    public void setGlobalColorTableFlag(boolean globalColorTableFlag) {
        this.globalColorTableFlag = globalColorTableFlag;
    }

    public byte getColorResolution() {
        return colorResolution;
    }

    public void setColorResolution(byte colorResolution) {
        this.colorResolution = colorResolution;
    }

    public boolean isSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(boolean sortFlag) {
        this.sortFlag = sortFlag;
    }

    public byte getPixel() {
        return pixel;
    }

    public void setPixel(byte pixel) {
        this.pixel = pixel;
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    public void setLogicalWidth(int logicalWidth) {
        this.logicalWidth = logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }

    public void setLogicalHeight(int logicalHeight) {
        this.logicalHeight = logicalHeight;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<GifImagePixelModel> getImageDecodeData() {
        return pic_list;
    }

    public void addImageDecodeData(GifImageBlock imageBlock, int[] decode) {
        GifImagePixelModel model = new GifImagePixelModel();
        model.setWidth(imageBlock.getImageWidth());
        model.setHeight(imageBlock.getImageHeight());
        model.setOffsetX(imageBlock.getOffsetX());
        model.setOffsetY(imageBlock.getOffsetY());
        model.setData(decode);
        pic_list.add(model);
    }

    private int currentIndex = -1;

    @Override
    public void draw(Canvas canvas) {
        //TODO
        currentIndex++;
        if (pic_list.size() == 0) {
            return;
        }
        if (currentIndex >= pic_list.size()) {
            currentIndex = 0;
        }
        GifImagePixelModel model = pic_list.get(currentIndex);
        drawGif(canvas, model);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidateSelf();
            }
        }, model.getDelayTime());
    }

    private void drawGif(Canvas canvas, GifImagePixelModel model) {
        int width = model.getWidth();
        int height = model.getHeight();
        String key = String.format(Locale.getDefault(), "%d*%d", width, height);
        Bitmap bitmap = cache.get(key);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(model.getWidth(), model.getHeight(), Bitmap.Config.ARGB_8888);
            cache.put(key, bitmap);
        }
        bitmap.setPixels(model.getData(), 0, model.getWidth(), 0, 0, model.getWidth(), model.getHeight());
        canvas.drawBitmap(bitmap, model.getOffsetX(), model.getOffsetY(), mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return getLogicalWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return getLogicalHeight();
    }
}