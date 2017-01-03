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
        left_bit = 8;
    }

    public BitInputStream(List<byte[]> imageEncodeData) {
        this.imageEncodeData = imageEncodeData;
        left_bit = 8;
    }

    public BitInputStream(byte[] data) {
        imageEncodeData = new ArrayList<>();
        imageEncodeData.add(data);
        left_bit = 8;
    }

    public short readBits(byte bits) {
        if (bits < 0) {
            return -1;
        }
        return read(bits, bits, (short) 0);
    }

    private short read(byte originalBits, byte needBits, short nowResult) {
        if (array_p >= imageEncodeData.size() || byte_p >= imageEncodeData.get(array_p).length) {
            return -1;
        }
        short temp, result;
        short nowByte = (short) (imageEncodeData.get(array_p)[byte_p] & 0xff);
        if (needBits <= left_bit) {
            //当前byte剩余bit足够
            temp = (short) ((nowByte & (((1 << needBits) - 1) << (8 - left_bit))) >> (8 - left_bit));
            result = (short) ((temp << (originalBits - needBits)) | nowResult);//前后拼接
            left_bit -= needBits;
            if (left_bit == 0) {
                byte_p++;
                if (byte_p >= imageEncodeData.get(array_p).length) {
                    byte_p = 0;
                    array_p++;
                }
                left_bit = 8;
            }
        } else {
            //当前byte剩余bit不足
            byte usedBits = (byte) (8 - left_bit);
            //先取出剩余bit
            temp = (short) ((nowByte & (~((1 << usedBits) - 1))) >> usedBits);
            temp = (short) ((temp << (originalBits - needBits)) | nowResult);
            needBits -= left_bit;

            //读取下一个byte
            byte_p++;
            if (byte_p >= imageEncodeData.get(array_p).length) {
                byte_p = 0;
                array_p++;
            }
            left_bit = 8;
            if (array_p >= imageEncodeData.size() || byte_p >= imageEncodeData.get(array_p).length) {
                //如果下一个byte已经溢出，则直接返回当前值
                return (short) temp;
            }
            result = read(originalBits, needBits, temp);
        }
        return result;
    }
}