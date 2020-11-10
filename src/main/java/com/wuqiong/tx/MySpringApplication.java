package com.wuqiong.tx;

import com.wuqiong.tx.config.MyConfiguration;
import com.wuqiong.tx.context.ContextHolder;
import com.wuqiong.tx.entity.Trade;
import com.wuqiong.tx.entity.User;
import com.wuqiong.tx.service.TradeService;
import com.wuqiong.tx.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class MySpringApplication  {

    static AnnotationConfigApplicationContext context;
    static UserService userService;
    static TradeService tradeService;

    static {
        context = new AnnotationConfigApplicationContext();
        context.register(MyConfiguration.class);
        context.refresh();
        userService = context.getBean(UserService.class);
        tradeService = context.getBean(TradeService.class);
    }

    public static void main(String[] args) {
        String companyID = "123";
        ContextHolder.setCompanyID(companyID);
        ContextHolder.setApplicationType(2);
        //addUserTest();
        addTradeTest();
    }

    private static void addUserTest() {
        User user = new User();
        user.setCompanyID("123");
        user.setUsername("2020090906");
        userService.addUser(user);
    }

    private static void addTradeTest() {
        Trade trade = new Trade();
        trade.setBuyerID("buyerID2020091015");
        tradeService.addTrade(trade);
    }
}
