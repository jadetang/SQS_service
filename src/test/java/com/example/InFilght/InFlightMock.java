package com.example.InFilght;

import com.example.model.Message;

import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author sanguan.tangsicheng on 2017/7/21 上午8:55
 */
public class InFlightMock {

    /**
     * return a in-flight queue mocker do nothing
     */
    @SuppressWarnings("unchecked")
    public static InFlightQueue doNothingInFlightQueue() {
        InFlightQueue inFlightQueue = mock(InFlightQueue.class);
        doNothing().when(inFlightQueue).put(anyString(), any(Message.class), anyInt(), any(Consumer.class));
        doNothing().when(inFlightQueue).removeMessage(anyString(), anyString());
        return inFlightQueue;
    }


    /**
     * return a in-flight queue mocker times out a invisible message instantly
     */
    @SuppressWarnings("unchecked")
    public static InFlightQueue timeOutFastInFlightQueue() {
        InFlightQueue inFlightQueue = mock(InFlightQueue.class);
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            Message message = (Message) args[1];
            Consumer<Message> consumer = (Consumer) args[3];
            consumer.accept(message);
            return null;
        }).when(inFlightQueue).put(anyString(), any(Message.class), anyInt(), any(Consumer.class));
        doNothing().when(inFlightQueue).removeMessage(anyString(), anyString());
        return inFlightQueue;
    }

}
