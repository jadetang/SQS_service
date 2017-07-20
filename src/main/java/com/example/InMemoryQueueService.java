package com.example;

import com.example.exception.QueueDoesNotExistException;
import com.example.exception.QueueNameExistsException;
import com.example.model.Message;
import com.google.common.base.Preconditions;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A in memory queue service, for each in flight message using a thread to monitor its visibility.
 */
public class InMemoryQueueService implements QueueService {

    //this is the total limit of all queue, not for a single queue
    private int maxInFlightMessageNumber;

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> queueMap;

    private ConcurrentHashMap<String, Integer> timeoutConfig;

    /**
     * construct a in memory queue service
     * @param maxInFlightMessageNumber the max number of in flight message of all queue
     */
    public InMemoryQueueService(int maxInFlightMessageNumber) {
        Preconditions.checkArgument(maxInFlightMessageNumber < 100 && maxInFlightMessageNumber > 0);
        this.maxInFlightMessageNumber = maxInFlightMessageNumber;
        this.timeoutConfig = new ConcurrentHashMap<>();
        this.queueMap = new ConcurrentHashMap<>();
    }

    @Override
    public void pushMessage(String queueUrl, String messageBody) {
        validateQueueUrl(queueUrl);
        queueMap.get(queueUrl).offer(new Message(UUID.randomUUID().toString(), messageBody));
    }

    @Override
    public Message pullMessage(String queueUrl) {
        validateQueueUrl(queueUrl);
        Message message = queueMap.get(queueUrl).poll();
        if (message != null) {
            message.setReceiptHandle(message.getMessageId());
        }
        return message;
    }

    private void validateQueueUrl(String queueUrl) {
        if (!queueMap.containsKey(queueUrl)) {
            throw new QueueDoesNotExistException(queueUrl);
        }
    }

    @Override
    public void deleteMessage(String queueUrl, String receiptHandle) {

    }

    /**
     * @return the name of the queue as its url since this is in memory queue service
     */
    @Override
    public synchronized String createQueue(String queueName, int visibilityTimeout) {
        Preconditions.checkArgument(queueName != null && queueName.length() != 0);
        Preconditions.checkArgument(visibilityTimeout >= 0);
        if (timeoutConfig.get(queueName) != null && timeoutConfig.get(queueName) != visibilityTimeout) {
            throw new QueueNameExistsException(String.format("A queue already exists with the same name[%s] and a different value for attribute VisibilityTimeout", queueName));
        } else {
            timeoutConfig.put(queueName, visibilityTimeout);
            queueMap.putIfAbsent(queueName, new ConcurrentLinkedQueue<>());
            return queueName;
        }
    }
}
