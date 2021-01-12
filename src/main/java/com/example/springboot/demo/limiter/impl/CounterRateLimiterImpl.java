package com.example.springboot.demo.limiter.impl;

import com.example.springboot.demo.entity.LimitEntity;
import com.example.springboot.demo.enums.LimitResponse;
import com.example.springboot.demo.exception.LimitException;
import com.example.springboot.demo.limiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Optional;

@Slf4j
public class CounterRateLimiterImpl implements RateLimiter {
    @Autowired
    private RedisTemplate redisTemplate;

    private DefaultRedisScript<Long> redisScript;

    public CounterRateLimiterImpl(DefaultRedisScript<Long> redisScript) {
        this.redisScript = redisScript;
    }

    @Override
    public void isAllowed(LimitEntity entity) {
        Object result = null;
        try {
            result = this.redisTemplate.execute(redisScript, entity.getKeys(), entity.getBurstCapacity(), entity.getInterval());
        } catch (Exception e) {
            log.error("Execute counter rate limiting error.", e);
            return;//异常时直接通过，防止影响主业务
        }

        LimitResponse limitResponse = Optional.ofNullable(result).map(o -> (Long) o)
                .map(aLong -> {
                    boolean allowed = aLong > 0L;
                    LimitResponse response = new LimitResponse(allowed, aLong, entity.getKeys(), null);

                    if (log.isDebugEnabled()) {
                        log.debug("response: " + response);
                    }
                    return response;
                }).orElseGet(() -> new LimitResponse(false, entity.getKeys()));

        if (!limitResponse.isAllowed()) {
            throw new LimitException("Type:Counter,you request is not allowed because of exceeding the limit threshold. " + limitResponse.toString());
        }
    }
}
