package com.niu.chat.config;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Auth justinniu
 * @Date 2018/12/6
 * @Desc
 */
@Component
@Qualifier("tcpChannelInitializer")
public class NettyTcpChannelInitizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("tcpServerHandler")
    private TCPServerHandler tcpServerHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LineBasedFrameDecoder(1024));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(tcpServerHandler);
    }
}
