package com.sal.ratelimiter.core;

import com.sal.ratelimiter.event.RateCheckFailureEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class RateLimiterTaskRunner implements ApplicationContextAware {

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Autowired
    private RedisRateLimiterFactory redisRateLimiterFactory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public boolean run(String key, TimeUnit timeUnit, int permits){

        CheckTask task = new CheckTask(key, timeUnit, permits);
        Future<Boolean> checkResult = executorService.submit(task);
        boolean retVal = true;
        try {
            retVal = checkResult.get(100, TimeUnit.MILLISECONDS);
        }
        catch(Exception e) {
            applicationContext.publishEvent(new RateCheckFailureEvent(e, "Access rate check task execution failed."));
        }
        return retVal;
    }

    class CheckTask implements Callable<Boolean> {
        private final String rateLimiterKey;
        private final TimeUnit timeUnit;
        private final int permits;
        CheckTask(String rateLimiterKey, TimeUnit timeUnit, int permits) {
            this.rateLimiterKey = rateLimiterKey;
            this.timeUnit = timeUnit;
            this.permits = permits;
        }
        public Boolean call() {
            RedisRateLimiter redisRatelimiter = redisRateLimiterFactory.get(timeUnit);
            return redisRatelimiter.acquire(rateLimiterKey, permits);
        }
    }
}
