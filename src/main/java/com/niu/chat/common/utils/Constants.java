package com.niu.chat.common.utils;

import io.netty.channel.Channel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: justinniu
 * @date: 2018-11-28 09:23
 * @desc:
 **/
public class Constants {


    private String FORMAT =  "yyyy-MM-dd hh:MM:ss";
    ThreadLocal<SimpleDateFormat> dateFormatHolder = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new SimpleDateFormat(FORMAT);
        }
    };
    //帧头
    public static final String HEAD = "gz";
    //帧尾
    public static final String TAIL = "xr";
    //控制锁状态
    public static final String CONTROL_TYPE = "s";
    //24锁 全部关闭状态
    public static final String LOCKS = "nnnnnnnnnnnnnnnnnnnnnnnn";
    //开锁标识
    public static final char OPEN = 'y';
    //客户端执行结果
    public static final String RESULT_TYPE = "j";
    //客户端执行测试结果
    public static final String RESULT_TEXT = "t";
    //服务器执行结果
    public static final String SUCCESS = "yyyyyyyyyyyyyyyyyyyyyyyy";
    public static final String ERROR = "nnnnnnnnnnnnnnnnnnnnnnnn";

    private static Map<String, Channel> map = new ConcurrentHashMap<>();

    public static void add(String chlientId, Channel channel) {
        map.put(chlientId, channel);
    }

    public static boolean hasChannelID(String channelId) {
        return map.containsKey(channelId);
    }

    public static Channel get(String clientId) {
        return map.get(clientId);
    }

    public static void remove(Channel channel) {
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (map.get(key) == channel) {
                map.remove(key);
            }
        }
    }

    public static int getSize() {
        return map.size();
    }

    public static Set<String> getIdList() {
        return map.keySet();
    }

    public static String inMap(Channel channel) {
        String clientId = null;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (map.get(key) == channel) {
                clientId = key;
                break;
            }
        }
        return  clientId;
    }

    public static void ChangeClientId(String old, String newId) {
        Channel channel = map.get(old);
        map.remove(old);
        map.put(newId, channel);
    }

    public static void changeChannel(String channelId, Channel channel) {
        map.remove(channelId);
        map.put(channelId, channel);
    }

    public static enum StateEnum {
        SUCCESS(1, "OK"),
        FALID(2, "FALID");
        int code;
        String msg;
        StateEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
        HashMap<Integer, StateEnum> enumMap = new HashMap<>();

        {
            for (StateEnum stateEnum : values()) {
                enumMap.put(stateEnum.getCode(), stateEnum);
            }
        }
        public StateEnum getEnumByCode(int code) {
            return enumMap.get(code);
        }
    }


}
