package com.example.InFilght;

import com.example.model.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** a in flight queue based on {@link java.util.concurrent.CompletableFuture}, for one message in the
 * queue, using one thread as a timer. This implementation is memory consuming, but could guarantee
 * the time out could be accurate
 * @author sanguan.tangsicheng on 2017/7/20 下午8:32
 */
public class CompletableFutureInFlightQueue implements InFlightQuque {

    ExecutorService service;

    public CompletableFutureInFlightQueue(int queueSize){
        service = Executors.newFixedThreadPool(queueSize);
    }

    @Override
    public void put(String queueUrl, Message message, int visibilityTimeout, Consumer<Message> consumer) {

    }

    @Override
    public void delete(String queueUrl, String receiptHandle) {

    }
}
