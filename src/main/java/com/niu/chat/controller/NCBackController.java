package com.niu.chat.controller;

import com.niu.chat.common.utils.RedisUtil;
import com.niu.chat.common.utils.ResultVOUtil;
import com.niu.chat.common.utils.SendUtil;
import com.niu.chat.service.RedisService;
import com.niu.chat.vo.ResultVo;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * @Auth justinniu
 * @Date 2018/12/7
 * @Desc
 */
@RestController
@RequestMapping("/back")
public class NCBackController {
    @Autowired
    private RedisService redisTemplate;

    @GetMapping("/size")
    public ResultVo getSize() {
        return ResultVOUtil.success(redisTemplate.getSize());
    }

    @GetMapping("/online")
    public ResultVo getOnline() {
        return ResultVOUtil.success(redisTemplate.getOnline());
    }

    @PostMapping("/send")
    public ResultVo send(@RequestParam String name, @RequestParam String msg) {
        Channel channel = (Channel) redisTemplate.getChannel((String)name);
        if (channel == null) {
            return ResultVOUtil.error(555, "当前用户连接已断开");
        }
        String result = SendUtil.sendTest(msg, channel);
        return ResultVOUtil.success(result);
    }



}
