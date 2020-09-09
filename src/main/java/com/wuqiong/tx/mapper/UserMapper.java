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
     *
     * @param id
     * @return
     */
    User getUserByID(@Param("id")long id);
}
