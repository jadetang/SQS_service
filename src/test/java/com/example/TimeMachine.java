package com.example;

import java.util.concurrent.TimeUnit;

/**
 * a clock that could move time forward and back, used for testing
 * not thread safe
 *
 * @author sanguan.tangsicheng on 2017/7/21 下午4:15
 */
public class TimeMachine extends Clock {

    long offset;

    public Long now() {
        return System.currentTimeMillis() + offset;
    }

    /**
     * move the 'time' forward
     */
    public TimeMachine moveForward(long duration, TimeUnit unit) {
        offset += unit.toMillis(duration);
        return this;
    }

    /**
     * move the 'time' back
     */
    public TimeMachine moveBack(long duration, TimeUnit unit) {
        offset -= unit.toMillis(duration);
        return this;
    }

    public TimeMachine reset() {
        offset = 0L;
        return this;
    }
}
