package com.example;

import com.example.model.Message;

public class FileQueueService implements QueueService {

    @Override public void pushMessage(String queueName, String messageBody) {

    }

    @Override public Message pullMessage(String queueName) {
        return null;
    }

    @Override public void deleteMessage(String queueName, String receiptHandle) {

    }

    @Override public String createQueue(String queueName, int visibilityTimeout) {
        return null;
    }
    //
  // Task 3: Implement me if you have time.
  //
}
