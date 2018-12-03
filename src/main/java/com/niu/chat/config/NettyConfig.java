package com.niu.chat.config;

import com.niu.chat.common.properties.InitNetty;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.xml.ws.Action;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: justinniu
 * @date: 2018-11-28 14:59
 * @desc:
 **/
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
}
