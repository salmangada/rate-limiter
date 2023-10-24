package com.sal.ratelimiter.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisRateLimiterFactory {

    @Autowired
    private final JedisPool jedisPool;

    private Cache<TimeUnit, RedisRateLimiter> redisRateLimiterCache =
            Caffeine.newBuilder().maximumSize(10).build();

    public RedisRateLimiter get(TimeUnit timeUnit) {
        RedisRateLimiter redisRateLimiter = redisRateLimiterCache.getIfPresent(timeUnit);
        if(redisRateLimiter == null) {
            synchronized (RedisRateLimiterFactory.class) {
                redisRateLimiter = redisRateLimiterCache.getIfPresent(timeUnit);
                if(redisRateLimiter == null) {
                    redisRateLimiter = new RedisRateLimiter(jedisPool, timeUnit);
                    redisRateLimiterCache.put(timeUnit, redisRateLimiter);
                }
            }
        }
        return redisRateLimiter;
    }
}
