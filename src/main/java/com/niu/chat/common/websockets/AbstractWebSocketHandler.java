package com.niu.chat.common.websockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: justinniu
 * @date: 2018-11-28 14:45
 * @desc:
 **/
@Slf4j
public abstract class AbstractWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    IWebSocketHandler webSocketHandler;

    public AbstractWebSocketHandler(IWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        if (o instanceof TextWebSocketFrame) {
            textDoMessage(ctx, (TextWebSocketFrame)o);
        } else if (o instanceof WebSocketFrame) {
            webDoMessage(ctx, (WebSocketFrame)o);
        }
    }

    protected abstract void webDoMessage(ChannelHandlerContext ctx, WebSocketFrame msg);

    protected abstract void textDoMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("【DefaultWebSocketHandler：channelInactive】"+ctx.channel().localAddress().toString()+"关闭成功");
        webSocketHandler.close(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof IdleStateEvent) {
            webSocketHandler.doTimeOut(ctx.channel(), (IdleStateEvent)o);
        }
        super.userEventTriggered(ctx, o);
    }
}
