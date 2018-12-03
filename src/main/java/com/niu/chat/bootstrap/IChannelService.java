package com.niu.chat.bootstrap;

import com.niu.chat.bootstrap.bean.MqttChannel;
import com.niu.chat.bootstrap.bean.WillMeaasge;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.List;
import java.util.Set;

/**
 * @author: justinniu
 * @date: 2018-11-30 10:04
 * @desc:
 **/
public interface IChannelService {

    MqttChannel getMqttChannel(String deviceId);

    boolean connectSuccess(String s, MqttChannel build);

    void suscribeSuccess(String deviceId, Set<String> topics);

    void loginSuccess(Channel channel, String deviceId, MqttConnectMessage mqttConnectMessage);

    void publishSuccess(Channel channel, MqttPublishMessage mqttPublishMessage);

    void closeSuccess(String deviceId, boolean isDisconnect);

    void sendWillMsg(WillMeaasge willMeaasge);

    String  getDeviceId(Channel channel);

    void unsubscribe(String deviceId, List<String> topics1);

    void  doPubrel(Channel channel, int mqttMessage);

    void  doPubrec(Channel channel, int mqttMessage);
}
