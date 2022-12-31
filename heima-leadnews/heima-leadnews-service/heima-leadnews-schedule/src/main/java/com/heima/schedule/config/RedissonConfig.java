package com.heima.schedule.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author peelsannaw
 * @create 28/12/2022 16:48
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.200.130:6379")
                .setPassword("leadnews");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
