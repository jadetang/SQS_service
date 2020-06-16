package github.jadetang.sqs;

import com.amazonaws.services.sqs.model.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class InMemoryQueueTest extends BaseTest {
    //
    // Implement me.
    //

    private InMemoryQueueService queueService;

    private long timeout = 10L;

    private String queueName = "test queue";

    private TimeMachine timeMachine = new TimeMachine();

    @Before
    public void setUp() {
        queueService = new InMemoryQueueService();
        timeMachine.reset();
        queueService.setClock(timeMachine);
        queueService.createQueue(queueName, timeout);

    }

    @Override
    public QueueService getQueueService() {
        return new InMemoryQueueService();
    }

    @Test
    public void afterVisibilityTimeOutMessageCouldBeReceivedAgain() {

        queueService.pushMessage(queueName, messageBody);
        Message message = queueService.pullMessage(queueName);
        //move forward 10 second
        timeMachine.moveForward(timeout, TimeUnit.SECONDS);
        Message message2 = queueService.pullMessage(queueName);
        Assert.assertEquals(message.getBody(), message2.getBody());
    }

    @Test
    public void beforeVisibilityTimeOutMessageIsInvisible() {
        queueService.pushMessage(queueName, messageBody);
        queueService.pullMessage(queueName);
        //move forward 9 second
        timeMachine.moveForward(timeout - 1, TimeUnit.SECONDS);
        Message messages = queueService.pullMessage(queueName);
        Assert.assertNull(messages);
    }

    @Test
    public void deletedMessageCanNotBePullEvenAfterTimeOut() {
        queueService.pushMessage(queueName, messageBody);
        Message message = queueService.pullMessage(queueName);
        queueService.deleteMessage(queueName, message.getReceiptHandle());
        timeMachine.moveForward(timeout, TimeUnit.SECONDS);
        Message messages = queueService.pullMessage(queueName);
        Assert.assertNull(messages);

    }


}


