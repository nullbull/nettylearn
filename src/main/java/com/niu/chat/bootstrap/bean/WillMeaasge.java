package com.niu.chat.bootstrap.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WillMeaasge {

    private String willTopic;

    private String willMessage;

    private  boolean isRetain;

    private int qos;

}
