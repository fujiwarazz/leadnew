package com.heima.common.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author peelsannaw
 * @create 13/11/2022 下午10:59
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SysParam {
    String name();

    String description() default "";
}
