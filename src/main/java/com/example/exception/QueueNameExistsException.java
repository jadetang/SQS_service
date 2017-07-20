package com.example.exception;

/**
 * @author sanguan.tangsicheng on 2017/7/20 下午3:58
 */
public class QueueNameExistsException extends RuntimeException {


    public QueueNameExistsException(String message){
        super(message);
    }
}
