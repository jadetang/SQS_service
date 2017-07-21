package com.example.InFilght;

import com.example.model.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午9:53
 */
public class SchedulerServiceInFlightQueueTest {
    @Test
    public void putMessageAndTheConsumeActionWillBeCalled() throws Exception {
        SchedulerServiceInFlightQueue inFlightQueue = new SchedulerServiceInFlightQueue();
        String messageBody = "this is a test message";
        Message message = new Message("id", "receiptHandle", messageBody);
        final String[] stringWrapper = new String[1];
        inFlightQueue.put("test queue", message, 1, message1 -> stringWrapper[0] = message1.getBody());
        TimeUnit.SECONDS.sleep(2);
        Assert.assertEquals(messageBody, stringWrapper[0]);
    }

    @Test
    public void ifDeleteTheBodyOfMessageShouldNotBeModified() throws Exception {
        SchedulerServiceInFlightQueue inFlightQueue = new SchedulerServiceInFlightQueue();
        String messageBody = "this is a test message";
        Message message = new Message("id","receiptHandle", messageBody);
        inFlightQueue.put("test queue", message, 1, new Consumer<Message>() {
            @Override
            public void accept(Message message) {
                message.setBody("new message body");
            }
        });
        inFlightQueue.removeMessage("test queue",message.getReceiptHandle());
        TimeUnit.SECONDS.sleep(2);
        Assert.assertEquals(messageBody,message.getBody());
    }

}
