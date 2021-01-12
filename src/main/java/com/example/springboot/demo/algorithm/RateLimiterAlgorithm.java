package com.example.springboot.demo.algorithm;

import com.example.springboot.demo.entity.LimitEntity;

public interface RateLimiterAlgorithm {
    public void tryAcquire(LimitEntity limitEntity);
}
