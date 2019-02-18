package com.niu.chat.common.properties;

import com.niu.chat.common.enums.ProtocolEnum;
import com.niu.chat.common.mqtts.AbstractMqttHandler;
import com.niu.chat.common.websockets.AbstractWebSocketHandler;
import com.niu.chat.common.websockets.IWebSocketHandler;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: justinniu
 * @date: 2018-11-27 17:20
 * @desc:
 **/
@Data
@ConfigurationProperties(prefix = "netty")
public class InitNetty {

    private ProtocolEnum protocol;

    private int webport;

    private int tcpport;

    private int mqttport;

    private int bossThread;

    private int workerThread;

    private boolean keepalive;

    private int backlog;

    private boolean nodelay;

    private boolean reuseaddr;

    private String serverName;

    private int sndbuf;

    private int revbuf;

    private int heart;

    private boolean ssl;

    private String jksFile;

    private String jksStorePassword;

    private String jksCertificatePassword;

    private Class<AbstractMqttHandler> mqttHandler;

    private int initalDelay;

    private int period;
}

