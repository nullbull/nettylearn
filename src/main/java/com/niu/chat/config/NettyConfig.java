package com.niu.chat.config;

import com.niu.chat.common.properties.InitNetty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.ws.Action;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author: justinniu
 * @date: 2018-11-28 14:59
 * @desc:
 **/
@Component
public class NettyConfig {
    @Autowired
    private InitNetty nettyConfig;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(nettyConfig.getBossThread());
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workGroup() {
        return new NioEventLoopGroup(nettyConfig.getWorkerThread());
    }

    @Bean(name = "webSocketAddress")
    public InetSocketAddress tcpPost() {
        return new InetSocketAddress(nettyConfig.getWebport());
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>();
        options.put(ChannelOption.TCP_NODELAY, nettyConfig.isNodelay());
        options.put(ChannelOption.SO_KEEPALIVE, nettyConfig.isKeepalive());
        options.put(ChannelOption.SO_BACKLOG, nettyConfig.getBacklog());
        options.put(ChannelOption.SO_REUSEADDR, nettyConfig.isReuseaddr());
        return options;
    }

    @Autowired
    @Qualifier("somethingChannelInitializer")
    private NettyWebSocketChannelInitializer nettyWebSocketChannelInitializer;

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(nettyWebSocketChannelInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }
}
