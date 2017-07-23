package com.example;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** some base applied both to memory queue and file queue, only test case irrelevant to visibility time out behavior.
 * @author sanguan.tangsicheng on 2017/7/20 下午11:27
 */
public abstract class BaseTest {

    String queueName = "test queue";

    String messageBody = "this is a message";


    /**
     * return a queue service who's behavior irrelevant to the visibility timeout
     *
     * @return the queue service
     */
    public abstract QueueService getQueueService();

    @Test
    public void pullMessageBodyShouldEqualToOriginMessage() {
        QueueService queue = getQueueService();
        queue.createQueue(queueName, 15L);
        queue.pushMessage(queueName, messageBody);
        Message messageFromQueue = queue.pullMessage(queueName);
        Assert.assertEquals(messageBody, messageFromQueue.getBody());
    }


    @Test(expected = QueueNameExistsException.class)
    public void createQueueWithDifferentTimeoutShouldThrowException() {
        QueueService queueService = getQueueService();
        queueService.createQueue(queueName, 5L);
        queueService.createQueue(queueName, 10L);
    }


    @Test(expected = QueueDoesNotExistException.class)
    public void putMessageToNonExistQueueShouldThrowException() {
        QueueService queueService = getQueueService();
        queueService.pushMessage("emptyUrl", messageBody);
    }


    @Test
    public void MultiThreadPushAndPullTheTotalMessageNumberShouldEqual() throws InterruptedException {
        QueueService queueService = getQueueService();
        queueService.createQueue(queueName, 1000L);
        int treadNum = 10;
        List<Thread> pushThreads = new ArrayList<>(treadNum);
        int eachThreadPushMessage = 10;
        AtomicInteger globalCount = new AtomicInteger();
        for (int i = 0; i < treadNum; i++) {
            pushThreads.add(new Thread(new PushMessageWorker(queueService, queueName, eachThreadPushMessage, globalCount)));
        }
        pushThreads.forEach(Thread::start);
        for (Thread t : pushThreads) {
            t.join();
        }
        while (queueService.pullMessage(queueName) != null) {
            globalCount.decrementAndGet();
        }
        Assert.assertEquals(0, globalCount.get());
    }



    private static class PushMessageWorker implements Runnable {

        private QueueService queueService;

        private int messageNumber;

        private AtomicInteger count;

        private String queueName;

        PushMessageWorker(QueueService queueService, String queueName, int messageNumber, AtomicInteger count) {
            this.queueService = queueService;
            this.messageNumber = messageNumber;
            this.count = count;
            this.queueName = queueName;
        }

        @Override
        public void run() {
            while (messageNumber > 0) {
                queueService.pushMessage(queueName, Thread.currentThread().getName());
                messageNumber--;
                count.incrementAndGet();
            }
        }
    }
}
