package com.niu.chat.bootstrap.handler;

import com.niu.chat.bootstrap.IChannelService;
import com.niu.chat.bootstrap.bean.MqttChannel;
import com.niu.chat.common.exception.NoFindHandlerException;
import com.niu.chat.common.mqtts.AbstractMqttHandler;
import com.niu.chat.common.mqtts.AbstractServerMqttHandlerService;
import com.niu.chat.common.mqtts.IMqttHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auth justinniu
 * @Date 2018/12/5
 * @Desc
 */
@Slf4j
public class DefaultMqttHandler extends AbstractMqttHandler {

    private final IMqttHandler mqttHandler;

    @Autowired
    IChannelService channelService;

    public DefaultMqttHandler(IMqttHandler mqttHandler) {
        super(mqttHandler);
        this.mqttHandler = mqttHandler;
    }

    @Override
    public void doMessage(ChannelHandlerContext handlerContext, MqttMessage mqttMessage) {
        Channel channel = handlerContext.channel();
        AbstractServerMqttHandlerService serverMqttHandlerService;
        if (mqttHandler instanceof  AbstractServerMqttHandlerService) {
            serverMqttHandlerService = (AbstractServerMqttHandlerService) mqttHandler;
        } else {
            throw new NoFindHandlerException("Server Handler 不匹配");
        }
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        if (mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)) {
            if (!serverMqttHandlerService.login(channel, (MqttConnectMessage) mqttMessage)) {
                channel.close();
            }
            return;
        }
        MqttChannel mqttChannel = channelService.getMqttChannel(channelService.getDeviceId(channel));
        if (mqttChannel != null && mqttChannel.isLogin()) {
            switch (mqttFixedHeader.messageType()) {
                case PUBLISH:
                    serverMqttHandlerService.publish(channel, (MqttPublishMessage)mqttMessage);
                    break;
                case SUBSCRIBE:
                    serverMqttHandlerService.subscribe(channel, (MqttSubscribeMessage)mqttMessage);
                    break;
                case PINGREQ:
                    serverMqttHandlerService.pong(channel);
                    break;
                case DISCONNECT:
                    serverMqttHandlerService.disConnect(channel);
                    break;
                case UNSUBSCRIBE:
                    serverMqttHandlerService.unSubscirbe(channel, (MqttUnsubscribeMessage) mqttMessage);
                    break;
                case PUBACK:
                    mqttHandler.pubAck(channel, mqttMessage);
                    break;
                case PUBREC:
                    mqttHandler.pubRec(channel, mqttMessage);
                    break;
                case PUBREL:
                    mqttHandler.pubRel(channel, mqttMessage);
                    break;
                case PUBCOMP:
                    mqttHandler.pubComp(channel, mqttMessage);
                    break;
                default:
                        break;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[DefaultMqttHandler: channelActive] " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception", cause);
        mqttHandler.close(ctx.channel());
    }
}

