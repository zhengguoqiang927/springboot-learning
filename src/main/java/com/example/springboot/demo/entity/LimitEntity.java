package com.example.springboot.demo.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class LimitEntity {
    private List<String> keys;

    //令牌桶填充速率
    private int replenishRate = 1;

    //令牌桶上限 / 计数器限流次数
    private int burstCapacity = 1;

    //每次请求获取的令牌数量
    private int requestedTokens = 1;

    //计数器时间窗口
    private int interval = 1;
}
