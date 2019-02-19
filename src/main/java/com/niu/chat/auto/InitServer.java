package com.niu.chat.auto;

import com.niu.chat.bootstrap.IBootstrapServer;
import com.niu.chat.bootstrap.NettyBootstrapServer;
import com.niu.chat.common.properties.InitNetty;

/**
 * @author justinniu
 * @date 2018-11-27 14:14
 * @desc
 **/
public class InitServer {
    private InitNetty serverBean;

    public InitServer(InitNetty serverBean) {
        this.serverBean = serverBean;
    }

    IBootstrapServer bootstrapServer;

    public void open(){
        if(serverBean!=null){
            bootstrapServer = new NettyBootstrapServer();
            bootstrapServer.setServerBean(serverBean);
            bootstrapServer.start();
        }
    }


    public void close(){
        if(bootstrapServer!=null){
            bootstrapServer.shutdown();
        }
    }
}
