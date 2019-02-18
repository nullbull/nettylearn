package com.niu.chat.config;

import io.netty.bootstrap.ServerBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Auth justinniu
 * @Date 2018/12/6
 * @Desc
 */
@Component
public class TCPServer {
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    @Autowired
    @Qualifier("tcpServerBootstrap")
    private ServerBootstrap tcpServerBootstrap;

//    @Autowired
//    Qualifier

}
