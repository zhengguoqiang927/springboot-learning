package com.example.springboot.demo.algorithm.impl;

import com.example.springboot.demo.algorithm.RateLimiterAlgorithm;
import com.example.springboot.demo.entity.LimitEntity;
import com.example.springboot.demo.limiter.impl.BucketRateLimiterImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("tokenBucketAlgorithm")
public class TokenBucketAlgorithmImpl implements RateLimiterAlgorithm {

    @Resource(name = "bucketRateLimiter")
    private BucketRateLimiterImpl bucketRateLimiter;

    @Override
    public void tryAcquire(LimitEntity limitEntity) {
        bucketRateLimiter.isAllowed(limitEntity);
    }
}
