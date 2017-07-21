package com.example;

import com.example.model.Message;

public interface QueueService {

    //
    // Task 1: Define me.
    //
    // This interface should include the following methods.  You should choose appropriate
    // signatures for these methods that prioritise simplicity of implementation for the range of
    // intended implementations (in-memory, file, and SQS).  You may include additional methods if
    // you choose.
    //
    // - push
    //   pushes a message onto a queue.
    // - pull
    //   retrieves a single message from a queue.
    // - removeMessage
    //   deletes a message from the queue that was received by pull().
    //

    /**
     * push a message to a queue
     * @param queueName queue's name
     * @param messageBody message to push
     * @throws com.example.exception.QueueDoesNotExistException if the queue does not exist
     * @throws com.example.exception.OverInFlightLimitException if the number of in flight message exceed the limit
     */
    void pushMessage(String queueName,String messageBody);

    /**
     * pull a message from a queue, return null if the the queue is empty
     * @param queueName queue's name
     * @return the message
     * @throws com.example.exception.QueueDoesNotExistException if the queue does not exist
     */
    Message pullMessage(String queueName);

    /**
     * removeMessage a message from a queue
     * @param queueName the queue's name
     * @param receiptHandle the message's receipt handle
     * @throws com.example.exception.QueueDoesNotExistException if the queue does not exist
     */
    void deleteMessage(String queueName,String receiptHandle);

    /**
     * create a queue
     * @param queueName the queue's name
     * @param visibilityTimeout the visibility timeout in second, can not be less than 0
     * @return the queue's url
     * @throws com.example.exception.QueueNameExistsException if a queue with the same name but a different value for visibility time
     * out already exists
     */
    String createQueue(String queueName,int visibilityTimeout);
}
