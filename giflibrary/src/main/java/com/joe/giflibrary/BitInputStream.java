package com.joe.giflibrary;

import com.joe.giflibrary.model.GifImageBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/11/19.
 */
public class BitInputStream {

    private List<byte[]> imageEncodeData;
    private int array_p = 0;
    private int byte_p = 0;
    private int left_bit;

    public BitInputStream(GifImageBlock imageBlock) {
        imageEncodeData = imageBlock.getImageEncodeData();
        left_bit = 7;//(0-7)
    }

    public BitInputStream(List<byte[]> imageEncodeData) {
        this.imageEncodeData = imageEncodeData;
        left_bit = 7;
    }

    public BitInputStream(byte[] data) {
        imageEncodeData = new ArrayList<>();
        imageEncodeData.add(data);
    }

    public int readBits(int bits) {
        int result = 0;
        byte nowByte;
        if (array_p >= imageEncodeData.size() || byte_p >= imageEncodeData.get(array_p).length) {
            return -1;
        }
        for (int i = 0; i < bits; i++) {
            if (array_p < imageEncodeData.size() && byte_p < imageEncodeData.get(array_p).length) {
                nowByte = imageEncodeData.get(array_p)[byte_p];
            } else {
                nowByte = 0;
            }
            result = result << 1;
            result = result | ((nowByte & (1 << left_bit)) >> left_bit);
            left_bit--;
            if (left_bit < 0) {
                left_bit = 7;
                byte_p++;
                if (byte_p >= imageEncodeData.get(array_p).length) {
                    byte_p = 0;
                    array_p++;
                }
            }
        }
        return result;
    }
}