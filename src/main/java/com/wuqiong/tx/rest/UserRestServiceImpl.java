package com.wuqiong.tx.rest;

import com.wuqiong.tx.entity.User;
import com.wuqiong.tx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@RestController
@RequestMapping("/user")
public class UserRestServiceImpl {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void addUser(@RequestParam("companyID")String companyID, @RequestParam("transactionID") String transactionID, @RequestBody User user) {
        System.out.println("add user");
        userService.addUser(user);
    }
}
