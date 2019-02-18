package com.niu.chat.service.impl;

import com.niu.chat.entity.UserMsg;
import com.niu.chat.mapper.UserMapper;
import com.niu.chat.mapper.UserMsgMapper;
import com.niu.chat.service.IUserMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author justinniu
 * @since 2018-12-07
 */
@Service
public class UserMsgServiceImpl extends ServiceImpl<UserMsgMapper, UserMsg> implements IUserMsgService {


}
