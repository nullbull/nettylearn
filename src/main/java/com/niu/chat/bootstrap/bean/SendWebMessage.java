package com.niu.chat.bootstrap.bean;

import com.niu.chat.common.enums.ConfirmStatus;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendWebMessage {

    private int messageId;

    private Channel channel;

    private volatile ConfirmStatus confirmStatus;

    private long time;

    private byte[] byteBuf;

    private boolean isRetain;

}
