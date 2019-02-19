package com.niu.chat.config;

import com.alibaba.druid.sql.visitor.functions.Bin;
import com.niu.chat.common.utils.ByteBufUtil;
import com.niu.chat.common.utils.StringUtil;
import com.niu.chat.constont.LikeSomeCacheTemplate;
import com.niu.chat.service.RedisService;
import com.niu.chat.task.MsgAsyncTask;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;

/**
 * @author: justinniu
 * @date: 2018-11-28 15:16
 * @desc:
 **/
@Component
@Qualifier("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Autowired
    private RedisService redisService;
    @Autowired
    private LikeSomeCacheTemplate cacheTemplate;
    @Autowired
    private MsgAsyncTask task;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof TextWebSocketFrame) {
            textWebSocketFrame(ctx, (TextWebSocketFrame) o);
        } else if (o instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) o);
        }
    }

    private void textWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Channel incoming = ctx.channel();
        String rName = StringUtil.getName(msg.text());
        String rMsg = StringUtil.getMsg(msg.text());
        if (rMsg.isEmpty()) {
            return;
        }
        if (redisService.check(incoming.id(), rName)) {
            cacheTemplate.save(rName, rMsg);
            redisService.save(incoming.id(), rName);
            redisService.saveChannel(rName, incoming);
        } else {
            incoming.writeAndFlush(new TextWebSocketFrame("存在二次登陆，系统以为你走自动断开本次链接"));
            channels.remove(ctx.channel());
            ctx.close();
            return;
        }
        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush(new TextWebSocketFrame("[" + rName + "]"));
            } else {
                channel.writeAndFlush(new TextWebSocketFrame(rMsg + "[" + rName + "]"));
            }
        }
    }
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame imgBack = (BinaryWebSocketFrame) frame.copy();
            for (Channel channel : channels) {
                channel.writeAndFlush(imgBack.retain());
            }
            BinaryWebSocketFrame img = (BinaryWebSocketFrame) frame;
            ByteBuf byteBuf = img.content();
            try {
                FileOutputStream outputStream = new FileOutputStream("D:\\a.jpg");
                byteBuf.readBytes(outputStream, byteBuf.capacity());
                byteBuf.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String name = (String) redisService.getName(ctx.channel().id());
        redisService.deleteChannel(name);
        redisService.delete(ctx.channel().id());
        channels.remove(ctx.channel());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress());
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        task.saveChatMsgTask();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
