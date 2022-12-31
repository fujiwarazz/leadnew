package com.heima.wemedia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


/**
 * @author peelsannaw
 */
@EnableAsync
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients("com.heima.apis")
@MapperScan("com.heima.wemedia.mapper")
@ComponentScan("com.heima.apis.article.fallback")
@ComponentScan("com.heima.wemedia.service")
public class WemediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class,args);
    }
}
