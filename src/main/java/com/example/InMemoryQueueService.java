package com.example;

import com.example.InFilght.InFlightQueue;
import com.example.exception.QueueDoesNotExistException;
import com.example.exception.QueueNameExistsException;
import com.example.model.Message;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * A in memory queue service, for each in flight message using a thread to monitor its visibility.
 */
public class InMemoryQueueService implements QueueService {

    private HashMap<String, LinkedList<Message>> queueMap;

    private HashMap<String, Integer> timeoutConfig;

    private InFlightQueue inFlightQueue;

    /**
     * construct a in memory queue service
     */
    public InMemoryQueueService(InFlightQueue inFlightQueue) {
        this.inFlightQueue = inFlightQueue;
        this.timeoutConfig = new HashMap<>();
        this.queueMap = new HashMap<>();
    }

    @Override
    public synchronized void pushMessage(String queueName, String messageBody) {
        validateQueueUrl(queueName);
        queueMap.get(queueName).offer(new Message(UUID.randomUUID().toString(), messageBody));
    }

    @Override
    public synchronized Message pullMessage(String queueName) {
        validateQueueUrl(queueName);
        Message message = queueMap.get(queueName).poll();
        if (message != null) {
            int visibilityTimeout = timeoutConfig.get(queueName);
            if (visibilityTimeout == 0) {
                queueMap.get(queueName).addFirst(message);
            } else {
                message.setReceiptHandle(message.getMessageId());
                inFlightQueue.put(queueName, message, timeoutConfig.get(queueName),
                        message1 -> queueMap.get(queueName).addFirst(message1));
            }
        }
        return message;
    }

    private void validateQueueUrl(String queueUrl) {
        if (!queueMap.containsKey(queueUrl)) {
            throw new QueueDoesNotExistException(queueUrl);
        }
    }

    @Override
    public synchronized void deleteMessage(String queueName, String receiptHandle) {
        inFlightQueue.removeMessage(queueName, receiptHandle);
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
            queueMap.putIfAbsent(queueName, new LinkedList<>());
            return queueName;
        }
    }
}
