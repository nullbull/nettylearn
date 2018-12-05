package com.niu.chat.common.mqtts;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * @author: justinniu
 * @date: 2018-11-27 15:08
 * @desc:
 **/
public abstract class AbstractServerMqttHandlerService implements IMqttHandler {

    public abstract boolean login(Channel channel, MqttConnectMessage mqttConnectMessage);

    public abstract void publish(Channel channel, MqttPublishMessage mqttPublishMessage);

    public abstract void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage);

    public abstract void pong(Channel channel);

    public abstract void unSubscirbe(Channel channel, MqttUnsubscribeMessage mqttMessage);

    public abstract void disConnect(Channel channel);

    public abstract void doTimeOut(Channel channel, IdleStateEvent event);


}
