package com.niu.chat.service;

import com.niu.chat.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author justinniu
 * @since 2018-12-07
 */
public interface IUserService extends IService<User> {

    User findOne(Integer id);

    User findByUserName(String userName);

    List<User> findAll();

    Integer create(User user);

}
