package com.wuqiong.tx.rest;

import com.wuqiong.tx.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@FeignClient("WUQIONG-USER-SERVICE")
public interface UserRestService {

    @RequestMapping(path = "/user/add", method = RequestMethod.POST)
    void addUser(@RequestParam("companyID")String companyID, @RequestParam("transactionID") String transactionID, @RequestBody User user);
}
