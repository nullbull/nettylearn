package com.niu.chat.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

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

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;

    @Autowired
    @Qualifier("webSocketAddress")
    private InetSocketAddress webPort;

    private Channel serverChannel;
    private Channel tcpServerChannel;

    public void startWeb() throws Exception {
        serverChannel =  serverBootstrap.bind(webPort).sync().channel().closeFuture().sync().channel();
    }

    public void startTcp() throws Exception {
        tcpServerChannel = tcpServerBootstrap.bind(tcpPort).sync().channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void stop() throws Exception {
        serverChannel.close();
        serverChannel.parent().close();
        tcpServerChannel.close();
        tcpServerChannel.parent().close();
    }


}
