package com.niu.chat.bootstrap.bean;

import com.niu.chat.common.enums.SessionStatus;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Data
@Builder
public class WebChannel {

    private transient volatile Channel channel;

    private String msgId;

    private boolean isWill;

    private volatile SessionStatus sessionStatus; //在线 - 离线

    private volatile boolean cleanSession; //当为 true 时channel close 时 从缓存中删除 此channel

    private ConcurrentHashMap<Integer,SendWebMessage> message;// messageId - message(toid) //待确认消息

    private Set<Integer> receive;

    public boolean isLogin(){
        return Optional.ofNullable(this.channel).map(channel1 -> {
            AttributeKey<Boolean> _login = AttributeKey.valueOf("login");
            return channel1.isActive() && channel1.hasAttr(_login);
        }).orElse(false);
    }


    public void close(){
        Optional.ofNullable(this.channel).ifPresent(channel1 -> channel1.close());
    }


    public boolean isActive(){
        return channel!=null&&this.channel.isActive();
    }

}
