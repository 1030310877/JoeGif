package com.joe.giflibrary.model;

import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/12/13.
 */
public class GifImagePixelModel {
    private short width;
    private short height;
    private int[] data;
    private short delayTime;
    private short offsetX;
    private short offsetY;
    private byte disposalMethod;

    public GifImagePixelModel() {
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public short getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(short offsetX) {
        this.offsetX = offsetX;
    }

    public short getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(short offsetY) {
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