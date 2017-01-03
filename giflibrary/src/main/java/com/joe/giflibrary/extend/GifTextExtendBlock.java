package com.joe.giflibrary.extend;

/**
 * Description
 * Created by chenqiao on 2016/11/11.
 */
public class GifTextExtendBlock extends GifExtendBlock {
    private short offsetX;
    private short offsetY;
    private short width;
    private short height;
    private short cellWidth;
    private short cellHeight;
    private short foregroundColorIndex;
    private short backgroundColorIndex;

    public short getOffsetX() {
        return offsetX;
    }

    public short getOffsetY() {
        return offsetY;
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public short getCellWidth() {
        return cellWidth;
    }

    public short getCellHeight() {
        return cellHeight;
    }

    public short getForegroundColorIndex() {
        return foregroundColorIndex;
    }

    public short getBackgroundColorIndex() {
        return backgroundColorIndex;
    }

    public GifTextExtendBlock() {
        super();
        setType(GifExtendBlock.LABEL_TEXT_EXTEND_BLOCK);
    }

    @Override
    public void setControlData(byte[] controlData) {
        offsetX = (short) (((controlData[1] & 0xff) << 8) | (controlData[0] & 0xff));
        offsetY = (short) (((controlData[3] & 0xff) << 8) | (controlData[2] & 0xff));
        width = (short) (((controlData[5] & 0xff) << 8) | (controlData[4] & 0xff));
        height = (short) (((controlData[7] & 0xff) << 8) | (controlData[6] & 0xff));
        cellWidth = (short) (controlData[8] & 0xff);
        cellHeight = (short) (controlData[9] & 0xff);
        foregroundColorIndex = (short) (controlData[10] & 0xff);
        backgroundColorIndex = (short) (controlData[11] & 0xff);
    }
}