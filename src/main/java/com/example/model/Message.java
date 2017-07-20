package com.example.model;

import com.google.common.base.Preconditions;

/**
 * @author sanguan.tangsicheng on 2017/7/19 下午10:01
 */
public class Message {


    public Message(String messageId, String receiptHandle, String body) {
        Preconditions.checkArgument(body != null && body.length() > 0, "message body should not be null or empty.");
        this.messageId = messageId;
        this.body = body;
        this.receiptHandle = receiptHandle;
    }

    public Message(String messageId,String body){
        this(messageId,null,body);
    }

    private String messageId;
    private String body;
    private String receiptHandle;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        if (!getMessageId().equals(message.getMessageId())) return false;
        if (!getBody().equals(message.getBody())) return false;
        return getReceiptHandle().equals(message.getReceiptHandle());
    }

    @Override
    public int hashCode() {
        int result = getMessageId().hashCode();
        result = 31 * result + getBody().hashCode();
        result = 31 * result + getReceiptHandle().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", body='" + body + '\'' +
                ", receiptHandle='" + receiptHandle + '\'' +
                '}';
    }
}
