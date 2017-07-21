package com.example.InFilght;

import com.example.model.Message;

import java.util.function.Consumer;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午8:06
 */
public interface InFlightQueue {

    /**
     * put a message to the in flight queue, after the visibility timeout, the consumer's accept will invoke
     * if the message is not deleted from the queue
     * @param queueName
     * @param message
     * @param visibilityTimeout
     * @param consumer
     */
    void put(String queueName,Message message, int visibilityTimeout, Consumer<Message> consumer);


    /**
     * remove the message out of the in flight queue, identified by the message's receipt handle,
     * if the receiptHandle is not valid, nothing happens
     * @param queueName
     * @param receiptHandle the message's receipt handle
     */
    void removeMessage(String queueName, String receiptHandle);
}
