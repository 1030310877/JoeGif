package com.joe.giflibrary.model;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.joe.giflibrary.extend.GifExtendBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/11/10.
 */
public class GifDrawable extends Drawable {
    public static final int HEADER_LENGTH = 6;
    public static final int LOGICAL_SCREEN_DESCRIPTOR_LENGTH = 7;

    public static final String VERSION_87 = "87a";
    public static final String VERSION_89 = "89a";

    private String version;
    private int logicalWidth;
    private int logicalHeight;
    private boolean globalColorTableFlag;
    private byte colorResolution;
    private boolean sortFlag;
    private byte pixel;
    private byte backgroundColorIndex;
    private byte pixelAspectRadio;
    private byte[] color_table_r;
    private byte[] color_table_g;
    private byte[] color_table_b;
    private byte[] extendBlockBytes;
    private List<GifExtendBlock> extendBlocks = new ArrayList<>();
    private List<GifImageBlock> imageBlocks = new ArrayList<>();

    public void addImageBlock(GifImageBlock block) {
        imageBlocks.add(block);
    }

    public List<GifImageBlock> getImageBlocks() {
        return imageBlocks;
    }

    public void setImageBlocks(List<GifImageBlock> imageBlocks) {
        this.imageBlocks = imageBlocks;
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

    public byte[] getColor_table_r() {
        return color_table_r;
    }

    public void setColor_table_r(byte[] color_table_r) {
        this.color_table_r = color_table_r;
    }

    public byte[] getColor_table_g() {
        return color_table_g;
    }

    public void setColor_table_g(byte[] color_table_g) {
        this.color_table_g = color_table_g;
    }

    public byte[] getColor_table_b() {
        return color_table_b;
    }

    public void setColor_table_b(byte[] color_table_b) {
        this.color_table_b = color_table_b;
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

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}