package com.niu.chat.constont;

import com.niu.chat.entity.UserMsg;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 14:02
 * @desc
 */
@Component
public class LikeSomeCacheTemplate {
    private List<UserMsg> SomeCache = new LinkedList<>();

    public void save(Object user,Object msg){
        UserMsg userMsg = new UserMsg();
        userMsg.setName(String.valueOf(user));
        userMsg.setMsg(String.valueOf(msg));
        SomeCache.add(userMsg);
    }

    public List<UserMsg> cloneCacheMap(){
        return SomeCache;
    }

    public void clearCacheMap(){
        SomeCache.clear();
    }
}
