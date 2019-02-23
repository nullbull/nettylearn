package com.niu.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.chat.entity.User;
import com.niu.chat.mapper.UserMapper;
import com.niu.chat.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author justinniu
 * @since 2018-12-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User findOne(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findByUserName(String userName) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("user_name", userName));
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public User create(User user) {
        return userMapper.insert(user);
    }

}
