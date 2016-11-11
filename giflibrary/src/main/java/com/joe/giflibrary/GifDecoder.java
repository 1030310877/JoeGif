package com.joe.giflibrary;

import com.joe.giflibrary.extend.GifAnnotationExtendBlock;
import com.joe.giflibrary.extend.GifAppExtendBlock;
import com.joe.giflibrary.extend.GifExtendBlock;
import com.joe.giflibrary.extend.GifGraphicControlExtendBlock;
import com.joe.giflibrary.extend.GifTextExtendBlock;
import com.joe.giflibrary.model.GifDrawable;
import com.joe.giflibrary.model.GifImageBlock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/11/11.
 */
class GifDecoder {

    static boolean isGif(GifDrawable drawable, byte[] header) {
        if (header.length == GifDrawable.HEADER_LENGTH) {
            if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F') {
                String version = String.format(Locale.getDefault(), "%c%c%c", header[3], header[4], header[5]);
                switch (version) {
                    case GifDrawable.VERSION_87:
                        drawable.setVersion(GifDrawable.VERSION_87);
                        break;
                    case GifDrawable.VERSION_89:
                        drawable.setVersion(GifDrawable.VERSION_89);
                        break;
                    default:
                        return false;
                }
            }
        }
        return true;
    }

    static void setGifParams(GifDrawable drawable, byte[] screenDescriptor) {
        if (screenDescriptor.length == GifDrawable.LOGICAL_SCREEN_DESCRIPTOR_LENGTH) {
            int width = ((screenDescriptor[1] & 0xff) << 8 | screenDescriptor[0]);
            int height = ((screenDescriptor[3] & 0xff) << 8 | screenDescriptor[2]);
            drawable.setLogicalWidth(width);
            drawable.setLogicalHeight(height);
            drawable.setGlobalColorTableFlag((screenDescriptor[4] & 0b1000_0000) != 0);
            drawable.setColorResolution((byte) ((screenDescriptor[4] & 0b0111_0000) >> 3));
            drawable.setSortFlag((screenDescriptor[4] & 0b0000_1000) != 0);
            drawable.setPixel((byte) (screenDescriptor[4] & 0b0000_0111));
            drawable.setBackgroundColorIndex(screenDescriptor[5]);
            drawable.setPixelAspectRadio(screenDescriptor[6]);
        }
    }

    static void setGlobalColorTable(GifDrawable drawable, InputStream gifIn) throws IOException {
        int index = (int) Math.pow(2, 1 + (drawable.getPixel() & 0xff));
        byte[] r = new byte[index];
        byte[] g = new byte[index];
        byte[] b = new byte[index];
        for (int i = 0; i < index; i++) {
            r[i] = (byte) gifIn.read();
            g[i] = (byte) gifIn.read();
            b[i] = (byte) gifIn.read();
        }
        drawable.setColor_table_r(r);
        drawable.setColor_table_g(g);
        drawable.setColor_table_b(b);
    }

    static byte readDataStream(GifDrawable drawable, InputStream gifIn) throws IOException {
        byte head;
        while ((head = (byte) gifIn.read()) >= 0) {
            switch (head) {
                case GifExtendBlock.FLAG_EXTEND_BLOCK:
                    addExtendBlocks(drawable, gifIn);
                    break;
                case GifImageBlock.FLAG_IMAGE_BLOCK:
                    addImageBlock(drawable, gifIn);
                    break;
            }
        }
        return head;
    }

    private static void addImageBlock(GifDrawable drawable, InputStream gifIn) throws IOException {
        GifImageBlock block = new GifImageBlock();
        byte[] imageDescriptor = new byte[10];
        imageDescriptor[0] = GifImageBlock.FLAG_IMAGE_BLOCK;
        gifIn.read(imageDescriptor, 1, 9);
        block.setData(imageDescriptor);
        if (block.isLocalColorTableFlag()) {
            int index = (int) Math.pow(2, 1 + (block.getLocalPixel() & 0xff));
            byte[] r = new byte[index];
            byte[] g = new byte[index];
            byte[] b = new byte[index];
            for (int i = 0; i < index; i++) {
                r[i] = (byte) gifIn.read();
                g[i] = (byte) gifIn.read();
                b[i] = (byte) gifIn.read();
            }
            block.setColor_table_r(r);
            block.setColor_table_g(g);
            block.setColor_table_b(b);
        }
    }

    private static void addExtendBlocks(GifDrawable drawable, InputStream gifIn) throws IOException {
        byte type = (byte) gifIn.read();
        GifExtendBlock extendBlock = null;
        switch (type) {
            case GifExtendBlock.LABEL_ANNOTATION_EXTEND_BLOCK:
                extendBlock = new GifAnnotationExtendBlock();
                readAnnotationExtendBlock((GifAnnotationExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_APP_EXTEND_BLOCK:
                extendBlock = new GifAppExtendBlock();
                readAppExtendBlock((GifAppExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_GRAPHIC_CONTROL_EXTEND_BLOCK:
                extendBlock = new GifGraphicControlExtendBlock();
                readGraphicControlExtendBlock((GifGraphicControlExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_TEXT_EXTEND_BLOCK:
                extendBlock = new GifTextExtendBlock();
                readTextExtendBlock((GifTextExtendBlock) extendBlock, gifIn);
                break;
        }
        if (extendBlock != null) {
            drawable.addExtendBlock(extendBlock);
        }
    }

    private static void readTextExtendBlock(GifTextExtendBlock extendBlock, InputStream gifIn) throws IOException {
        byte size = (byte) gifIn.read();
        extendBlock.setSize(size - 1);//固定值13(包含size)
        byte[] controlData = new byte[size];
        gifIn.read(controlData);
        byte[] dataSubBlocks = readDataBlock(gifIn);
        extendBlock.setControlData(controlData);
        extendBlock.setDataSubBlocks(dataSubBlocks);
    }

    private static void readGraphicControlExtendBlock(GifGraphicControlExtendBlock extendBlock, InputStream gifIn) throws IOException {
        byte size = (byte) gifIn.read();
        extendBlock.setSize(size - 1);//固定值4
        byte[] controlData = new byte[size - 1];
        gifIn.read(controlData);
        byte[] dataSubBlocks = readDataBlock(gifIn);
        extendBlock.setControlData(controlData);
        extendBlock.setDataSubBlocks(dataSubBlocks);
    }

    private static void readAppExtendBlock(GifAppExtendBlock extendBlock, InputStream gifIn) throws IOException {
        byte size = (byte) gifIn.read();
        extendBlock.setSize(size - 1);//固定值12
        byte[] controlData = new byte[size - 1];
        gifIn.read(controlData);
        byte[] dataSubBlocks = readDataBlock(gifIn);
        extendBlock.setControlData(controlData);
        extendBlock.setDataSubBlocks(dataSubBlocks);
    }

    private static void readAnnotationExtendBlock(GifAnnotationExtendBlock extendBlock, InputStream gifIn) throws IOException {
        byte[] dataSubBlocks;
        do {
            dataSubBlocks = readDataBlock(gifIn);
        } while (dataSubBlocks.length > 1);
    }

    static byte[] readGifParamsBlock(InputStream in) throws IOException {
        byte[] block = new byte[7];
        in.read(block);
        return block;
    }

    static byte[] readHeader(InputStream in) throws IOException {
        byte[] header = new byte[6];
        in.read(header);
        return header;
    }

    public static byte[] readDataBlock(InputStream in) throws IOException {
        byte blockHeader = (byte) in.read();
        byte[] block = new byte[blockHeader + 1];
        block[0] = blockHeader;
        if (in.read(block, 1, blockHeader) < 0) {
            throw new IOException("the data has been read is not enough.");
        }
        return block;
    }
}