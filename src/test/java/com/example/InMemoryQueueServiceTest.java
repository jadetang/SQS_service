package com.example;

import com.example.exception.QueueDoesNotExistException;
import com.example.exception.QueueNameExistsException;
import com.example.model.Message;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午4:22
 */
public class InMemoryQueueServiceTest {

    int visibilityTimeout = 5;

    @Test
    public void createQueueShowReturnQueueName() throws Exception {
        InMemoryQueueService queueService = new InMemoryQueueService(10);
        String queueName = "test_queue";
        String queueUrl = queueService.createQueue(queueName,visibilityTimeout);
        Assert.assertEquals(queueName,queueUrl);
    }

    @Test(expected = QueueNameExistsException.class)
    public void createQueueWithDifferentTimeoutShouldThrowException(){
        InMemoryQueueService queueService = new InMemoryQueueService(10);
        String queueName = "test_queue";
        queueService.createQueue(queueName,5);
        queueService.createQueue(queueName,10);
    }

    @Test(expected = QueueDoesNotExistException.class)
    public void putMessageToNonExistQueueShouldThrowException(){
        InMemoryQueueService queueService = new InMemoryQueueService(10);
        String messageBody = "this is a message";
        queueService.pushMessage("emptyUrl",messageBody);
    }


    @Test
    public void pullMessageBodyShouldEqualToOriginMessage(){
        InMemoryQueueService queue = new InMemoryQueueService(10);
        String queueName = "testQueue";
        String queueUrl = queue.createQueue(queueName,10);
        String messageBody = "this is a message";
        queue.pushMessage(queueUrl,messageBody);
        Message messageFromQueue = queue.pullMessage(queueUrl);
        Assert.assertEquals(messageBody,messageFromQueue.getBody());

    }



}
