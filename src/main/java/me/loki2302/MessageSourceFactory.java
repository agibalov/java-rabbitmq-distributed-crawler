package me.loki2302;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class MessageSourceFactory {
    private final Channel channel;
    private final JsonSerializer jsonSerializer;
    
    public MessageSourceFactory(Channel channel, JsonSerializer jsonSerializer) {
        this.channel = channel;
        this.jsonSerializer = jsonSerializer;
    }
    
    public <T> MessageSource<T> makeMessageSource(String queueName, Class<T> messageClass) {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new MessageSource<T>(jsonSerializer, consumer, messageClass);
    }
}