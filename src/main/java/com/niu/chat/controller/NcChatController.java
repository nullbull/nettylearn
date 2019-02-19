package com.niu.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.chat.common.utils.CookieUtil;
import com.niu.chat.constont.CookieConstant;
import com.niu.chat.constont.H5Constant;
import com.niu.chat.entity.User;
import com.niu.chat.entity.UserMsg;
import com.niu.chat.mapper.UserMsgMapper;
import com.niu.chat.service.IUserService;
import com.niu.chat.sotre.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 14:34
 * @desc
 */
@Controller
@RequestMapping("chat")
public class NcChatController {

    @Autowired
    private UserMsgMapper userMsgMapper;

    @Autowired
    private IUserService userService;

    @GetMapping("/netty")
    public ModelAndView netty(@RequestParam(value = "page", defaultValue = "1")Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                Map<String, Object> map){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (null == cookie) {
            map.put("msg", "cookie中不存在token");
            return new ModelAndView(H5Constant.LOGIN, map);
        }
        Integer userId = (Integer) TokenStore.get(cookie.getValue());
        if (null == userId) {
            map.put("msg", "用户信息不存在");
        }
        User user  = userService.findOne(userId);
        List<UserMsg> userList = userMsgMapper.selectPage(new Page<UserMsg>(page - 1, size), new EmptyWrapper<UserMsg>()).getRecords();
        map.put("userName", user.getUserName()) ;
        map.put("userMsgList", userList);
        return new ModelAndView(H5Constant.ALLCHAT, map);
    }
}
