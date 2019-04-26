package com.niu.chat.task;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.chat.constont.LikeSomeCacheTemplate;
import com.niu.chat.entity.User;
import com.niu.chat.entity.UserMsg;
import com.niu.chat.mapper.UserMapper;
import com.niu.chat.mapper.UserMsgMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 13:52
 * @desc
 */
@Component
public class MsgAsyncTask {
    @Autowired
    private LikeSomeCacheTemplate template;

    @Autowired
    private UserMsgMapper userMsgMapper;

    @Autowired
    private UserMapper userMapper;

    @Async
    public Future<Boolean> saveChatMsgTask() throws Exception {
        List<UserMsg> userMsgList = template.cloneCacheMap();
        for (UserMsg i : userMsgList) {
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", i.getName()));
            if (null != user) {
                userMsgMapper.insert(i);
            }
        }
        template.clearCacheMap();
        return new AsyncResult<>(true);
    }
}
