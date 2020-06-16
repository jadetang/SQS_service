package github.jadetang.sqs;

import java.util.concurrent.TimeUnit;

/**
 * a clock that could move time forward and back, used for testing.
 * not thread safe
 *
 * @author sanguan.tangsicheng on 2017/7/21 下午4:15
 */
public class TimeMachine extends Clock {

    long currentTime = 0L;

    public Long now() {
        return currentTime;
    }

    /**
     * move the 'time' forward
     */
    public TimeMachine moveForward(long duration, TimeUnit unit) {
        currentTime += unit.toMillis(duration);
        return this;
    }

    /**
     * move the 'time' back
     */
    public TimeMachine moveBack(long duration, TimeUnit unit) {
        currentTime -= unit.toMillis(duration);
        return this;
    }

    public TimeMachine reset() {
        currentTime = 0L;
        return this;
    }

}
