package com.sal.ratelimiter.core;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisRateLimiter {

    private JedisPool jedisPool;

    private TimeUnit timeUnit;


    private static final String LUA_SCRIPT = """
            local current = redis.call('incr', KEYS[1])
            if tonumber(current) == 1 then
                redis.call('expire', KEYS[1], ARGV[1])
            end
                        
            -- Return 1 if permits are allowed, otherwise return -1
            if tonumber(current) <= tonumber(ARGV[2]) then
                return 1
            else
                return -1
            end
            """;

    public RedisRateLimiter(JedisPool jedisPool, TimeUnit timeUnit) {
        this.jedisPool = jedisPool;
        this.timeUnit = timeUnit;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public boolean acquire(String keyPrefix, int permitsPerUnit) {
        log.info("Limiter Acquired");
        boolean res = false;
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                log.info("Key ::::: " + keyPrefix);
                long result = (long) jedis.eval(LUA_SCRIPT, 1, keyPrefix, String.valueOf(timeUnit.toSeconds(1)), String.valueOf(permitsPerUnit));
                log.info("Result ::::: " + result);
                return result > 0;
            }
        }
        return res;
    }
}
