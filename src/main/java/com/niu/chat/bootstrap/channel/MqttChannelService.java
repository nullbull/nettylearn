package com.niu.chat.bootstrap.channel;

import com.niu.chat.bootstrap.bean.*;
import com.niu.chat.bootstrap.scan.ScanRunnable;
import com.niu.chat.common.enums.ConfirmStatus;
import com.niu.chat.common.enums.SessionStatus;
import com.niu.chat.common.enums.SubStatus;
import com.niu.chat.common.exception.ConnectionException;
import com.niu.chat.common.utils.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
@Slf4j
@Component
public class MqttChannelService extends AbstractChannelService {
    @Autowired
    private ClientSessionService clientSessionService;

    @Autowired
    private WillService willService;

    private final ScanRunnable scanRunnable;

    public MqttChannelService(ScanRunnable scanRunnable) {
        super(scanRunnable);
        this.scanRunnable = scanRunnable;
    }

    private void replyLogin(Channel channel, MqttConnectMessage mqttConnectMessage) {
        MqttFixedHeader mqttFixedHeader = mqttConnectMessage.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeader = mqttConnectMessage.variableHeader();
        final MqttConnectPayload payload = mqttConnectMessage.payload();
        String deviceId = getDeviceId(channel);
        MqttChannel build = MqttChannel.builder().channel(channel).cleanSession(mqttConnectVariableHeader.isCleanSession())
                .deviceId(payload.clientIdentifier())
                .sessionStatus(SessionStatus.OPEN)
                .isWill(mqttConnectVariableHeader.isWillFlag())
                .topic(new CopyOnWriteArraySet<>())
                .message(new ConcurrentHashMap<>())
                .receive(new CopyOnWriteArraySet<>())
                .build();

        if (connectSuccess(deviceId, build)) {
            if (mqttConnectVariableHeader.isWillFlag()) {
                boolean b = doIf(mqttConnectVariableHeader, mqttConnectVariableHeader1 -> (payload.willMessage() != null),
                        mqttConnectVariableHeader1 -> (payload.willTopic()) != null);
                if (!b) {
                    throw new ConnectionException("will message and will topic is not null");
                }
                final WillMessage buildWill = WillMessage.builder()
                        .qos(mqttConnectVariableHeader.willQos())
                        .willMessage(deviceId)
                        .willTopic(payload.willTopic())
                        .isRetain(mqttConnectVariableHeader.isWillRetain())
                        .build();
                willService.save(payload.clientIdentifier(), buildWill);
            } else {
                willService.del(payload.clientIdentifier());
                boolean b = doIf(mqttConnectVariableHeader, mqttConnectVariableHeader1 -> (!mqttConnectVariableHeader1.isWillRetain()),
                        mqttConnectVariableHeader1 -> (mqttConnectVariableHeader1.willQos() == 0));
                if (!b) {
                    throw new ConnectionException("will retain should be null and will QOS equals 0");
                }

            }
                doIfElse(mqttConnectVariableHeader,
                        mqttConnectVariableHeader1 -> (mqttConnectVariableHeader1.isCleanSession()),
                        mqttConnectVariableHeader1 -> {
                        MqttConnectReturnCode code = MqttConnectReturnCode.CONNECTION_ACCEPTED;
                        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(code, false);
                        MqttFixedHeader mqttFixedHeader1 = new MqttFixedHeader(MqttMessageType.CONNACK, mqttFixedHeader.isDup(), MqttQoS.AT_MOST_ONCE,
                                mqttFixedHeader.isRetain(), 0x02);
                        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(mqttFixedHeader1, mqttConnAckVariableHeader);
                        channel.writeAndFlush(connAckMessage); },
                        mqttConnectVariableHeader1 -> {
                        MqttConnectReturnCode connectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;
                        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, true);
                        MqttFixedHeader mqttFixedHeader1 = new MqttFixedHeader(MqttMessageType.CONNACK, mqttFixedHeader.isDup(), MqttQoS.AT_MOST_ONCE,
                                mqttFixedHeader.isRetain(), 0x02);
                        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeader1, mqttConnAckVariableHeader);
                        channel.writeAndFlush(connAck);
                    });
            ConcurrentLinkedQueue<SessionMessage> sessionMessages = clientSessionService.getByteBuf(payload.clientIdentifier());
            doIfElse(sessionMessages, sessionMessages1 -> sessionMessages1 != null && !sessionMessages1.isEmpty(), byteBufs -> {
                SessionMessage sessionMessage;
                while ((sessionMessage = byteBufs.poll()) != null) {
                    switch (sessionMessage.getQoS()) {
                        case EXACTLY_ONCE:
                            sendQosConfirmMsg(MqttQoS.EXACTLY_ONCE, getMqttChannel(deviceId), sessionMessage.getTopic(), sessionMessage.getByteBuf());
                            break;
                        case AT_MOST_ONCE:
                            sendQos0Msg(channel, sessionMessage.getTopic(), sessionMessage.getByteBuf());
                            break;
                        case AT_LEAST_ONCE:
                            sendQosConfirmMsg(MqttQoS.AT_LEAST_ONCE, getMqttChannel(deviceId), sessionMessage.getTopic(), sessionMessage.getByteBuf());
                    }
                }
            });
        }
    }

    @Override
    public MqttChannel getMqttChannel(String deviceId) {
        return Optional.ofNullable(deviceId).map(s -> mqttChannelMap.get(s))
                .orElse(null);
    }

    @Override
    public boolean connectSuccess(String s, MqttChannel build) {
        return Optional.ofNullable(mqttChannelMap.get(s))
                .map(mqttChannel -> {
                    switch (mqttChannel.getSessionStatus()) {
                        case OPEN:
                            return false;
                        case CLOSE:
                            switch (mqttChannel.getSubStatus()) {
                                case YES:
                                    deleteSubTopic(mqttChannel).stream()
                                            .forEach(ss -> cacheMap.putData(getTopic(ss), build));
                                    break;
                            }
                            break;
                    }
                    mqttChannelMap.put(s, build);
                    return true;
                }).orElseGet(() -> {mqttChannelMap.put(s, build);
                    return true;
                });
    }

    public Set<String> deleteSubTopic(MqttChannel mqttChannel) {
        Set<String> topics = mqttChannel.getTopic();
        topics.parallelStream().forEach(topic -> cacheMap.delete(getTopic(topic), mqttChannel));
        return topics;
    }
    @Override
    public void suscribeSuccess(String deviceId, Set<String> topics) {
        doIfElse(topics, topics1 -> !CollectionUtils.isEmpty(topics), strings -> {
            MqttChannel channel = mqttChannelMap.get(deviceId);
            channel.setSubStatus(SubStatus.YES);
            channel.addTopic(strings);
            executorService.execute( () -> {
                Optional.ofNullable(channel).ifPresent(mqttChannel -> {
                    if (mqttChannel.isLogin()) {
                        strings.parallelStream().forEach(
                                topic -> {
                                    addChannel(topic, mqttChannel);
                                    sendRetain(topic, mqttChannel);
                                }
                        );
                    }
                });
            });
        });
    }



    @Override
    public void loginSuccess(Channel channel, String deviceId, MqttConnectMessage mqttConnectMessage) {
        channel.attr(login).set(true);
        channel.attr(_deviceId).set(deviceId);
        replyLogin(channel, mqttConnectMessage);
    }

    @Override
    public void publishSuccess(Channel channel, MqttPublishMessage mqttPublishMessage) {
        MqttFixedHeader mqttFixedHeader = mqttPublishMessage.fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttPublishMessage.variableHeader();
        MqttChannel mqttChannel = getMqttChannel(getDeviceId(channel));
        ByteBuf payload = mqttPublishMessage.payload();
        byte[] bytes = ByteBufUtil.copyByteBuf(payload);
        int messageId = mqttPublishVariableHeader.messageId();
        executorService.execute(() -> {
            if(channel.hasAttr(login) && channel != null) {
                boolean isRetain;
                switch (mqttFixedHeader.qosLevel()) {
                    case AT_MOST_ONCE:
                        break;
                    case AT_LEAST_ONCE:
                        sendPubBack(channel, messageId);
                        break;
                    case EXACTLY_ONCE:
                        sendPubRec(mqttChannel, messageId);
                        break;
                }
                if ((isRetain = mqttFixedHeader.isRetain() && mqttFixedHeader.qosLevel() != MqttQoS.AT_MOST_ONCE)) {
                    saveRetain(mqttPublishVariableHeader.topicName(),
                            RetainMessage.builder().byteBuf(bytes)
                            .qoS(mqttFixedHeader.qosLevel())
                            .build(), false);
                } else if (mqttFixedHeader.isRetain() && mqttFixedHeader.qosLevel() == MqttQoS.AT_MOST_ONCE) {
                    saveRetain(mqttPublishVariableHeader.topicName(),
                            RetainMessage.builder().byteBuf(bytes)
                                    .qoS(mqttFixedHeader.qosLevel())
                                    .build(), true);
                }
                if (!mqttChannel.checkRecevice(messageId)) {
                    push(mqttPublishVariableHeader.topicName(), mqttFixedHeader.qosLevel(), bytes, isRetain);
                    mqttChannel.addRecevice(messageId);
                }
            }
        });
    }
    private void push(String topic, MqttQoS qos, byte[] bytes, boolean isRetain) {
        Collection<MqttChannel> subChannels = getChannels(topic, topic1 -> cacheMap.getData(getTopic(topic1)));
        if(!CollectionUtils.isEmpty(subChannels)) {
            subChannels.parallelStream().forEach(subChannel -> {
                switch (subChannel.getSessionStatus()) {
                    case OPEN:
                        if (subChannel.isActive()) {
                            switch (qos) {
                                case AT_LEAST_ONCE:
                                    sendQosConfirmMsg(MqttQoS.AT_LEAST_ONCE, subChannel, topic, bytes);
                                    break;
                                case AT_MOST_ONCE:
                                    sendQos0Msg(subChannel.getChannel(), topic, bytes);
                                    break;
                                case EXACTLY_ONCE:
                                    sendQosConfirmMsg(MqttQoS.EXACTLY_ONCE, subChannel, topic, bytes);
                                    break;
                            }
                        } else {
                            if (!subChannel.isCleanSession() && !isRetain) {
                                clientSessionService.saveSessionMsg(subChannel.getDeviceId(),
                                        SessionMessage.builder().byteBuf(bytes)
                                                .qoS(qos)
                                                .topic(topic)
                                                .build());
                            }
                            break;
                        }
                    case CLOSE:
                        clientSessionService.saveSessionMsg(subChannel.getDeviceId(),
                                SessionMessage.builder()
                                        .byteBuf(bytes)
                                        .qoS(qos)
                                        .topic(topic)
                                        .build());
                }
            });
        }
    }

    @Override
    public void closeSuccess(String deviceId, boolean isDisconnect) {
        if (StringUtils.isNoneBlank(deviceId)) {
            executorService.execute(() -> {
                MqttChannel mqttChannel = mqttChannelMap.get(deviceId);
                Optional.ofNullable(mqttChannel).ifPresent(
                        mqttChannel1 -> {
                            mqttChannel1.setSessionStatus(SessionStatus.CLOSE);
                            mqttChannel.close();
                            mqttChannel.setChannel(null);
                            if (!mqttChannel1.isCleanSession()) {
                                ConcurrentHashMap<Integer, SendMqttMessage> message = mqttChannel1.getMessage();
                                Optional.ofNullable(message).ifPresent(
                                        map -> {
                                            map.forEach(((integer, sendMqttMessage) -> doIfElse(sendMqttMessage,
                                                    sendMqttMessage1 -> sendMqttMessage1.getConfirmStatus() == ConfirmStatus.PUB,
                                                    sendMqttMessage1 -> {
                                                clientSessionService.saveSessionMsg(mqttChannel.getDeviceId(), SessionMessage.builder()
                                                .byteBuf(sendMqttMessage1.getByteBuf())
                                                .qoS(sendMqttMessage1.getQos())
                                                .topic(sendMqttMessage1.getTopic())
                                                .build());
                                            })));
                                        }
                                );
                            }
                        }
                );
            });
        }
    }

    @Override
    public void sendWillMsg(WillMessage willMeaasge) {
        Collection<MqttChannel> mqttChannels = getChannels(willMeaasge.getWillTopic(), topic -> cacheMap.getData(getTopic(topic)));
        if (!CollectionUtils.isEmpty(mqttChannels)) {
            mqttChannels.forEach(mqttChannel -> {
                switch (mqttChannel.getSessionStatus()) {
                    case CLOSE:
                        clientSessionService.saveSessionMsg(mqttChannel.getDeviceId(), SessionMessage.builder()
                                .topic(willMeaasge.getWillTopic())
                                .qoS(MqttQoS.valueOf(willMeaasge.getQos()))
                                .byteBuf(willMeaasge.getWillMessage().getBytes())
                                .build());
                            break;
                    case OPEN:
                        writeWillMsg(mqttChannel, willMeaasge);
                        break;
                }

            });
        }
    }

    @Override
    public void unsubscribe(String deviceId, List<String> topics1) {
        Optional.ofNullable(mqttChannelMap.get(deviceId)).ifPresent(
                mqttChannel -> {
                    topics1.forEach(t -> {
                        deleteChannel(t, mqttChannel);
                    });
                });
    }

    @Override
    public void doPubrel(Channel channel, int mqttMessage) {
        MqttChannel mqttChannel = getMqttChannel((getDeviceId(channel)));
        doIfElse(mqttChannel,mqttChannel1 -> mqttChannel1.isLogin(), mqttChannel1 -> {
            mqttChannel1.removeRecevice(mqttMessage);
            sendToPubComp(channel, mqttMessage);
        } );
    }

    @Override
    public void doPubrec(Channel channel, int mqttMessage) {
        sendPubRel(channel, false, mqttMessage);
    }

    private void saveRetain(String topic, RetainMessage retainMessage, boolean isClean) {
        ConcurrentLinkedQueue<RetainMessage> retainMessages = retain.getOrDefault(topic, new ConcurrentLinkedQueue<>());
        if (!retainMessages.isEmpty() && isClean) {
            retainMessages.clear();
        }
        boolean flag;
        do {
            flag = retainMessages.add(retainMessage);
        } while (!flag);
        retain.put(topic, retainMessages);
    }
    public void sendRetain(String topic, MqttChannel mqttChannel) {
        retain.forEach( (s, retainMessages) -> {
            if (StringUtils.startsWith(s, topic)) {
                Optional.ofNullable(retainMessages).ifPresent(
                        pubMessage1 -> {
                            retainMessages.parallelStream().forEach(retainMessage -> {
                                log.info("[发送保留信息]" + mqttChannel.getChannel().remoteAddress() + ":" + retainMessage.getString() + "[Success]");
                                switch (retainMessage.getQoS()) {
                                    case AT_MOST_ONCE:
                                        sendQos0Msg(mqttChannel.getChannel(), s, retainMessage.getByteBuf());
                                        break;
                                    case AT_LEAST_ONCE:
                                        sendQosConfirmMsg(MqttQoS.AT_LEAST_ONCE, mqttChannel, s, retainMessage.getByteBuf());
                                        break;
                                    case EXACTLY_ONCE:
                                        sendQosConfirmMsg(MqttQoS.EXACTLY_ONCE, mqttChannel, s, retainMessage.getByteBuf());
                                        break;
                                }
                            });
                        }
                );
            }
        });
    }
}
