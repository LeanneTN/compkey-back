package com.example.compkeyback.annotation;

import java.lang.annotation.*;

/**
 * ip限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpLimiter {

    /**
     * 限流ip
     */
    String ipAdress() ;
    /**
     * 单位时间限制通过请求数
     */
    long limit() default 10;

    /**
     * 单位时间，单位秒
     */
    long time() default 1;

    /**
     * 达到限流提示语
     */
    String message();
}
