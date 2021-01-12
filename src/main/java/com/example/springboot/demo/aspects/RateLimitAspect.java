package com.example.springboot.demo.aspects;

import com.example.springboot.demo.algorithm.impl.CounterAlgorithmImpl;
import com.example.springboot.demo.algorithm.impl.TokenBucketAlgorithmImpl;
import com.example.springboot.demo.annotations.SydcRateLimit;
import com.example.springboot.demo.entity.LimitEntity;
import com.example.springboot.demo.enums.AlgorithmType;
import com.example.springboot.demo.utils.RateLimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Resource(name = "tokenBucketAlgorithm")
    private TokenBucketAlgorithmImpl tokenBucketAlgorithm;

    @Resource(name = "counterAlgorithm")
    private CounterAlgorithmImpl counterAlgorithm;

    @Pointcut("@annotation(sydcRateLimit)")
    private void check(SydcRateLimit sydcRateLimit){

    }

    @Before(value = "check(sydcRateLimit)", argNames = "joinPoint,sydcRateLimit")
    public void before(JoinPoint joinPoint, SydcRateLimit sydcRateLimit){
        List<String> rateKeys = RateLimiterUtil.getRateKeys(joinPoint, sydcRateLimit.limitType());
        if (rateKeys == null){
            log.warn("");
            return;
        }
        LimitEntity entity = LimitEntity.builder()
                .keys(rateKeys)
                .burstCapacity(sydcRateLimit.limit())
                .replenishRate(sydcRateLimit.rate())
                .requestedTokens(1)
                .interval(sydcRateLimit.interval())
                .build();
        if (sydcRateLimit.algorithm() == AlgorithmType.COUNTER){
            counterAlgorithm.tryAcquire(entity);
        }else if (sydcRateLimit.algorithm() == AlgorithmType.TOKEN_BUCKET){
            tokenBucketAlgorithm.tryAcquire(entity);
        }
    }
}
