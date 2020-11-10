package com.wuqiong.tx.rest;

import com.wuqiong.tx.entity.Trade;
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
@FeignClient("WUQIONG-TRADE-SERVICE")
public interface TradeRestService {

    @RequestMapping(path = "/trade/add", method = RequestMethod.POST)
    void addTrade(@RequestParam("companyID")String companyID, @RequestParam("transactionID") String transactionID, @RequestBody Trade trade);
}
