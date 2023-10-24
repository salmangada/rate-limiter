package com.sal.ratelimiter.event;

import org.springframework.context.ApplicationEvent;

public class RateCheckFailureEvent extends ApplicationEvent {

    private final String msg;

    public RateCheckFailureEvent (Object source, String msg) {
        super(source);
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
}
