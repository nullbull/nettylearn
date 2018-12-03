package com.niu.chat.bootstrap.channel;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.niu.chat.bootstrap.BaseApi;
import com.niu.chat.bootstrap.IChannelService;
import com.niu.chat.bootstrap.bean.MqttChannel;
import com.niu.chat.bootstrap.bean.RetainMessage;
import com.niu.chat.bootstrap.channel.cache.CacheMap;
import com.niu.chat.bootstrap.scan.ScanRunnable;
import com.niu.chat.common.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
@Slf4j
public abstract class AbstractChannelService extends PublishApiService implements IChannelService, BaseApi {

    protected AttributeKey<Boolean> login = AttributeKey.valueOf("login");

    protected AttributeKey<String> _deviceId = AttributeKey.valueOf("deviceId");

    protected static char SPLITOR = '/';

    protected ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    protected static CacheMap<String, MqttChannel> cacheMap = new CacheMap<>();

    protected static ConcurrentHashMap<String, MqttChannel> mqttChannelMap = new ConcurrentHashMap<>();

    protected static ConcurrentHashMap<String, ConcurrentLinkedQueue<RetainMessage>> retain = new ConcurrentHashMap<>();

    protected static Cache<String, Collection<MqttChannel>> mqttChannelCache = CacheBuilder.newBuilder().maximumSize(100).build();

    public AbstractChannelService(ScanRunnable scanRunnable) {
        super(scanRunnable);
    }

    protected Collection<MqttChannel> getChannels(String topic, TopicFilter topicFilter) {
        try {
            return mqttChannelCache.get(topic, () -> topicFilter.filter(topic));
        } catch (Exception e) {
            log.info("Guava cache key topic [{}] channel value == null", topic);
        }
        return null;
    }

    @FunctionalInterface
    interface TopicFilter {
        Collection<MqttChannel> filter(String topic);
    }

    protected boolean deleteChannel(String topic, MqttChannel mqttChannel) {
        return Optional.ofNullable(topic).map(s -> {
            mqttChannelCache.invalidate(s);
            return cacheMap.delete(getTopic(topic), mqttChannel);
        }).orElse(false);
    }

    protected boolean addChannel(String topic, MqttChannel channel) {
        return Optional.ofNullable(topic).map(s -> {
                    mqttChannelCache.invalidate(topic);
                    return cacheMap.putData(getTopic(topic), channel); })
                .orElse(false);
    }

    protected MqttChannel getChannel(String deviceId) {
        return Optional.ofNullable(deviceId).map(id -> mqttChannelMap.get(getTopic(id)))
                .orElse(null);
    }
    @Override
    public String getDeviceId(Channel channel) {
        return Optional.ofNullable(channel).map( channel1 -> channel1.attr(_deviceId).get())
                .orElse(null);
    }
    public String[] getTopic(String topic) {
        return Optional.ofNullable(topic).map(
                s -> StringUtils.split(s, SPLITOR)).orElse(null);
    }
}
