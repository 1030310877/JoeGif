package com.joe.giflibrary.model;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/11/11.
 */
public class GifImageBlock {
    public static final byte FLAG_IMAGE_BLOCK = 0x2c;

    private byte header;
    private short offsetX;
    private short offsetY;
    private short imageWidth;
    private short imageHeight;
    private boolean localColorTableFlag;
    private boolean interlaceFlag;
    private boolean localSortFlag;
    private byte localPixel;
    private int[] color_table;
    private byte LZWSize;
    private ArrayList<byte[]> imageEncodeData;

    public GifImageBlock() {
        header = FLAG_IMAGE_BLOCK;
        imageEncodeData = new ArrayList<>();
    }

    public int[] getColor_table() {
        return color_table;
    }

    public void setColor_table(int[] color_table) {
        this.color_table = color_table;
    }

    public ArrayList<byte[]> getImageEncodeData() {
        return imageEncodeData;
    }

    public void setImageEncodeData(ArrayList<byte[]> imageEncodeData) {
        this.imageEncodeData = imageEncodeData;
    }

    public void addImageEncodeData(byte[] imageDataBlock) {
        imageEncodeData.add(imageDataBlock);
    }

    public void setData(byte[] data) {
        if (data[0] != FLAG_IMAGE_BLOCK || data.length != 10) {
            throw new IllegalArgumentException("data bytes do not match IMAGE_BLOCK(0x2c)");
        }
        setOffsetX((short) (((data[2] & 0xff) << 8) | (data[1] & 0xff)));
        setOffsetY((short) (((data[4] & 0xff) << 8) | (data[3] & 0xff)));
        setImageWidth((short) (((data[6] & 0xff) << 8) | (data[5] & 0xff)));
        setImageHeight((short) (((data[8] & 0xff) << 8) | (data[7] & 0xff)));
        setLocalColorTableFlag((data[9] & 0b1000_0000) != 0);
        setInterlaceFlag((data[9] & 0b0100_0000) != 0);
        setLocalSortFlag((data[9] & 0b0010_0000) != 0);
        setLocalPixel((byte) (data[9] & 0x07));
//        Log.d("GifImageBlock", toString());
    }

    public byte getHeader() {
        return header;
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

    public short getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(short imageWidth) {
        this.imageWidth = imageWidth;
    }

    public short getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(short imageHeight) {
        this.imageHeight = imageHeight;
    }

    public boolean isLocalColorTableFlag() {
        return localColorTableFlag;
    }

    public void setLocalColorTableFlag(boolean localColorTableFlag) {
        this.localColorTableFlag = localColorTableFlag;
    }

    public boolean isInterlaceFlag() {
        return interlaceFlag;
    }

    public void setInterlaceFlag(boolean interlaceFlag) {
        this.interlaceFlag = interlaceFlag;
    }

    public boolean isLocalSortFlag() {
        return localSortFlag;
    }

    public void setLocalSortFlag(boolean localSortFlag) {
        this.localSortFlag = localSortFlag;
    }

    public byte getLocalPixel() {
        return localPixel;
    }

    public void setLocalPixel(byte localPixel) {
        this.localPixel = localPixel;
    }

    public void setLZWSize(byte LZWSize) {
        this.LZWSize = LZWSize;
    }

    public byte getLZWSize() {
        return LZWSize;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "Width=%d,Height=%d,OffsetX=%d,OffsetY=%d,InterlaceFlag=%b,SortFlag=%b",
                imageWidth, imageHeight, offsetX, offsetY, interlaceFlag, localSortFlag);
    }
}