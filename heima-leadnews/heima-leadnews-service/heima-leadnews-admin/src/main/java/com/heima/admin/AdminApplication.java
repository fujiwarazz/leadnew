package com.heima.admin;

import com.heima.admin.config.FeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author peelsannaw
 * @create 01/01/2023 13:33
 */
@SpringBootApplication
@EnableFeignClients("com.heima.apis")
@FeignClient(configuration = FeignConfig.class)
@EnableDiscoveryClient
@Slf4j
@EnableSwagger2
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
