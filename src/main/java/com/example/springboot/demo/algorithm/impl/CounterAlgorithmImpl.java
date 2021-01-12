package com.example.springboot.demo.algorithm.impl;

import com.example.springboot.demo.algorithm.RateLimiterAlgorithm;
import com.example.springboot.demo.entity.LimitEntity;
import com.example.springboot.demo.limiter.impl.CounterRateLimiterImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("counterAlgorithm")
public class CounterAlgorithmImpl implements RateLimiterAlgorithm {

    @Resource(name = "counterRateLimiter")
    private CounterRateLimiterImpl counterRateLimiter;

    @Override
    public void tryAcquire(LimitEntity limitEntity) {
        counterRateLimiter.isAllowed(limitEntity);
    }
}
