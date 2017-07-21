package com.example;

import com.example.exception.QueueDoesNotExistException;
import com.example.exception.QueueNameExistsException;
import com.example.model.Message;
import org.junit.Assert;
import org.junit.Test;

/** base test about queue service irrelevant to the visibility timeout
 * @author sanguan.tangsicheng on 2017/7/20 下午11:27
 */
public abstract class BaseTest {

    String queueName = "test queue";

    String messageBody = "this is a message";

    @Test
    public void pullMessageBodyShouldEqualToOriginMessage(){
        QueueService queue = getQueueServiceIrrelevantToVisibilityTimeout();
        String queueUrl = queue.createQueue(queueName,10);
        queue.pushMessage(queueUrl,messageBody);
        Message messageFromQueue = queue.pullMessage(queueUrl);
        Assert.assertEquals(messageBody,messageFromQueue.getBody());
    }



    @Test(expected = QueueNameExistsException.class)
    public void createQueueWithDifferentTimeoutShouldThrowException(){
        QueueService queueService = getQueueServiceIrrelevantToVisibilityTimeout();
        queueService.createQueue(queueName,5);
        queueService.createQueue(queueName,10);
    }



    @Test(expected = QueueDoesNotExistException.class)
    public void putMessageToNonExistQueueShouldThrowException(){
        QueueService queueService = getQueueServiceIrrelevantToVisibilityTimeout();
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
    public abstract QueueService getQueueServiceIrrelevantToVisibilityTimeout();
}
