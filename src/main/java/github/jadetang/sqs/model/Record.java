package github.jadetang.sqs.model;

/**
 * @author sanguan.tangsicheng on 2017/7/21 下午3:04
 */
public class Record {

    //the body of one message
    private String messageBody;
    //the time point when this message is visible to the client
    private Long visibleFrom;
    //receipt handle used to delete a message
    private String receiptHandle;

    public Record() {
    }

    public static Record create(Long visibleFrom, String message) {
        Record r = new Record();
        return r.withVisibleFrom(visibleFrom).withMessageBody(message);
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public Long getVisibleFrom() {
        return visibleFrom;
    }

    public void setVisibleFrom(Long visibleFrom) {
        this.visibleFrom = visibleFrom;
    }

    private Record withMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    private Record withVisibleFrom(Long visibleFrom) {
        this.visibleFrom = visibleFrom;
        return this;
    }


}
