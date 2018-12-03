package com.niu.chat.common.utils;

/**
 * @author: justinniu
 * @date: 2018-11-27 18:24
 * @desc:
 **/
public class StringUtil {
    public static String getName(String s) {
        int nameIndex = s.indexOf("-");
        return s.substring(0, nameIndex);
    }

    public static String getMsg(String s) {
        return s.substring(s.indexOf("-" + 1, s.length()));
    }
}
