package com.niu.chat;

import com.niu.chat.config.NettyConfig;
import com.niu.chat.config.NettyTcpConfig;
import com.niu.chat.config.TCPServer;
import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //定时任务支持
public class ChatApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ChatApplication.class, args);
        NettyConfig nettyConfig = context.getBean(NettyConfig.class);
        NettyTcpConfig nettyTcpConfig = context.getBean(NettyTcpConfig.class);
        TCPServer tcpServer = context.getBean(TCPServer.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Web端Netty通信服务端启动成功！端口：8090");
                    tcpServer.startWeb();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("TCP端Netty通信服务端启动成功！端口：8092");
                    tcpServer.startTcp();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
