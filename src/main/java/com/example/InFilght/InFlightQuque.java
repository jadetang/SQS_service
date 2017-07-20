package com.example.InFilght;

import com.example.model.Message;

import java.util.function.Consumer;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午8:06
 */
public interface InFlightQuque {

    /**
     * put a message to the in flight queue, after the visibility timeout, the consumer's accept will invoke
     * if the message is not deleted from the queue
     * @param queueUrl
     * @param message
     * @param visibilityTimeout
     * @param consumer
     * @throws com.example.exception.OverInFlightLimitException if the in flight queue is full
     */
    void put(String queueUrl,Message message, int visibilityTimeout, Consumer<Message> consumer);


    /**
     * delete the message identified by the message's receipt handle,if the receiptHandle is not
     * valid, the queue will do nothing
     * @param queueUrl
     * @param receiptHandle the message's receipt handle
     */
    void delete(String queueUrl, String receiptHandle);
}
