package com.wuqiong.tx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

}
