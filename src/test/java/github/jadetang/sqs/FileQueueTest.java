package github.jadetang.sqs;

import com.amazonaws.services.sqs.model.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author sanguan.tangsicheng on 2017/7/21 下午7:09
 */
public class FileQueueTest extends BaseTest {

    //
    // Implement me if you have time.
    //

    private FileQueueService fileQueue = FileQueueService.getInstance();

    private TimeMachine timeMachine = new TimeMachine();

    private Long timeout = 10L;

    private String queueName = "file_test_queue";

    @Before
    public void setUp() throws IOException, InterruptedException {
        fileQueue.cleanAllData();
        fileQueue.setClock(timeMachine);
        timeMachine.reset();
        fileQueue.createQueue(queueName, timeout);
    }

    @Override
    public QueueService getQueueService() {
        FileQueueService queueService = FileQueueService.getInstance();
        queueService.cleanAllData();
        return queueService;
    }

    @Test
    public void afterVisibilityTimeOutMessageCouldBeReceivedAgain() {
        fileQueue.pushMessage(queueName, messageBody);
        Message message = fileQueue.pullMessage(queueName);
        //move forward 10 second
        timeMachine.moveForward(timeout, TimeUnit.SECONDS);
        Message message2 = fileQueue.pullMessage(queueName);
        Assert.assertEquals(message.getBody(), message2.getBody());
    }

    @Test
    public void beforeVisibilityTimeOutMessageIsInvisible() {
        fileQueue.pushMessage(queueName, messageBody);
        fileQueue.pullMessage(queueName);
        //move forward 9 second
        timeMachine.moveForward(timeout - 1, TimeUnit.SECONDS);
        Message messages = fileQueue.pullMessage(queueName);
        Assert.assertNull(messages);
    }


    @Test
    public void deletedMessageCanNotBePullEvenAfterTimeOut() {
        fileQueue.pushMessage(queueName, messageBody);
        Message message = fileQueue.pullMessage(queueName);
        fileQueue.deleteMessage(queueName, message.getReceiptHandle());
        timeMachine.moveForward(timeout, TimeUnit.SECONDS);
        Message messages = fileQueue.pullMessage(queueName);
        Assert.assertNull(messages);
    }


}
