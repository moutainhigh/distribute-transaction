package com.wuqiong.tx.service;

import com.wuqiong.tx.context.ContextHolder;
import com.wuqiong.tx.entity.Trade;
import com.wuqiong.tx.entity.User;
import com.wuqiong.tx.mapper.TradeMapper;
import com.wuqiong.tx.rest.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserRestService userRestService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    @Override
    public void addTrade(Trade trade) {
        try {
            String companyID = ContextHolder.getCompanyID();
            User user = new User();
            user.setCompanyID(companyID);
            user.setUsername(trade.getBuyerID());
            String transactionID = ContextHolder.getLocalTransactionID();
            userRestService.addUser(companyID, transactionID, user);
            int a = 1;
            int b = 0;
            int c = a/b;
            trade.setCompanyID(companyID);
            tradeMapper.addTrade(trade);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存订单出错");
            throw new RuntimeException("保存订单出错");
        }

    }
}
