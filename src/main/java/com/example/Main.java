package com.example;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sanguan.tangsicheng on 2017/7/19 下午6:42
 */
public class Main {


    private static AmazonSQSClient sqs;

    public static void createQueue(String name, int timeOut){

        CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);

        Map<String,String> attributes = new HashMap<>();

        attributes.put("VisibilityTimeout",timeOut+"");

        createQueueRequest.withAttributes(attributes);


        CreateQueueResult myQueueUrl = sqs.createQueue(createQueueRequest);

        System.out.println(myQueueUrl);

    }


    public static void sendMesage(String queueName,String mesage){

        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        SendMessageRequest request = new SendMessageRequest();
        request.withQueueUrl(queueUrl);
        request.withMessageBody(mesage);

        SendMessageResult result = sqs.sendMessage(request);

        System.out.println(result);

    }

    public static void deleteMessage(String queueName, String messageReceiptHandler){
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setQueueUrl(queueUrl);
        request.setReceiptHandle(messageReceiptHandler);
        sqs.deleteMessage(request);
    }



    public static void main(String[] args) throws InterruptedException {

        /*
         * The ProfileCredentialsProvider returns your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                                            + "Please make sure that your credentials file is at the correct "
                                            + "location (~/.aws/credentials), and is in valid format.", e);
        }

        sqs = new AmazonSQSClient(credentials);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");


      //  createQueue("1",0);
       // sendMesage("1","");
        deleteMessage("1","jfkafa");

    }

     /*   try {
            // Create a queue
            System.out.println("Creating a new SQS queue called MyQueue.\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");

           // createQueueRequest.setAttributes();


            String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

            // List queues
            System.out.println("Listing all queues in your account.\n");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();

            // Send a message
            System.out.println("Sending a message to MyQueue.\n");
            sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my 1 message text."));
            sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my 2 message text."));

            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            List<Message> messages1 = getMessages(sqs, myQueueUrl);
            List<Message> messages2 = getMessages(sqs, myQueueUrl);

            // Delete a message
            System.out.println("Deleting a message.\n");
            String messageReceiptHandle = messages1.get(0).getReceiptHandle();
            //String messageReceiptHandle = messages2.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));

            // Delete a queue
            System.out.println("Deleting the test queue.\n");
            sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                               "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                               "a serious internal problem while trying to communicate with SQS, such as not " +
                               "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }*/

   /* private static List<Message> getMessages(AmazonSQS sqs, String myQueueUrl) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
        return messages;
    }*/

}
