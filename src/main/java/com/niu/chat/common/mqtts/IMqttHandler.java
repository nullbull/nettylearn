package com.niu.chat.common.mqtts;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * @author: justinniu
 * @date: 2018-11-27 14:55
 * @desc:
 **/
public interface IMqttHandler {

    void close(Channel channel);

    void pubAck(Channel channel, MqttMessage mqttMessage);

    void pubRec(Channel channel, MqttMessage mqttMessage);

    void pubRel(Channel channel, MqttMessage mqttMessage);

    void pubComp(Channel channel, MqttMessage mqttMessage);

    void doTimeOut(Channel channel, IdleStateEvent event);
}
