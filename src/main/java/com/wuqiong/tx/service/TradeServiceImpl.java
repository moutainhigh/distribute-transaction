package com.wuqiong.tx.service;

import com.wuqiong.tx.context.ContextHolder;
import com.wuqiong.tx.entity.Trade;
import com.wuqiong.tx.entity.User;
import com.wuqiong.tx.mapper.TradeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/10
 */
@Service
public class TradeServiceImpl implements TradeService {

    @Resource
    private TradeMapper tradeMapper;
    @Resource
    private UserService userService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    @Override
    public void addTrade(Trade trade) {
        String companyID = ContextHolder.getCompanyID();
        User user = new User();
        user.setCompanyID(companyID);
        user.setUsername(trade.getBuyerID());
        userService.addUser(user);
//        int a = 1;
//        int b = 0;
//        int c = a/b;
        trade.setCompanyID(companyID);
        tradeMapper.addTrade(trade);
    }
}
