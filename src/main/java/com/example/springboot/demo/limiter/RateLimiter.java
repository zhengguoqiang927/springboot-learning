package com.example.springboot.demo.limiter;

import com.example.springboot.demo.entity.LimitEntity;

public interface RateLimiter {

    void isAllowed(LimitEntity entity);
}
