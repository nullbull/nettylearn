package com.niu.chat.controller;

import com.niu.chat.common.utils.CookieUtil;
import com.niu.chat.constont.CookieConstant;
import com.niu.chat.constont.H5Constant;
import com.niu.chat.entity.User;
import com.niu.chat.service.IUserService;
import com.niu.chat.service.impl.UserServiceImpl;
import com.niu.chat.sotre.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 10:44
 * @desc
 */
@Controller
@RequestMapping("/su")
public class NcChangeController {
    @Autowired
    private IUserService userService;

    /**
     * 我的中心界面
     * @param map
     * @return
     */
    @GetMapping("/me")
    public ModelAndView Me(Map<String, Object> map) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Cookie cookie  = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null) {
            map.put("msg", "cookie中不纯在token");
            return new ModelAndView(H5Constant.LOGIN_SUI, map);
        }
        Integer userId = (Integer) TokenStore.get(cookie.getValue());
        if (null == userId) {
            map.put("msg", "用户信息不存在");
            return new ModelAndView(H5Constant.ME, map);
        }
        User user = userService.findOne(userId);
        map.put("userName", user.getUserName());
        return new ModelAndView(H5Constant.ME, map);
    }

    @GetMapping("find")
    public ModelAndView find(Map<String, Object> map) {
        return new ModelAndView(H5Constant.FIND);
    }

    @GetMapping("chat")
    public ModelAndView chat(Map<String, Object> map) {
        return new ModelAndView(H5Constant.CHAT);
    }

    @GetMapping("home")
    public ModelAndView home(Map<String, Object> map) {
        return new ModelAndView(H5Constant.HOME);
    }
}
