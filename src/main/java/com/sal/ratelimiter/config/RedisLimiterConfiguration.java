package com.sal.ratelimiter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisLimiterConfiguration {

    private final RedisLimiterProperties redisLimiterProperties;

    public RedisLimiterConfiguration(RedisLimiterProperties redisLimiterProperties) {
        this.redisLimiterProperties = redisLimiterProperties;
    }

    @Bean
    @ConditionalOnMissingBean(JedisPool.class)
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(redisLimiterProperties.getRedisPoolMaxWaitMillis());
        jedisPoolConfig.setMaxTotal(redisLimiterProperties.getRedisPoolMaxTotal());
        return new JedisPool(jedisPoolConfig,
                redisLimiterProperties.getRedisHost(),
                redisLimiterProperties.getRedisPort(),
                redisLimiterProperties.getRedisConnectionTimeout(),
                redisLimiterProperties.getRedisPassword(),
                0);
    }
}
