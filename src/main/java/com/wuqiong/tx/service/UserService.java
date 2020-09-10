package com.wuqiong.tx.service;

import com.wuqiong.tx.entity.User;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public interface UserService {

    /**
     * 新增用户
     * @param user
     */
    void addUser(User user);

    User getUserByID(String companyID, long id);
}
