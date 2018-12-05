package com.niu.chat.common.mqtts;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author: justinniu
 * @date: 2018-11-27 15:07
 * @desc:
 **/
@Slf4j
public abstract class AbstractMqttHandler extends SimpleChannelInboundHandler<MqttMessage> {

    IMqttHandler mqttHandler;

    public AbstractMqttHandler(IMqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        Optional.ofNullable(mqttFixedHeader)
                .ifPresent(mqttFixedHeader1 -> doMessage(channelHandlerContext, mqttMessage));
    }

    public abstract void  doMessage(ChannelHandlerContext handlerContext, MqttMessage mqttMessage);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[DefaultMqttHandleer: channelInactive " + ctx.channel().localAddress().toString());
        mqttHandler.close(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            mqttHandler.doTimeOut(ctx.channel(), (IdleStateEvent)evt);
        }
        super.userEventTriggered(ctx, evt);
    }
}
