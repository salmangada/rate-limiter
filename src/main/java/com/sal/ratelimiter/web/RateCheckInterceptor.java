package com.sal.ratelimiter.web;

import com.sal.ratelimiter.annotation.RateLimit;
import com.sal.ratelimiter.core.RateLimiterTaskRunner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class RateCheckInterceptor implements HandlerInterceptor, ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private RateLimiterTaskRunner rateCheckTaskRunner;

    private String applicationName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (applicationName == null) {
            throw new BeanInitializationException("the property with key 'spring.application.name' must be set!");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        boolean isSuccess = true;
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(RateLimit.class)) {
            isSuccess = handle(method, request, response);
        }

        if (!isSuccess){
            buildDenyResponse(response);
        }

        return isSuccess;
    }

    private boolean handle(Method method, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);
        int permits = rateLimitAnnotation.permits();
        TimeUnit timeUnit = rateLimitAnnotation.timeUnit();
        String path = rateLimitAnnotation.path();
        if ("".equals(path)) {
            path = request.getRequestURI();
        }
        String baseVal = request.getHeader("userId");
        if (Objects.isNull(baseVal))
            baseVal = "";
        String rateLimiterKey =  applicationName + ":" + path + ":" + baseVal;
        return rateCheckTaskRunner.run(rateLimiterKey,timeUnit,permits);
    }

    private void buildDenyResponse(HttpServletResponse response) throws Exception{
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().print("Access denied because of exceeding access rate");
    }
}
