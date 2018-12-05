package com.niu.chat.bootstrap.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * @Auth justinniu
 * @Date 2018/12/5
 * @Desc
 */
public class ByteBufToWebSocketFrameEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf == null) {
            return;
        }
        BinaryWebSocketFrame result = new BinaryWebSocketFrame();
        result.content().writeBytes(byteBuf);
        list.add(result);
    }
}
