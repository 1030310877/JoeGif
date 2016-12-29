package com.joe.giflibrary.model;

import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/12/13.
 */
public class GifImagePixelModel {
    private int width;
    private int height;
    private int[] data;
    private short delayTime;
    private int offsetX;
    private int offsetY;
    private byte disposalMethod;

    public GifImagePixelModel() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public short getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(short delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "Width=%d,Height=%d,OffsetX=%d,OffsetY=%d,DelayTime=%d",
                width, height, offsetX, offsetY, delayTime);
    }

    public void setDisposalMethod(byte disposalMethod) {
        this.disposalMethod = disposalMethod;
    }

    public byte getDisposalMethod() {
        return disposalMethod;
    }
}