package com.joe.giflibrary;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        ArrayList<byte[]> data = new ArrayList<>();
        byte[] t = new byte[]{(byte) 0b0000_1100, 0b0011_1111};
        byte[] t2 = new byte[]{(byte) 0b1001_0110, (byte) 0b1111_0001};
        data.add(t);
        data.add(t2);
        BitInputStream bitInputStream = new BitInputStream(data);
        int result;
        while ((result = bitInputStream.readBits(6)) != -1) {
            System.out.println(result);
        }
        assertEquals(4, 2 + 2);
    }
}