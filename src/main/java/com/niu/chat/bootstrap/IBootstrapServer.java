package com.niu.chat.bootstrap;

import com.niu.chat.common.properties.InitNetty;

/**
 * @author: justinniu
 * @date: 2018-11-30 09:59
 * @desc:
 **/
public interface IBootstrapServer {

    void shutdown();

    void setServerBean(InitNetty serverBean);

    void start();
}
