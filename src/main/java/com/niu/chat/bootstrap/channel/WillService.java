package com.niu.chat.bootstrap.channel;

import com.niu.chat.bootstrap.IChannelService;
import com.niu.chat.bootstrap.bean.WillMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auth justinniu
 * @Date 2018/12/5
 * @Desc
 */
@Slf4j
@Component
@Data
@NoArgsConstructor
public class WillService {
    @Autowired
    IChannelService channelService;

    private static ConcurrentHashMap<String, WillMessage> willMessageMap = new ConcurrentHashMap<>();

    private void save(String deviceId, WillMessage build) {
        willMessageMap.put(deviceId, build);
    }

    public void doSend(String deviceId) {
        if (StringUtils.isNotBlank(deviceId) && (willMessageMap.get(deviceId)) != null) {
            WillMessage willMessage = willMessageMap.get(deviceId);
            channelService.sendWillMsg(willMessage);
            if (!willMessage.isRetain()) {
                willMessageMap.remove(deviceId);
                log.info("deviceId will Message [{}]", willMessage.getWillMessage());
            }
        }
    }
    public void del(String deviceId) {
        willMessageMap.remove(deviceId);
    }
}
