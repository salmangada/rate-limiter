package com.sal.ratelimiter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.redis-limiter")
@Data
@Component
public class RedisLimiterProperties {

    /**
     * Redis server host
     */
    private String redisHost = "127.0.0.1";

    /**
     * Redis service port
     */
    private int redisPort = 6379;

    private String redisPassword = null;
    /**
     * Redis connection timeout
     */
    private int redisConnectionTimeout = 2000;
    /**
     * max idle connections in the pool
     */
    private int redisPoolMaxIdle = 50;
    /**
     * min idle connection in the pool
     */
    private int redisPoolMinIdle = 10;
    /**
     * the max wait milliseconds for borrowing an instance from the pool
     */
    private long redisPoolMaxWaitMillis = -1;
    /**
     * the max total instances in the pool
     */
    private int redisPoolMaxTotal = 200;
}
