package com.niu.chat.bootstrap.channel.cache;

import com.niu.chat.bootstrap.BaseApi;
import com.niu.chat.bootstrap.IChannelService;
import com.niu.chat.bootstrap.bean.WillMeaasge;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
@Slf4j
@Component
@Data
@NoArgsConstructor
public class WillService implements BaseApi {
    @Autowired
    IChannelService channelService;

    private static ConcurrentHashMap<String, WillMeaasge> willMessages = new ConcurrentHashMap<>();

    public void  save(String deviceId, WillMeaasge build) {
        willMessages.put(deviceId, build);
    }

    public void doSend(String deviceId) {
        if (StringUtils.isNotBlank(deviceId) && null != (willMessages).get(deviceId)) {
            WillMeaasge willMeaasge = willMessages.get(deviceId);
            channelService.sendWillMsg(willMeaasge);
            if (!willMeaasge.isRetain()) {
                willMessages.remove(deviceId);
                log.info("deviceId will message[" + willMeaasge.getWillMessage() + "] is removed");
            }
        }
    }

    public void del(String deviceId) {
        willMessages.remove(deviceId);
    }
}
