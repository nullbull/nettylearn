package com.niu.chat.common.properties;

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
@ConfigurationProperties(prefix = "inchat")
public class InitNetty {
    private int webport;

    private int bossThread;

    private int workerThread;

    private boolean keepalive;

    private int backlog;

    private boolean nodelay;

    private boolean reuseaddr;

    private  int  sndbuf;

    private int revbuf;

    private int heart;

    private int period;

    private String serverName;

    private int initalDelay;

    private int maxContext;

    private String webSocketPath;

    private Class<AbstractWebSocketHandler> webSocketHandler;

}
