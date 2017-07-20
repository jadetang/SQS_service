package com.example.exception;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午2:54
 */
public class QueueDoesNotExistException extends RuntimeException {

    public QueueDoesNotExistException(String queueUrl) {
        super(String.format("%s does not exits.", queueUrl));
    }

}
