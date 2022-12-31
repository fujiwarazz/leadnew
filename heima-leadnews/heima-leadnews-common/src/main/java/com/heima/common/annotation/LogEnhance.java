package com.heima.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author peelsannaw
 * @create 13/11/2022 下午11:13
 */
@Target(ElementType.METHOD)
//Annotations are to be recorded in the class file by the compiler and retained by the VM at run time, so they may be read reflectively.
//See Also:
//reflect.AnnotatedElement
@Retention(RetentionPolicy.RUNTIME)
public @interface LogEnhance {
    String  BusinessName();
}
