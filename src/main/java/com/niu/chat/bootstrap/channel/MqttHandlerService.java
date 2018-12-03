package com.niu.chat.bootstrap.channel;

import com.niu.chat.bootstrap.BaseApi;
import com.niu.chat.common.mqtts.AbstractServerMqttHandlerService;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.channels.Channel;

/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
public class MqttHandlerService extends AbstractServerMqttHandlerService implements BaseApi {
    @Override
    public boolean login(Channel channel, MqttConnectMessage mqttConnectMessage) {
        return false;
    }

    @Override
    public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {

    }

    @Override
    public void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage) {

    }

    @Override
    public void pong(Channel channel) {

    }

    @Override
    public void unSubscirbe(Channel channel, MqttUnsubscribeMessage mqttMessage) {

    }

    @Override
    public void disConnect(Channel channel) {

    }

    @Override
    public void doTimeOut(Channel channel, IdleStateEvent event) {

    }

    @Override
    public void close(io.netty.channel.Channel channel) {

    }

    @Override
    public void pubAck(io.netty.channel.Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubRec(io.netty.channel.Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubRel(io.netty.channel.Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubComp(io.netty.channel.Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void doTimeOut(io.netty.channel.Channel channel, IdleStateEvent event) {

    }
}
