package com.joe.giflibrary;

import com.joe.giflibrary.model.GifImageBlock;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Description
 * Created by chenqiao on 2016/11/12.
 */
class LZWDecoder {

    static ArrayList<Word> result = new ArrayList<>();
    static ArrayList<Integer> decodeData = new ArrayList<>();

    static ArrayList<Integer> decode(GifImageBlock block) {
        ArrayList<byte[]> imageEncodeData = block.getImageEncodeData();
        byte lzwCodeSize = block.getLZWSize();
        short rootTableSize = (short) (1 << lzwCodeSize);
        byte dataBits = (byte) (lzwCodeSize + 1);
        short CC = (short) (1 << lzwCodeSize);
        short EOI = (short) (CC + 1);

        BitInputStream bitInputStream = new BitInputStream(imageEncodeData);
        Dictionary dictionary = new Dictionary(rootTableSize);
        int oldCode = -1, code;
        if (result == null) {
            result = new ArrayList<>();
        } else {
            result.clear();
        }
        while ((code = bitInputStream.readBits(dataBits)) != -1) {
            if (code == EOI) {
                break;
            }
            if (code == CC) {
                dataBits = (byte) (lzwCodeSize + 1);
                dictionary = new Dictionary(rootTableSize);
                oldCode = -1;
                continue;
            }
            Word w = dictionary.get(code);
            if (w != null) {
                //find
                result.add(w);
                if (oldCode != -1) {
                    Word newWord = new Word();
                    newWord.codes.addAll(dictionary.get(oldCode).codes);
                    newWord.codes.add(w.codes.get(0));
                    dictionary.addWord(newWord);
                }
            } else {
                //没有找到，则添加到字典中
                Word oldWord = dictionary.get(oldCode);
                Word newWord = new Word();
                newWord.codes.addAll(oldWord.codes);
                newWord.codes.add(oldWord.codes.get(0));
                dictionary.addWord(newWord);
                result.add(newWord);
            }
            oldCode = code;
            if (dictionary.size() >= (1 << dataBits)) {
                //超出当前表长度
                dataBits++;
                if (dataBits > 12) {
                    dataBits = 12;
                }
            }
        }

        if (decodeData == null) {
            decodeData = new ArrayList<>();
        } else {
            decodeData.clear();
        }
        for (Word word : result) {
            for (Short aShort : word.codes) {
                int colorInt;
                if (block.getTransparentColorIndex() == aShort) {
                    colorInt = 0;
                } else {
                    colorInt = block.getColor_table()[aShort];
                }
                decodeData.add(colorInt);
            }
        }
        return decodeData;
    }

    private static class Dictionary {
        LinkedList<Word> words;

        Dictionary(int initSize) {
            words = new LinkedList<>();
            for (int i = 0; i < initSize + 2; i++) {
                Word word = new Word();
                word.codes.add((short) i);
                words.add(word);
            }
        }

        Word get(int index) {
            if (words.size() <= index) {
                return null;
            } else {
                return words.get(index);
            }
        }

        void addWord(Word word) {
            words.add(word);
        }

        public int size() {
            if (words == null) {
                return 0;
            } else {
                return words.size();
            }
        }
    }

    private static class Word {
        ArrayList<Short> codes;

        Word() {
            codes = new ArrayList<>();
        }
    }
}