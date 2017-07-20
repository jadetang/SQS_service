package com.example;

import com.example.model.Message;

public class FileQueueService implements QueueService {

    @Override public void pushMessage(String queueUrl, String messageBody) {

    }

    @Override public Message pullMessage(String queueUrl) {
        return null;
    }

    @Override public void deleteMessage(String queueUrl, String receiptHandle) {

    }

    @Override public String createQueue(String queueName, int visibilityTimeout) {
        return null;
    }
    //
  // Task 3: Implement me if you have time.
  //
}
