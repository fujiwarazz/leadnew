package com.heima.common.aop;

import com.alibaba.fastjson.JSON;
import com.heima.common.annotation.LogEnhance;

import com.heima.common.exception.CustomException;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
* @Author peelsannaw
* @create 13/11/2022 下午11:13
*/

@SuppressWarnings("all")
@Aspect
@Component
@Slf4j
public class logAspect {


    //pointCut 指定切入点 被切入的点叫做jointPoint jointPoint可以获取这个切入点和相关信息 如同名字，方法等
    @Pointcut("@annotation(com.heima.common.annotation.LogEnhance)")
    public void pt(){
    }

    /**
     * 增强
     * @param ProceedingJoinPoint 为被增强方法的封装对象
     * ProceedingJointPoint 为JointPoint的继承类 它可以使用Around 进行环绕
     */
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint point){

        Object proceed = null;
        try {
            beforeHandler(point);
            proceed = point.proceed();
            afterHandler(proceed);
        }catch (Throwable e){
            throw new RuntimeException();
        }finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }
        return proceed;
    }

    public void beforeHandler(ProceedingJoinPoint point){

        //RequestContextHolder线程池:
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //获取被增强方法的名字
        LogEnhance logEnhance = getLogEnhance(point);

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}",request.getRequestURI());
        // 打印描述信息
        log.info("BusinessName   : {}",logEnhance.BusinessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}",point.getSignature().getDeclaringType(),
                point.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}",request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(point.getArgs()));

    }

    private LogEnhance getLogEnhance(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        return signature.getMethod().getAnnotation(LogEnhance.class);
    }

    public void afterHandler(Object proceed){

        log.info("Response       : {}",JSON.toJSONString(proceed));
    }
}
