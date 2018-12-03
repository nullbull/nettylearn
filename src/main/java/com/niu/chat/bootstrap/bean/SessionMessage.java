package com.niu.chat.bootstrap.bean;


import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;



@Builder
@Data
public class SessionMessage {

    private byte[]  byteBuf;

    private MqttQoS qoS;

    private  String topic;

    public String getString(){
        return new String(byteBuf);
    }

}
