package com.example.springboot.demo.annotations;

import com.example.springboot.demo.enums.AlgorithmType;
import com.example.springboot.demo.enums.LimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SydcRateLimit {

    /**
     * token_bucket:令牌桶
     * counter:计数器
     *
     * @return 限流算法
     */
    AlgorithmType algorithm() default AlgorithmType.COUNTER;

    /**
     * @return LimitType 限流类型. 默认值:ALL. 可选值:IP/USER/URI/CUSTOMER
     */
    LimitType limitType() default LimitType.CUSTOMER;

    /**
     * 计数器算法：限流次数
     * 令牌桶算法：最大突发流量
     *
     * @return 限流次数. 单位:秒
     */
    int limit() default 10;

    /**
     * 计数器算法需要
     *
     * @return 时间窗口. 单位:秒
     */
    int interval() default 60;

    /**
     * 令牌桶算法时需要
     *
     * @return 令牌桶填充速率. 单位:秒
     */
    int rate() default 10;
}
