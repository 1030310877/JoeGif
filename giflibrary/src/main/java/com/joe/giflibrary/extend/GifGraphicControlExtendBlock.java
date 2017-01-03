package com.joe.giflibrary.extend;

import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/11/11.
 */
public class GifGraphicControlExtendBlock extends GifExtendBlock {

    private byte disposalMethod = 0x00;
    private boolean inputFlag = false;
    private boolean transparentColorFlag = false;
    private short transparentColorIndex = -1;
    private short delayTime = 0;

    public GifGraphicControlExtendBlock() {
        super();
        setType(GifExtendBlock.LABEL_GRAPHIC_CONTROL_EXTEND_BLOCK);
    }

    @Override
    public void setControlData(byte[] controlData) {
        disposalMethod = (byte) ((controlData[0] & 0b0001_1100) >> 2);
        transparentColorFlag = (controlData[0] & 0x01) != 0;
        inputFlag = (controlData[0] & 0x02) != 0;
        delayTime = (short) ((((controlData[2] & 0xff) << 8) | (controlData[1] & 0xff)) * 10);
        transparentColorIndex = (short) (controlData[3] & 0xff);
//        Log.d("GraphicControlBlock", toString());
    }

    public byte getDisposalMethod() {
        return disposalMethod;
    }

    public boolean isInputFlag() {
        return inputFlag;
    }

    public boolean isTransparentColorFlag() {
        return transparentColorFlag;
    }

    public short getTransparentColorIndex() {
        return transparentColorIndex;
    }

    public short getDelayTime() {
        return delayTime;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "DisposalMethod=%d,InputFlag=%b,transparentColorFlag=%b,transparentColorIndex=%d", disposalMethod, inputFlag, transparentColorFlag, transparentColorIndex);
    }
}