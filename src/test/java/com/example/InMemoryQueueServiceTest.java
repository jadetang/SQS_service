package com.example;

import com.example.InFilght.InFlightMock;
import com.example.model.Message;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午4:22
 */
public class InMemoryQueueServiceTest extends BaseTest {


    @Override
    public QueueService getQueueServiceIrrelevantToVisibilityTimeout() {
        return new InMemoryQueueService(InFlightMock.doNothingInFlightQueue());
    }


    @Test
    public void afterVisibilityTheMessageCouldReceivedAgain(){
        String queueName = "testQueue";
        String messageBody = "this is a test,the message should place at the head of the queue after visibility time out";
        InMemoryQueueService queueService = new InMemoryQueueService(InFlightMock.timeOutFastInFlightQueue());
        queueService.createQueue(queueName,10);
        queueService.pushMessage(queueName,messageBody);
        Message message = queueService.pullMessage(queueName);
        // do something else, pretend 10 second past, the message is not deleted
        Message messageReceivedAgain = queueService.pullMessage(queueName);
        Assert.assertEquals(message.getBody(),messageReceivedAgain.getBody());
    }
}
