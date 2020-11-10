package com.wuqiong.tx.service;

import com.wuqiong.tx.mapper.UserMapper;
import com.wuqiong.tx.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    @Override
    public void addUser(User user) {
        int a = 1/user.getFrom();
        userMapper.addUser(user);
    }

    @Override
    public User getUserByID(String companyID, long id) {
        return userMapper.getUserByID(id);
    }
}
