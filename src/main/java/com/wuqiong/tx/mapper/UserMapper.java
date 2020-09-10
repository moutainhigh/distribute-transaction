package com.wuqiong.tx.mapper;

import com.wuqiong.tx.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public interface UserMapper {

    /**
     * 新增用户
     * @param user
     */
    void addUser(User user);

    /**
     *
     * @param id
     * @return
     */
    User getUserByID(@Param("id")long id);
}
