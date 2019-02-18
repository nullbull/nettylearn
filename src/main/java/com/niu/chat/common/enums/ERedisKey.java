package com.niu.chat.common.enums;

/**
 * @author  justinniu
 * @Date 2018/11/4
 * @Desc
 */
public enum ERedisKey {

    EXPRESS_ORDER("ExpressOrder:id:");
    /**
     * 键名;
     */
    private String name;
    /**
     * 过期时间
     */
    private int expireTime = 24 * 3600 * 1000;
    ERedisKey(String name) {
        this.name = name;
    }
    ERedisKey(String name, int expireTime) {
        this.expireTime = expireTime;
        this.expireTime = expireTime;
    }

    public String getName() {
        return name;
    }

    public int getExpireTime() {
        return expireTime;
    }
}
