package com.niu.chat.bootstrap;

/**
 * @author: justinniu
 * @date: 2018-11-30 09:57
 * @desc:
 **/
public interface IBaseAuthService {

    boolean authorized(String userName, String password);

}

