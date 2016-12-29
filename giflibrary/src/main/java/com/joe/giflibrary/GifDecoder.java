package com.joe.giflibrary;

import android.graphics.Color;
import android.util.Log;

import com.joe.giflibrary.extend.GifAnnotationExtendBlock;
import com.joe.giflibrary.extend.GifAppExtendBlock;
import com.joe.giflibrary.extend.GifExtendBlock;
import com.joe.giflibrary.extend.GifGraphicControlExtendBlock;
import com.joe.giflibrary.extend.GifTextExtendBlock;
import com.joe.giflibrary.model.GifDrawable;
import com.joe.giflibrary.model.GifImageBlock;
import com.joe.giflibrary.model.GifImagePixelModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/11/11.
 */
class GifDecoder {
    static int transparentColorIndex = -1;

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
            int width = ((screenDescriptor[1] & 0xff) << 8 | (screenDescriptor[0] & 0xff));
            int height = ((screenDescriptor[3] & 0xff) << 8 | (screenDescriptor[2] & 0xff));
            Log.d("GifDecoder", "width=" + width + "  height=" + height);
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
        int index = 1 << (1 + (drawable.getPixel() & 0xff));
        int r, g, b;
        int[] colorTable = new int[index];
        for (int i = 0; i < index; i++) {
            r = ((byte) gifIn.read()) & 0xff;
            g = ((byte) gifIn.read()) & 0xff;
            b = ((byte) gifIn.read()) & 0xff;
            colorTable[i] = Color.rgb(r, g, b);
        }
        drawable.setColor_table(colorTable);
    }

    static void readDataStream(GifDrawable drawable, InputStream gifIn) throws IOException {
        byte head;
        while ((head = (byte) gifIn.read()) >= 0) {
            switch (head) {
                case GifExtendBlock.FLAG_EXTEND_BLOCK:
                    addExtendBlocks(drawable, gifIn);
                    break;
                case GifImageBlock.FLAG_IMAGE_BLOCK:
                    Log.d("GifDecoder", "readDataStream: Image Block");
                    GifImageBlock block = addImageBlock(drawable, gifIn);
                    decodeImageBlock(drawable, block);
                    transparentColorIndex = -1;
                    break;
                case GifDrawable.FLAG_FILE_END:
                    Log.d("GifDecoder", "readDataStream: File End");
                    return;
                default:
                    Log.d("GifDecoder", "readDataStream: unknown");
                    break;
            }
        }
    }

    private static void decodeImageBlock(GifDrawable drawable, GifImageBlock imageBlock) {
        ArrayList<Integer> decodedData = LZWDecoder.decode(imageBlock);
        int width = drawable.getLogicalWidth();
        int height = drawable.getLogicalHeight();
        int[] imageOriginalData = new int[width * height];
        if (imageBlock.isInterlaceFlag()) {
            Log.d("GifDecoder", "decodeImageBlock: isInterlace");
            int line1 = height % 8 == 0 ? height / 8 : (int) Math.ceil(height / 8f);
            int temp = height - 4;
            int line2 = temp <= 0 ? 0 : (temp % 8 == 0 ? temp / 8 : (int) Math.ceil(temp / 8f));
            temp = height - 2;
            int line3 = temp <= 0 ? 0 : (temp % 4 == 0 ? temp / 4 : (int) Math.ceil(temp / 4f));
            temp = height - 1;
            int line4 = temp <= 0 ? 0 : (temp % 2 == 0 ? temp / 2 : (int) Math.ceil(temp / 2f));

            int pos = 0;
            for (int i = 0; i < line1; i++) {
                for (int j = 0; j < width; j++) {
                    imageOriginalData[pos++] = decodedData.get(i * 8 * width + j);
                }
            }
            for (int i = 0; i < line2; i++) {
                for (int j = 0; j < width; j++) {
                    imageOriginalData[pos++] = decodedData.get(i * 8 * width + 4 * width + j);
                }
            }
            for (int i = 0; i < line3; i++) {
                for (int j = 0; j < width; j++) {
                    imageOriginalData[pos++] = decodedData.get(i * 4 * width + 2 * width + j);
                }
            }
            for (int i = 0; i < line4; i++) {
                for (int j = 0; j < width; j++) {
                    imageOriginalData[pos++] = decodedData.get(i * 2 * width + width + j);
                }
            }
        } else {
            Log.d("GifDecoder", "decodeImageBlock: not isInterlace");
            for (int i = 0; i < decodedData.size(); i++) {
                imageOriginalData[i] = decodedData.get(i);
            }
        }
        decodedData.clear();
        drawable.addImageDecodeData(imageBlock, imageOriginalData);
    }

    private static GifImageBlock addImageBlock(GifDrawable drawable, InputStream gifIn) throws IOException {
        GifImageBlock block = new GifImageBlock();
        byte[] imageDescriptor = new byte[10];
        imageDescriptor[0] = GifImageBlock.FLAG_IMAGE_BLOCK;
        gifIn.read(imageDescriptor, 1, 9);
        block.setData(imageDescriptor);
        if (block.isLocalColorTableFlag()) {
            int index = 1 << (1 + (block.getLocalPixel() & 0xff));
            int r, g, b;
            int[] colorTable = new int[index];
            for (int i = 0; i < index; i++) {
                r = ((byte) gifIn.read()) & 0xff;
                g = ((byte) gifIn.read()) & 0xff;
                b = ((byte) gifIn.read()) & 0xff;
                colorTable[i] = Color.rgb(r, g, b);
            }
            block.setColor_table(colorTable);
        } else {
            //如果不存在局部颜色表，则使用全局颜色表
            block.setColor_table(drawable.getColor_table());
        }
        readImageData(block, gifIn);
        return block;
    }

    private static void readImageData(GifImageBlock block, InputStream gifIn) throws IOException {
        int lzwSize = gifIn.read();
        block.setLZWSize((byte) lzwSize);
        Log.d("GifDecoder", "readImageData LZW Size: " + lzwSize);
        byte[] imageDataBlock;
        do {
            imageDataBlock = readDataBlock(gifIn);
            if (imageDataBlock.length > 0) {
                block.addImageEncodeData(imageDataBlock);
            }
        } while (imageDataBlock.length > 0);
    }

    private static void addExtendBlocks(GifDrawable drawable, InputStream gifIn) throws IOException {
        byte type = (byte) gifIn.read();
        GifExtendBlock extendBlock = null;
        switch (type) {
            case GifExtendBlock.LABEL_ANNOTATION_EXTEND_BLOCK:
                Log.d("GifDecoder", "readDataStream: Annotation Extend Block");
                extendBlock = new GifAnnotationExtendBlock();
                readAnnotationExtendBlock((GifAnnotationExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_APP_EXTEND_BLOCK:
                Log.d("GifDecoder", "readDataStream: Application Extend Block");
                extendBlock = new GifAppExtendBlock();
                readAppExtendBlock((GifAppExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_GRAPHIC_CONTROL_EXTEND_BLOCK:
                Log.d("GifDecoder", "readDataStream: Graphic Control Extend Block");
                extendBlock = new GifGraphicControlExtendBlock();
                readGraphicControlExtendBlock((GifGraphicControlExtendBlock) extendBlock, gifIn);
                break;
            case GifExtendBlock.LABEL_TEXT_EXTEND_BLOCK:
                Log.d("GifDecoder", "readDataStream: Text Extend Block");
                extendBlock = new GifTextExtendBlock();
                readTextExtendBlock((GifTextExtendBlock) extendBlock, gifIn);
                transparentColorIndex = -1;
                break;
        }
        if (extendBlock != null) {
            drawable.addExtendBlock(extendBlock);
            if (extendBlock instanceof GifGraphicControlExtendBlock) {
                short time = 100;
                if (((GifGraphicControlExtendBlock) extendBlock).getDelayTime() != 0) {
                    time = ((GifGraphicControlExtendBlock) extendBlock).getDelayTime();
                }
                ArrayList<GifImagePixelModel> list = drawable.getImageDecodeData();
                if (list != null && list.size() > 0) {
                    list.get(list.size() - 1).setDelayTime(time);
                }
                if (((GifGraphicControlExtendBlock) extendBlock).isTransparentColorFlag()) {
                    transparentColorIndex = ((GifGraphicControlExtendBlock) extendBlock).getTransparentColorIndex();
                } else {
                    transparentColorIndex = -1;
                }
            }
        }
    }

    private static void readTextExtendBlock(GifTextExtendBlock extendBlock, InputStream gifIn) throws IOException {
        int size = gifIn.read();
        extendBlock.setSize(size);//固定值12
        byte[] controlData = new byte[size];
        gifIn.read(controlData);
        extendBlock.setControlData(controlData);
        byte[] dataSubBlock;
        do {
            dataSubBlock = readDataBlock(gifIn);
            if (dataSubBlock.length > 0) {
                extendBlock.addDataSubBlock(dataSubBlock);
            }
        } while (dataSubBlock.length > 0);
    }

    private static void readGraphicControlExtendBlock(GifGraphicControlExtendBlock extendBlock, InputStream gifIn) throws IOException {
        int size = gifIn.read();
        extendBlock.setSize(size);//固定值4
        byte[] controlData = new byte[size];
        gifIn.read(controlData);
        extendBlock.setControlData(controlData);
        byte[] dataSubBlock;
        do {
            dataSubBlock = readDataBlock(gifIn);
            if (dataSubBlock.length > 0) {
                extendBlock.addDataSubBlock(dataSubBlock);
            }
        } while (dataSubBlock.length > 0);
    }

    private static void readAppExtendBlock(GifAppExtendBlock extendBlock, InputStream gifIn) throws IOException {
        int size = gifIn.read();
        extendBlock.setSize(size);//固定值11
        byte[] controlData = new byte[size];
        gifIn.read(controlData);
        extendBlock.setControlData(controlData);
        byte[] dataSubBlock;
        do {
            dataSubBlock = readDataBlock(gifIn);
            if (dataSubBlock.length > 0) {
                extendBlock.addDataSubBlock(dataSubBlock);
            }
        } while (dataSubBlock.length > 0);
    }

    private static void readAnnotationExtendBlock(GifAnnotationExtendBlock extendBlock, InputStream gifIn) throws IOException {
        byte[] dataSubBlock;
        do {
            dataSubBlock = readDataBlock(gifIn);
            if (dataSubBlock.length > 0) {
                extendBlock.addDataSubBlock(dataSubBlock);
            }
        } while (dataSubBlock.length > 0);
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

    private static byte[] readDataBlock(InputStream in) throws IOException {
        int blockHeader = in.read();
        byte[] block = new byte[blockHeader];
        if (blockHeader > 0 && in.read(block, 0, blockHeader) < 0) {
            throw new IOException("the data has been read is not enough.");
        }
        return block;
    }
}