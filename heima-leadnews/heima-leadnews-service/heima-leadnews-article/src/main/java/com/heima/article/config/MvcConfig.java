package com.heima.article.config;

import com.heima.article.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 31/12/2022 22:07
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/login/login_auth");
    }
}
