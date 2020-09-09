package com.wuqiong.tx.service;

import com.wuqiong.tx.entity.User;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public interface UserService {

    User getUserByID(String companyID, long id);
}
