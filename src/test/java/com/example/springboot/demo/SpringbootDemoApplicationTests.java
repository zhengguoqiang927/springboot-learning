package com.example.springboot.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class SpringbootDemoApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(Instant.now().getEpochSecond());
    }

}
