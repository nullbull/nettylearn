package com.niu.chat.common.utils;

import java.util.Random;

/**
 * @author: justinniu
 * @date: 2018-11-28 11:48
 * @desc:
 **/
public class RandomNameUtil {
    private static Random random = new Random(37);
    /**
     * 汉字的编码范围在 0x9fa5-0x4e00之间
     */
    private final static int delta = 0x9fa5 - 0x4e00 + 1;
    public static char getName() {
        return (char) (0x4e00 + random.nextInt(delta));
    }
}
