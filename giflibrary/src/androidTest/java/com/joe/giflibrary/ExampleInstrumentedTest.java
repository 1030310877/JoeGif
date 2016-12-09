package com.joe.giflibrary;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        BitInputStream inputStream = new BitInputStream(new byte[]{
                0b0000_1111,
                (byte) 0b1111_0000,
                (byte) 0b1010_1001});
        int result;
        while ((result = inputStream.readBits(7)) != -1) {
            Log.d("ExampleInstrumentedTest", "useAppContext: " + result);
        }
        assertEquals("com.joe.giflibrary.test", appContext.getPackageName());
    }
}
