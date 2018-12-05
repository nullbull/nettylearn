package com.niu.chat.bootstrap.channel;

import com.niu.chat.bootstrap.BaseApi;
import com.niu.chat.bootstrap.IBaseAuthService;
import com.niu.chat.bootstrap.bean.SendMqttMessage;
import com.niu.chat.common.enums.ConfirmStatus;
import com.niu.chat.common.mqtts.AbstractServerMqttHandlerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
@Slf4j
public class MqttHandlerService extends AbstractServerMqttHandlerService implements BaseApi {

    @Autowired
    AbstractChannelService mqttChanelService;

    private final IBaseAuthService baseAuthService;

    public MqttHandlerService(IBaseAuthService baseAuthService) {
        this.baseAuthService = baseAuthService;
    }

    @Override
    public boolean login(Channel channel, MqttConnectMessage mqttConnectMessage) {
        MqttConnectPayload payload = mqttConnectMessage.payload();
        String deviceId = payload.clientIdentifier();
        if (StringUtils.isBlank(deviceId)) {
            connectBack(channel, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
            return false;
        }
        if (mqttConnectMessage.variableHeader().hasPassword() && mqttConnectMessage.variableHeader().hasUserName()
                     && !baseAuthService.authorized(payload.userName(), payload.password())) {
           connectBack(channel, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
           return false;
        }
        return Optional.ofNullable(mqttChanelService.getMqttChannel(deviceId))
                .map(mqttChannel -> {
                    switch (mqttChannel.getSessionStatus()) {
                        case OPEN:
                            return false;
                    }
                    mqttChanelService.loginSuccess(channel, deviceId, mqttConnectMessage);
                    return true;
                }).orElseGet(() -> {
                    mqttChanelService.loginSuccess(channel, deviceId, mqttConnectMessage);
                    return true;
                });
    }

    private void connectBack(Channel channel, MqttConnectReturnCode returnCode) {
        MqttConnAckVariableHeader header = new MqttConnAckVariableHeader(returnCode, true);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeader, header);
       channel.writeAndFlush(connAck);
    }

    @Override
    public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {
        mqttChanelService.publishSuccess(channel, mqttPublishMessage);
    }

    @Override
    public void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage) {
        Set<String> topics = mqttSubscribeMessage.payload().topicSubscriptions().stream().map(mqttTopicSubscription ->
            mqttTopicSubscription.topicName()).collect(Collectors.toSet());
        mqttChanelService.suscribeSuccess(mqttChanelService.getDeviceId(channel), topics);
        subBack(channel, mqttSubscribeMessage, topics.size());
    }
    private void subBack(Channel channel, MqttSubscribeMessage mqttSubscribeMessage, int num) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId());
        List<Integer> grantedQoSLevels = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            grantedQoSLevels.add(mqttSubscribeMessage.payload().topicSubscriptions().get(i).qualityOfService().value());
        }
        MqttSubAckPayload payload = new MqttSubAckPayload(grantedQoSLevels);
        MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
        channel.writeAndFlush(mqttSubAckMessage);
    }

    @Override
    public void pong(Channel channel) {
        if (channel.isOpen() && channel.isActive() && channel.isWritable()) {
            log.info("收到来自:[{}]心跳", channel.remoteAddress().toString());
            MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
            channel.writeAndFlush(new MqttMessage(fixedHeader));
        }
    }

    @Override
    public void unSubscirbe(Channel channel, MqttUnsubscribeMessage mqttMessage) {
        List<String> topics1 = mqttMessage.payload().topics();
        mqttChanelService.unsubscribe(mqttChanelService.getDeviceId(channel), topics1);
        unSubBack(channel, mqttMessage.variableHeader().messageId());
    }
    private void unSubBack(Channel channel, int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttUnsubAckMessage mqttUnsubAckMessage = new MqttUnsubAckMessage(mqttFixedHeader, variableHeader);
        channel.writeAndFlush(mqttUnsubAckMessage);
    }

    @Override
    public void disConnect(Channel channel) {
        mqttChanelService.closeSuccess(mqttChanelService.getDeviceId(channel), true);
    }

    @Override
    public void doTimeOut(Channel channel, IdleStateEvent event) {
        log.info("[PingPongSercice: doTimeOut 心跳超时] " + channel.remoteAddress().toString());
        switch (event.state()) {
            case READER_IDLE:
                close(channel);
            case WRITER_IDLE:
                close(channel);
            case ALL_IDLE:
                close(channel);
        }
    }

    @Override
    public void close(io.netty.channel.Channel channel) {
        mqttChanelService.closeSuccess(mqttChanelService.getDeviceId(channel), false);
        channel.close();
    }

    @Override
    public void pubAck(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader)mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        mqttChanelService.getMqttChannel(mqttChanelService.getDeviceId(channel)).getSendMqttMessage(messageId).setConfirmStatus(ConfirmStatus.COMPLETE);
    }

    @Override
    public void pubRec(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader)mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        mqttChanelService.getMqttChannel(mqttChanelService.getDeviceId(channel)).getSendMqttMessage(messageId).setConfirmStatus(ConfirmStatus.COMPLETE);
        mqttChanelService.doPubrec(channel, messageId);
    }

    @Override
    public void pubRel(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader)mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        mqttChanelService.getMqttChannel(mqttChanelService.getDeviceId(channel)).getSendMqttMessage(messageId).setConfirmStatus(ConfirmStatus.COMPLETE);
        mqttChanelService.doPubrel(channel, messageId);
    }

    @Override
    public void pubComp(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = mqttMessageIdVariableHeader.messageId();
        SendMqttMessage sendMqttMessage = mqttChanelService.getMqttChannel(mqttChanelService.getDeviceId(channel)).getSendMqttMessage(messageId);
        sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
    }


}
