package com.artifexiumgames.journal;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /**
     * Code from {@link android.graphics.drawable.ColorDrawable#setAlpha(int)}
     * Needed to understand how the method works.
     * Currently if you setHighlightAlpha(127), it sets it to half transparent, then again,
     * then again, and so on, until it's gone
     *
     * REASON:
     * Color is -1 let's say, (white)
     * then color of -1 with highlightAlpha 128 = -2130706433
     * then color of -2130706433 with highlightAlpha 128 = 1090519039
     *
     * SOLUTION:
     * Check if the color already has an highlightAlpha of 128, if so, then leave it.
     *
     * Duh.
     * @throws Exception
     */
    @Test
    public void alpha() throws Exception {
        int BaseColor = -1;
        int alpha = 127;
        if (alpha < 128){
            alpha += 1;
        }
        final int baseAlpha = BaseColor >>> 24;
        System.out.println("Base Color " + BaseColor);
        System.out.println("Set Alpha to " +alpha);
        System.out.println("Base Alpha " + baseAlpha);
        alpha += alpha >> 7;   // make it 0..256
        final int useAlpha = baseAlpha * alpha >> 8;
        final int useColor = (BaseColor << 8 >>> 8) | (useAlpha << 24);
        System.out.println("\nUse Alpha " + useAlpha);
        System.out.println("Color " + useColor);

    }
}