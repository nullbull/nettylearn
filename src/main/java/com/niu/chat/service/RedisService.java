package com.niu.chat.service;

import com.alibaba.fastjson.JSON;
import com.niu.chat.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auth justinniu
 * @Date 2018/12/7
 * @Desc
 */
@Component
public class RedisService {
    @Autowired
    private RedisUtil redisUtil;

    String CHANNEL_MAP = "channle:map";
    String ONLINE = "online:";
    public boolean saveChannel(Object name, Object channel) {
        return redisUtil.hset(CHANNEL_MAP, (String)name, JSON.toJSON(channel));
    }

    public Long getSize() {
        return redisUtil.hlen(CHANNEL_MAP);
    }

    public boolean save(Object id, Object name) {
        return redisUtil.hset(ONLINE, (String)id, name);
    }

    public void deleteChannel(Object name) {
        redisUtil.hdel(CHANNEL_MAP, (String)name);
    }

    public Object getChannel(Object name) {
        return JSON.parse((String) redisUtil.hget(CHANNEL_MAP, (String)name));
    }
    public Object getName(Object id) {
        return redisUtil.hget(ONLINE, (String)id);
    }

    public boolean check(Object id, Object name) {
        Object temp = redisUtil.hget(ONLINE, (String)id);
        if (null == temp) {
            return true;
        }
        if ((String)temp == name) {
            return true;
        }
        return false;
    }

    public Object getOnline() {
        List<Object> result = new ArrayList<>();
        result = redisUtil.hgetAll(ONLINE).keySet().stream().collect(Collectors.toList());
        return result;
    }


}
