package com.example;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import com.example.model.Record;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A in memory queue service, for each in flight message using a thread to monitor its visibility.
 */
public class InMemoryQueueService implements QueueService {

    private Map<String, LinkedList<Record>> queueMap;

    private Map<String, Long> visibilityTimeoutMap;

    private Clock clock;

    /**
     * construct a in memory queue service
     */
    public InMemoryQueueService() {
        this.visibilityTimeoutMap = new HashMap<>();
        this.queueMap = new HashMap<>();
        this.clock = new Clock();
    }

    @Override
    public synchronized void pushMessage(String queueName, String... messages) {
        validateQueueName(queueName);
        long visibleFrom = clock.now();
        for (String message : messages) {
            queueMap.get(queueName).offer(Record.create(visibleFrom, message));
        }
    }

    @Override
    public synchronized Message pullMessage(String queueName) {
        validateQueueName(queueName);
        LinkedList<Record> queue = queueMap.get(queueName);
        Long now = clock.now();
        Long visibilityTimeout = visibilityTimeoutMap.get(queueName);
        for (Record record : queue) {
            Long oldVisibleFrom = record.getVisibleFrom();
            if (oldVisibleFrom <= now) {
                String receiptHandle = UUID.randomUUID().toString();
                record.setReceiptHandle(receiptHandle);
                //reset the visible from time
                record.setVisibleFrom(now + visibilityTimeout);
                Message message = new Message();
                message.withMessageId(UUID.randomUUID().toString()).withReceiptHandle(receiptHandle).withBody(record.getMessageBody());
                return message;
            }
        }
        return null;
    }

    private void validateQueueName(String queueName) {
        if (!queueMap.containsKey(queueName)) {
            throw new QueueDoesNotExistException(queueName);
        }
    }

    @Override
    public synchronized void deleteMessage(String queueName, String receiptHandle) {
        validateQueueName(queueName);
        Iterator<Record> it = queueMap.get(queueName).iterator();
        while (it.hasNext()) {
            Record r = it.next();
            if (r.getReceiptHandle() != null && r.getReceiptHandle().equals(receiptHandle)) {
                it.remove();
                break;
            }
        }
    }

    /**
     * @return the name of the queue as its url since this is in memory queue service
     */
    @Override
    public synchronized String createQueue(String queueName, Long visibilityTimeout) {
        Preconditions.checkArgument(queueName != null && queueName.length() != 0);
        Preconditions.checkArgument(visibilityTimeout >= 0);
        if (visibilityTimeoutMap.get(queueName) != null && !visibilityTimeoutMap.get(queueName).equals(visibilityTimeout)) {
            throw new QueueNameExistsException(String.format("A queue already exists with the same name[%s] and a different value for attribute VisibilityTimeout", queueName));
        } else {
            visibilityTimeoutMap.put(queueName, TimeUnit.SECONDS.toMillis(visibilityTimeout));
            queueMap.putIfAbsent(queueName, new LinkedList<>());
            return queueName;
        }
    }

    @VisibleForTesting
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
