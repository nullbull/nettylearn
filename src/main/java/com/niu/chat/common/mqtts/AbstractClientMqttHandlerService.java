package com.niu.chat.common.mqtts;


import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.timeout.IdleStateEvent;
/**
 * @author: justinniu
 * @date: 2018-11-27 14:47
 * @desc:
 **/
public abstract class AbstractClientMqttHandlerService implements IMqttHandler{
    @Override
    public void doTimeOut(Channel channel, IdleStateEvent event) {
        heart(channel, event);
    }

    public abstract void heart(Channel channel, IdleStateEvent event);


    public abstract void subBack(Channel channel, MqttSubAckMessage mqttSubAckMessage);

    public abstract void pubBackMessage(Channel channel, int i);

    public abstract void unSubBack(Channel channel, MqttMessage mqttMessage);

}
