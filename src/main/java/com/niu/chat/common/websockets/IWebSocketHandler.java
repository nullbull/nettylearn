package com.niu.chat.common.websockets;

import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Map;

public interface IWebSocketHandler {

    void close(Channel channel);

    void sendMeText(Channel channel, Map<String, String> maps);

    void sentToText(Channel channel, Map<String, String> maps);

    void doTimeOut(Channel channel, IdleStateEvent event);
}
