package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.example.model.Message;

public class SqsQueueService implements QueueService {
  //
  // Task 4: Optionally implement parts of me.
  //
  // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
  // primarily so you can quickly assess your choices for method signatures in QueueService in
  // terms of how well they map to the implementation intended for a production environment.
  //

  public SqsQueueService(AmazonSQSClient sqsClient) {
  }

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
}
