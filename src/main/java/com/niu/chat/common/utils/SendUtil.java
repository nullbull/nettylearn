package com.niu.chat.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;

/**
 * @author: justinniu
 * @date: 2018-11-27 18:26
 * @desc:
 **/
public class SendUtil {

    public boolean send(Integer it, Channel channel, String channelID, String type) {
        try {
            if (it != null && channel != null) {
                String items = IntegerToString(it);
                String result = CRC16.getAllString(channelID, type, items);
                ByteBuf msg = Unpooled.unreleasableBuffer(
                        Unpooled.copiedBuffer(result, Charset.forName("UTF-8")));
                channel.writeAndFlush(msg.duplicate());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public void sendAll(String items, Channel channel, String channelId, String type) {
        try {
            if (items != null && channel != null) {
                String result = CRC16.getAllString(channelId, type, items);
                ByteBuf msg = Unpooled.unreleasableBuffer(
                        Unpooled.copiedBuffer(result, Charset.forName("UTF-8")));
                channel.writeAndFlush(msg.duplicate());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String IntegerToString(Integer it) {
        char[] locks_char = Constants.LOCKS.toCharArray();
        for (int i = 0; i < locks_char.length; i++) {
            if (i == it) {
                locks_char[i] = Constants.OPEN;
            }
        }
        return String.valueOf(locks_char);
    }
    public static String sendTest(String msg,Channel channel) {
        try {
            channel.writeAndFlush(new TextWebSocketFrame( "[系统API]" + msg));
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }
}
