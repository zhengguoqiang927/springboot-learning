package com.example.springboot.demo.limiter.impl;

import com.example.springboot.demo.entity.LimitEntity;
import com.example.springboot.demo.enums.LimitResponse;
import com.example.springboot.demo.exception.LimitException;
import com.example.springboot.demo.limiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Instant;
import java.util.*;

@Slf4j
public class BucketRateLimiterImpl implements RateLimiter {

    /**
     * Replenish Rate Limit header name.
     */
    public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";

    /**
     * Burst Capacity header name.
     */
    public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

    /**
     * Requested Tokens header name.
     */
    public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";

    @Autowired
    private RedisTemplate redisTemplate;

    private DefaultRedisScript<List> redisScript;

    public BucketRateLimiterImpl(DefaultRedisScript<List> redisScript) {
        this.redisScript = redisScript;
    }

    @Override
    public void isAllowed(LimitEntity entity) {
        Object result = null;
        try {
            result = this.redisTemplate.execute(redisScript, entity.getKeys(), entity.getReplenishRate(),
                    entity.getBurstCapacity(), Instant.now().getEpochSecond(), entity.getRequestedTokens());
        } catch (Exception e) {
            log.error("Execute redis rate limiting error.",e);
            return;//异常时直接通过，防止影响主业务
        }
        LimitResponse limitResponse = Optional.ofNullable(result)
                .map(o -> (List) o)
                .map(longs -> {
                    boolean allowed = (Long)longs.get(0) == 1L;
                    Long tokensLeft = (Long) longs.get(1);
                    LimitResponse response = new LimitResponse(allowed, tokensLeft, entity.getKeys(), getHeaders(entity));

                    if (log.isDebugEnabled()) {
                        log.debug("response: " + response);
                    }

                    return response;
                }).orElseGet(() -> new LimitResponse(false, entity.getKeys()));

        if (!limitResponse.isAllowed()) {
            throw new LimitException("Type:TOKEN_BUCKET,you request is not allowed because of exceeding the limit threshold. " + limitResponse.toString());
        }
    }

    public Map<String, String> getHeaders(LimitEntity entity) {
        Map<String, String> headers = new HashMap<>();
        headers.put(REPLENISH_RATE_HEADER, String.valueOf(entity.getReplenishRate()));
        headers.put(BURST_CAPACITY_HEADER, String.valueOf(entity.getBurstCapacity()));
        headers.put(REQUESTED_TOKENS_HEADER, String.valueOf(entity.getRequestedTokens()));
        return headers;
    }
}
