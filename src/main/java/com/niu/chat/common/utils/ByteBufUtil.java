package com.niu.chat.common.utils;

import io.netty.buffer.ByteBuf;

/**
 * @author: justinniu
 * @date: 2018-11-27 17:54
 * @desc:
 **/
public class ByteBufUtil {
    public static byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
