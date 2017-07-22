package com.example;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import org.junit.Assert;
import org.junit.Test;

 /**
 * @author sanguan.tangsicheng on 2017/7/20 下午11:27
 */
public abstract class BaseTest {

    String queueName = "test queue";

    String messageBody = "this is a message";

    @Test
    public void pullMessageBodyShouldEqualToOriginMessage(){
        QueueService queue = getQueueService();
        queue.createQueue(queueName,15L);
        queue.pushMessage(queueName,messageBody);
        Message messageFromQueue = queue.pullMessage(queueName);
        Assert.assertEquals(messageBody,messageFromQueue.getBody());
    }



    @Test(expected = QueueNameExistsException.class)
    public void createQueueWithDifferentTimeoutShouldThrowException(){
        QueueService queueService = getQueueService();
        queueService.createQueue(queueName,5L);
        queueService.createQueue(queueName,10L);
    }



    @Test(expected = QueueDoesNotExistException.class)
    public void putMessageToNonExistQueueShouldThrowException(){
        QueueService queueService = getQueueService();
        queueService.pushMessage("emptyUrl",messageBody);
    }



/*    @Test
    public void afterDeletedShouldReturnNull(){
        QueueService queueService = getQueueServiceIrrelevantToVisibilityTimeout();
        queueService.createQueue(queueName,10);
        queueService.pushMessage(queueName,messageBody);
        queueService.pullMessage(queueName);
        Message message = queueService.pullMessage(queueName);
        Assert.assertNull(message);
    }


    @Test
    public void visibilityIsZeroShouldPutTheMessageToTheHeadImmediately(){
        QueueService queueService = getQueueServiceIrrelevantToVisibilityTimeout();
        queueService.createQueue(queueName,0);
        queueService.pushMessage(queueName,messageBody);
        queueService.pullMessage(queueName);
        Message message = queueService.pullMessage(queueName);
        Assert.assertNotNull(message);
    }*/

    /**
     * return a queue service who's behavior irrelevant to the visibility timeout
     * @return
     */
    public abstract QueueService getQueueService();
}
