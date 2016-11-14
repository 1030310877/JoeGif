package com.joe.giflibrary;

import com.joe.giflibrary.model.GifImageBlock;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/11/12.
 */
public class LZWDecoder {

    private void decode(GifImageBlock block) {
        ArrayList<byte[]> imageEncodeData = block.getImageEncodeData();
        int codeSize = toInt(block.getLZWSize());
        int clearCode = 1 << codeSize;
        int finishCode = clearCode + 1;
        int currentIndex = clearCode + 2;//当前编码表对应最后可插入的项

        int oldCode = -1, code = 0;
        for (int i = 0; i < imageEncodeData.size(); i++) {
            byte[] data = imageEncodeData.get(i);
            for (int j = 1; j < data.length; j++) {
                code = toInt(data[j]);
                if (code == finishCode) {
                    //读取到结束标志
                    return;
                }
                if (code == clearCode) {
                    //重新生成编码表
                    codeSize++;
                    currentIndex = clearCode + 2;
                }
                if (code < currentIndex) {
                    //说明找到了对应值
                } else {
                    //not found
                }
            }
        }
    }

    private int toInt(byte b) {
        return (b & 0xff);
    }
}