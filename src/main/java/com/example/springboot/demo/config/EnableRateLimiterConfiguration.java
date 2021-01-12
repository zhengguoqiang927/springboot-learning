package com.example.springboot.demo.config;

import com.example.springboot.demo.limiter.RateLimiter;
import com.example.springboot.demo.limiter.impl.BucketRateLimiterImpl;
import com.example.springboot.demo.limiter.impl.CounterRateLimiterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

@Slf4j
@Configuration
@ComponentScan("com.example.springboot.demo")
public class EnableRateLimiterConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public BucketRateLimiterImpl bucketRateLimiter(){
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/bucket_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return new BucketRateLimiterImpl(redisScript);
    }

    @Bean
    public CounterRateLimiterImpl counterRateLimiter(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/counter_rate_limiter.lua")));
        redisScript.setResultType(Long.class);
        return new CounterRateLimiterImpl(redisScript);
    }
}
