package com.niu.chat.common.mqtts;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * @author: justinniu
 * @date: 2018-11-27 15:07
 * @desc:
 **/
public abstract class AbstractMqttHandler extends SimpleChannelInboundHandler<MqttMessage> {

    IMqttHandler mqttHandler;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {

    }
}
