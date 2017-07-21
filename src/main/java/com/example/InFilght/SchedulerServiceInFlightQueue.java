package com.example.InFilght;

import com.example.model.Message;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * a in flight queue based on {@link java.util.concurrent.ScheduledExecutorService}, for one message in the
 * queue, using one thread. This implementation is memory consuming, but could guarantee the time out could be accurate
 * not thread safe
 * @author sanguan.tangsicheng on 2017/7/20 下午8:32
 */
public class SchedulerServiceInFlightQueue implements InFlightQueue {


    ScheduledExecutorService scheduledExecutorService;

    Map<String, ScheduledFuture> futureMap;

    public SchedulerServiceInFlightQueue() {
        scheduledExecutorService = Executors.newScheduledThreadPool(5, new ThreadFactoryBuilder().setNameFormat("completable future in flight queue %d").build());
        futureMap = new HashMap<>();
    }

    @Override
    public void put(String queueName, Message message, int visibilityTimeout, Consumer<Message> consumer) {
        Preconditions.checkNotNull(message.getReceiptHandle());
        ScheduledFuture future = scheduledExecutorService.schedule(() -> consumer.accept(message), visibilityTimeout, TimeUnit.SECONDS);
        futureMap.put(message.getReceiptHandle(), future);

    }

    @Override
    public void removeMessage(String queueName, String receiptHandle) {
        ScheduledFuture scheduledFuture = futureMap.get(receiptHandle);
        if (!scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
            futureMap.remove(receiptHandle);
        }
    }


}
