package com.heima.common.aop;

import com.alibaba.fastjson.JSON;
import com.heima.common.annotation.Prevent;
import com.heima.common.annotation.PreventStrategy;
import com.heima.common.exception.CustomException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Author peelsannaw
 * @create 19/12/2022 下午4:35
 */
@Aspect
@Component
@Slf4j

public class PreventAop {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.heima.common.annotation.Prevent)")
    public void pt() {

    }

    @Before("pt()")
    public void joinPoint(JoinPoint joinPoint) throws Exception {
        //获取请求
        String reqStr = JSON.toJSONString(joinPoint.getArgs()[0]);
        if (reqStr.length() == 0) {
            throw new RuntimeException("[防刷]入参不为空!");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());

        Prevent preventAnno = method.getAnnotation(Prevent.class);
        String methodFullName = method.getDeclaringClass().getName() + method.getName();

        entrance(preventAnno, reqStr, methodFullName);

    }

    @SuppressWarnings("all")
    private void entrance(Prevent preventAnno, String reqStr, String methodFullName) {
        PreventStrategy strategy = preventAnno.strategy();
        switch (strategy) {
            case DEFAULT:
                defaultHandle(reqStr, preventAnno, methodFullName);
                break;
            default:
                log.info("[invalid strategy]");
                throw new RuntimeException("invalid strategy");
        }
    }

    private void defaultHandle(String reqStr, Prevent preventAnno, String methodFullName) {
        String  base64Str= Base64.getEncoder().encodeToString(reqStr.getBytes(StandardCharsets.UTF_8));
        long expire = Long.parseLong(preventAnno.value());

        String resp = stringRedisTemplate.opsForValue().get(methodFullName + base64Str);
        if (resp == null || resp.length() == 0) {
            stringRedisTemplate.opsForValue().set(methodFullName +base64Str, reqStr, expire);
        } else {
            String msg = preventAnno.message() == null || preventAnno.message().length() == 0
                    ? expire + "秒内不允许重复请求!" : preventAnno.message();
            throw new CustomException(msg);
        }
    }

    public static void main(String[] args) {
        String s = "123456";
        String encodeToString = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        System.out.println(encodeToString);
        System.out.println(new String(Base64.getDecoder().decode(encodeToString.getBytes(StandardCharsets.UTF_8))));
    }
}
