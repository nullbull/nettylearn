package com.niu.chat.common.utils;

import java.math.BigInteger;
import java.nio.channels.Channel;

/**
 * @author: justinniu
 * @date: 2018-11-28 10:34
 * @desc:
 **/
public class CRC16 {

    public static String getAllString(String channelId, String type, String data) {
        String temp = channelId + type + data;
        String crcString = getCRC(temp.getBytes());
        String result = Constants.HEAD + temp + crcString + Constants.TAIL;
        return result;
    }


    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNIMIAL = 0x0000a001;
        for (int i = 0; i < bytes.length; i++) {
            CRC  ^= ((int)bytes[i] & 0x000000ff);
            for (int j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNIMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    private float parseHex2Float(String hexStr) {
        BigInteger bigInteger = new BigInteger(hexStr, 16);
        return Float.intBitsToFloat(bigInteger.intValue());
    }

    private String parseFloat2Hex(float data) {
        return Integer.toHexString(Float.floatToIntBits(data));
    }

    public static void main(String[] args) {
        System.out.println(getAllString("123", "zz", "1111111"));
    }
}
