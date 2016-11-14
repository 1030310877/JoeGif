package com.joe.giflibrary.extend;

import java.util.ArrayList;

/**
 * Description Gif扩展块
 * Created by chenqiao on 2016/11/11.
 */
public class GifExtendBlock {

    public static final byte FLAG_EXTEND_BLOCK = 0x21;
    public static final byte LABEL_ANNOTATION_EXTEND_BLOCK = (byte) 0xFE;
    public static final byte LABEL_APP_EXTEND_BLOCK = (byte) 0xFF;
    public static final byte LABEL_GRAPHIC_CONTROL_EXTEND_BLOCK = (byte) 0xF9;
    public static final byte LABEL_TEXT_EXTEND_BLOCK = 0x01;

    private byte type;
    private int size;
    private byte[] controlData;
    private ArrayList<byte[]> dataSubBlocks;

    public byte[] getControlData() {
        return controlData;
    }

    public void setControlData(byte[] controlData) {
        this.controlData = controlData;
    }

    public ArrayList<byte[]> getDataSubBlocks() {
        return dataSubBlocks;
    }

    public void setDataSubBlocks(ArrayList<byte[]> dataSubBlocks) {
        this.dataSubBlocks = dataSubBlocks;
    }

    public void addDataSubBlock(byte[] dataSubBlock) {
        dataSubBlocks.add(dataSubBlock);
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public GifExtendBlock() {
        dataSubBlocks = new ArrayList<>();
    }
}