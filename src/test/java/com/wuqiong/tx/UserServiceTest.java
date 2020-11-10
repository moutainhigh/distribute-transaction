package com.wuqiong.tx;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MySpringBootApplication.class)
public class UserServiceTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getUserTest() {
//        redisTemplate.opsForValue().set("userID", "1");
        String userID = (String) redisTemplate.opsForValue().get("userID");
        System.out.println("userID:" + userID);
    }

    @Test
    public void hashTest() {
        Map<String,String> tx = new HashMap<>();
        tx.put("transactionID", "123");
        redisTemplate.opsForHash().putAll("tx",tx);
        Map result = redisTemplate.opsForHash().entries("tx");
        System.out.println(new Gson().toJson(result));
    }
}
