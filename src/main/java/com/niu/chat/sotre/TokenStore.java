package com.niu.chat.sotre;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 10:53
 * @desc
 */
public class TokenStore {
    private static Map<String, Object> TokenStoreMap = new ConcurrentHashMap<>();

    public static void add(String token, Object userId) {
        TokenStoreMap.put(token, userId);
    }

    public static void remove(String token) {
        TokenStoreMap.remove(token);
    }

    public static Object get(String token) {
        return TokenStoreMap.get(token);
    }
}
