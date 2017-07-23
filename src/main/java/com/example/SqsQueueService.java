package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqsQueueService implements QueueService {
    //
    // Task 4: Optionally implement parts of me.
    //
    // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
    // primarily so you can quickly assess your choices for method signatures in QueueService in
    // terms of how well they map to the implementation intended for a production environment.
    //
    private AmazonSQSClient sqsClient;

    public SqsQueueService(AmazonSQSClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    private String getQueueUrl(String queueName) {
        return sqsClient.getQueueUrl(queueName).getQueueUrl();
    }

    @Override
    public void pushMessage(String queueName, String... messages) {
        String queueUrl = getQueueUrl(queueName);
        SendMessageBatchRequest request = new SendMessageBatchRequest();
        request.setQueueUrl(queueUrl);
        List<SendMessageBatchRequestEntry> batchRequestEntryList = Arrays.asList(messages).stream().map(s -> {
            SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry();
            entry.setMessageBody(s);
            return entry;
        }).collect(Collectors.toList());
        request.setEntries(batchRequestEntryList);
        sqsClient.sendMessageBatch(request);
    }

    @Override
    public Message pullMessage(String queueName) {
        String queueUrl = getQueueUrl(queueName);
        ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl);
        request.withMaxNumberOfMessages(1);
        ReceiveMessageResult result = sqsClient.receiveMessage(request);
        List<Message> messages = result.getMessages();
        if (messages == null || messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Override
    public void deleteMessage(String queueName, String receiptHandle) {
        String queueUrl = getQueueUrl(queueName);
        sqsClient.deleteMessage(queueUrl, receiptHandle);
    }

    @Override
    public String createQueue(String queueName, Long visibilityTimeout) {
        CreateQueueRequest request = new CreateQueueRequest();
        request.withQueueName(queueName);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("VisibilityTimeout", String.valueOf(visibilityTimeout));
        request.withAttributes(attributes);
        CreateQueueResult result = sqsClient.createQueue(request);
        return result.getQueueUrl();
    }
}

