package com.joe.giflibrary;

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
import com.joe.giflibrary.model.GifImageBlock;
import com.joe.giflibrary.model.GifImagePixelModel;

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

    private boolean isReadFinished = false;

    private String version;
    private short logicalWidth;
    private short logicalHeight;
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
    private ArrayList<GifImageBlock> imageBlocks = new ArrayList<>();

    private Handler handler;

    private LruCache<String, Bitmap> cache;

    private boolean isLowMemory = false;

    public GifDrawable() {
        handler = new Handler(Looper.getMainLooper());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cache = new LruCache<>(maxMemory / 8);
    }

    public boolean isReadFinished() {
        return isReadFinished;
    }

    public void setReadFinished(boolean readFinished) {
        isReadFinished = readFinished;
    }

    public boolean isLowMemory() {
        return isLowMemory;
    }

    public void setLowMemory(boolean lowMemory) {
        isLowMemory = lowMemory;
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

    public void clearExtendBlocks() {
        if (extendBlocks != null) {
            extendBlocks.clear();
        }
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

    public short getLogicalWidth() {
        return logicalWidth;
    }

    public void setLogicalWidth(short logicalWidth) {
        this.logicalWidth = logicalWidth;
    }

    public short getLogicalHeight() {
        return logicalHeight;
    }

    public void setLogicalHeight(short logicalHeight) {
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
        model.setDisposalMethod(GifDecoder.tempDisposalMethod);
        pic_list.add(model);
    }

    public ArrayList<GifImageBlock> getImageBlocks() {
        return imageBlocks;
    }

    public void addImageBlock(GifImageBlock block) {
        block.setDisposalMethod(GifDecoder.tempDisposalMethod);
        imageBlocks.add(block);
    }

    public void clearImageBlocks() {
        imageBlocks.clear();
    }

    private short currentIndex = -1;

    private Bitmap lastBitmap;
    private Canvas saveCanvas;

    private GifImagePixelModel tempModel;
    private boolean isTempDrawn = true;

    private Thread decodeThread;

    @Override
    public void draw(Canvas canvas) {
        if (isLowMemory) {
            if (tempModel == null) {
                tempModel = new GifImagePixelModel();
            }
            if (lastBitmap == null) {
                lastBitmap = Bitmap.createBitmap(logicalWidth, logicalHeight, Bitmap.Config.ARGB_8888);
                saveCanvas = new Canvas(lastBitmap);
            }
            canvas.drawBitmap(lastBitmap, 0, 0, mPaint);
            if (!isTempDrawn && decodeThread == null) {
                drawGif(canvas, tempModel);
                isTempDrawn = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invalidateSelf();
                    }
                }, tempModel.getDelayTime());
                return;
            } else {
                drawGif(canvas, tempModel);
                isTempDrawn = true;
            }
            if (decodeThread == null) {
                currentIndex++;
                if (currentIndex >= imageBlocks.size()) {
                    if (isReadFinished) {
                        currentIndex = 0;
                    } else {
                        currentIndex--;
                        return;
                    }
                }
                decodeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isTempDrawn = false;
                        GifImageBlock block = imageBlocks.get(currentIndex);
                        tempModel.setWidth(block.getImageWidth());
                        tempModel.setHeight(block.getImageHeight());
                        tempModel.setOffsetX(block.getOffsetX());
                        tempModel.setOffsetY(block.getOffsetY());
                        tempModel.setDisposalMethod(block.getDisposalMethod());
                        tempModel.setDelayTime(block.getDelayTime());
                        tempModel.setData(GifDecoder.decodeImageBlock(GifDrawable.this, block));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                invalidateSelf();
                            }
                        });
                        decodeThread = null;
                    }
                });
                decodeThread.start();
            }
        } else {
            if (pic_list.size() == 0) {
                if (!isReadFinished) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            invalidateSelf();
                        }
                    }, 160);
                }
                return;
            }
            if (lastBitmap == null) {
                lastBitmap = Bitmap.createBitmap(logicalWidth, logicalHeight, Bitmap.Config.ARGB_8888);
                saveCanvas = new Canvas(lastBitmap);
            }
            canvas.drawBitmap(lastBitmap, 0, 0, mPaint);
            currentIndex++;
            if (currentIndex >= pic_list.size()) {
                if (isReadFinished) {
                    currentIndex = 0;
                } else {
                    currentIndex = (short) (pic_list.size() - 1);
                }
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
    }

    private void drawGif(Canvas canvas, GifImagePixelModel model) {
        int width = model.getWidth();
        int height = model.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        String key = String.format(Locale.getDefault(), "%d*%d", width, height);
        Bitmap bitmap = cache.get(key);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(model.getWidth(), model.getHeight(), Bitmap.Config.ARGB_8888);
            cache.put(key, bitmap);
        }
        bitmap.setPixels(model.getData(), 0, model.getWidth(), 0, 0, model.getWidth(), model.getHeight());
        canvas.drawBitmap(bitmap, model.getOffsetX(), model.getOffsetY(), mPaint);
        if (model.getDisposalMethod() == (byte) 0x01) {
            saveCanvas.drawBitmap(bitmap, model.getOffsetX(), model.getOffsetY(), mPaint);
        }
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