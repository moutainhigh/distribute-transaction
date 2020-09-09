package com.wuqiong.tx;

import com.google.gson.Gson;
import com.wuqiong.tx.config.MyConfiguration;
import com.wuqiong.tx.context.ContextHolder;
import com.wuqiong.tx.service.UserService;
import com.wuqiong.tx.service.UserServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class MyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(MyConfiguration.class);
        context.refresh();
        UserService userService = context.getBean("userServiceImpl",UserServiceImpl.class);
        String companyID = "123";
        ContextHolder.setCompanyID(companyID);
        ContextHolder.setApplicationType(2);
        System.out.println(new Gson().toJson(userService.getUserByID(companyID, 4)));
    }
}
