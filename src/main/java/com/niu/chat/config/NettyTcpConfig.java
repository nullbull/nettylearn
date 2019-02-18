package com.niu.chat.config;

import com.niu.chat.common.properties.InitNetty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auth justinniu
 * @Date 2018/12/6
 * @Desc
 */
@Configuration
public class NettyTcpConfig {

    @Autowired
    private InitNetty serverBean;

    @Autowired
    @Qualifier("tcpChannelInitializer")
    private NettyTcpChannelInitizer nettyTcpChannelInitizer;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(serverBean.getBossThread());
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(serverBean.getWorkerThread());
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPost() {
        return new InetSocketAddress(serverBean.getTcpport());
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
        optionObjectMap.put(ChannelOption.TCP_NODELAY, serverBean.isNodelay());
        optionObjectMap.put(ChannelOption.SO_KEEPALIVE, serverBean.isKeepalive());
        optionObjectMap.put(ChannelOption.SO_BACKLOG, serverBean.getBacklog());
        optionObjectMap.put(ChannelOption.SO_REUSEADDR, serverBean.isReuseaddr());
        return optionObjectMap;
    }

    @Bean(name = "tcpServerBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(nettyTcpChannelInitizer);
        Map<ChannelOption<?>, Object> optionObjectMap = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = optionObjectMap.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, optionObjectMap.get(option));
        }
        return b;    }



}
