package com.wuqiong.tx.rest;

import com.wuqiong.tx.entity.Trade;
import com.wuqiong.tx.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@RestController
@RequestMapping("/trade")
public class TradeRestServiceImpl {

    @Autowired
    private TradeService tradeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void addTrade(@RequestParam("companyID")String companyID,
                         @RequestParam(name = "transactionID", required = false) String transactionID, @RequestBody Trade trade) {
        System.out.println("add trade");
        tradeService.addTrade(trade);
    }

}
