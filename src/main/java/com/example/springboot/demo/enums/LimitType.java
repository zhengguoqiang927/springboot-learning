package com.example.springboot.demo.enums;

public enum  LimitType {
    /**
     * 默认按照方法名+参数列表限流
     */
    ALL,
    /**
     * 根据IP限流
     */
    IP,
    /**
     * 根据用户限流
     */
    USER,
    /**
     * 根据URI限流
     */
    URI,
    /**
     * 自定义限流key
     */
    CUSTOMER;
}
