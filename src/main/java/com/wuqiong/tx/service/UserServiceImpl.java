package com.wuqiong.tx.service;

import com.wuqiong.tx.mapper.UserMapper;
import com.wuqiong.tx.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUserByID(String companyID, long id) {
        return userMapper.getUserByID(id);
    }
}
