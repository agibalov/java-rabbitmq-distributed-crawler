package me.loki2302;

import java.io.IOException;

import com.rabbitmq.client.Channel;

public class MessageDestination<T> {
    private final JsonSerializer jsonSerializer;
    private final String queueName;
    private final Channel channel;
    
    public MessageDestination(JsonSerializer jsonSerializer, String queueName, Channel channel) {
        this.jsonSerializer = jsonSerializer;
        this.queueName = queueName;
        this.channel = channel;
    }
    
    public void putMessage(T message) {
        byte[] messageBody = jsonSerializer.serialize(message);
        try {
            channel.basicPublish("", queueName, null, messageBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}