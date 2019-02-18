package com.niu.chat.config;

import com.niu.chat.common.utils.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Auth justinniu
 * @Date 2018/12/6
 * @Desc
 */
@Slf4j
@Component
@Qualifier("tcpServerHandler")
@ChannelHandler.Sharable
public class TCPServerHandler extends ChannelInboundHandlerAdapter {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String ChannelID = null;
        try {
            String data = (String) msg;
            if (DataValida.ValidateHeadAndFeet(data)) {
                data = DataResction.ResctionHeadAndFeet(data);
                if (DataValida.ValidateCRCCode(DataResction.ResctionData(data), DataResction.ResctionCRCCode(data))) {
                    data = DataResction.ResctionData(data);
                    ChannelID = DataResction.ResctionId(data);
                    if (!Constants.hasChannelID(ChannelID)) {
                        String realChannelID = Constants.inMap(ctx.channel());
                        Constants.ChangeClientId(realChannelID, ChannelID);
                    }
                    if (Constants.hasChannelID(ChannelID)) {
                        Constants.changeChannel(ChannelID, ctx.channel());
                    }
                    data = DataResction.ResctionDataNoID(data);
                    String type = DataResction.ResctionType(data);
                    String RealData = DataResction.ResctionRealData(data);
                    switch (type) {
                        case "s":
                            futureByController(ctx, RealData, ChannelID);
                            break;
                        case "g":
                            futureByLoLa(ctx, RealData, ChannelID);
                            break;
                        case "v":
                            RealData = DataResction.ResctionPower(RealData);
                            futurByCharge(ctx, RealData, ChannelID);
                            break;
                        case "p":
                            futureByPStates(ctx, RealData, ChannelID);
                            break;
                        case "r" :
                            futureByException(ctx, RealData, ChannelID);
                            break;
                        case "j":
                            futureByChlientResult(ctx, RealData, ChannelID);
                            break;
                        case "t":
                            futureByTesting(ctx, RealData, ChannelID);
                            break;
                        default:
                            ctx.writeAndFlush(CallbackMessage.sendString(CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.ERROR)));
                    }
                } else {
                    ctx.writeAndFlush(CallbackMessage.ERROR.duplicate());
                    ctx.close();
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void futureByTesting(ChannelHandlerContext ctx, String realData, String channelID) {
        Set<String> ids = Constants.getIdList();
        log.info("测试广播事件执行");
        for (String item : ids) {
            SendUtil sendUtil = new SendUtil();
            Channel channel = Constants.get(item);
            if (channel != null) {
                sendUtil.sendAll(realData, channel, item, Constants.RESULT_TEXT);
            }
        }
    }

    private void futureByChlientResult(ChannelHandlerContext ctx, String realData, String ChannelID){
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------客户端执行结果");
                    }
                }, 0, TimeUnit.SECONDS
        );
        ctx.writeAndFlush(CallbackMessage.sendString(CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));
    }

    private void futureByException(ChannelHandlerContext ctx, final String realData, final String ChannelID) {
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------出现异常");
                    }
                }, 0, TimeUnit.SECONDS
        );
        ctx.writeAndFlush(CallbackMessage.sendString(
                CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));
    }

    private void futureByLoLa(ChannelHandlerContext ctx, final String realData, final String ChannelID) {
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------经纬度传输");
                    }
                }, 0, TimeUnit.SECONDS
        );
        ctx.writeAndFlush(CallbackMessage.sendString(
                CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));


    }
    private void futureByPStates(ChannelHandlerContext ctx, final String realData, final String ChannelID) {
        log.info("检测物体事件执行");
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------物体检测类型");
                    }
                }, 0, TimeUnit.SECONDS
        );
        ctx.writeAndFlush(CallbackMessage.sendString(
                CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));
    }

    private void futureByController(ChannelHandlerContext ctx, final String realData, final String ChannelID) {
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------控制类型");
                    }
                }, 0, TimeUnit.SECONDS
        );
        //ctx.writeAndFlush(CallbackMessage.sendString(
            //    CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));
        ctx.writeAndFlush(CallbackMessage.Check1_test.duplicate());
    }

    private void futurByCharge(ChannelHandlerContext ctx, final String realData, final String ChannelID) {
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("-------尝试执行SQL操作-------设备电量");
                    }
                }, 0, TimeUnit.SECONDS
        );
        ctx.writeAndFlush(CallbackMessage.sendString(
                CRC16.getAllString(ChannelID, Constants.RESULT_TYPE, Constants.SUCCESS)));
    }

    private String getUpdateKey(String channelID, String pStates, String realData) {
        Integer openId = null;
        for (int i = 0; i < realData.length(); i++) {
            if (pStates.charAt(i) != realData.charAt(i)) {
                openId = i;
                break;
            }
        }
        return channelID + "_" + openId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("TCPServerHandler Exception", cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Constants.add(String.valueOf(UUID.randomUUID()), ctx.channel());
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Disconnected client " + ctx.channel().remoteAddress());
        Constants.remove(ctx.channel());
    }
}
