package com.niu.chat.common.utils;

/**
 * @author: justinniu
 * @date: 2018-11-28 14:06
 * @desc:
 **/
public class DataValida {

    public static boolean ValidateHeadAndFeet(String data) {
        boolean state = false;
        if (Constants.HEAD.equals(data.substring(0, 2)) && Constants.TAIL.equals(data.substring(data.length() -1, data.length()))) {
            state = true;
        }
        return state;
    }

    public static boolean ValidateCRCCode(String data, String crcCode) {
        if (crcCode.equals(CRC16.getCRC(data.getBytes()))) {
            return true;
        }
        return false;
    }
}
