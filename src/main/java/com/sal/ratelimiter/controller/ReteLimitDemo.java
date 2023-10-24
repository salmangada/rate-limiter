package com.sal.ratelimiter.controller;

import com.sal.ratelimiter.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/rate-limit")
public class ReteLimitDemo {


    @GetMapping("/test")
    @RateLimit(base = "",permits = 1,timeUnit = TimeUnit.MINUTES)
    public String test(){
        log.info("Request Received");
        return "Hello World";
    }
}
