package me.loki2302;

import com.rabbitmq.client.Channel;

public class MessageDestinationFactory {
    private final Channel channel;
    private final JsonSerializer jsonSerializer;
    
    public MessageDestinationFactory(Channel channel, JsonSerializer jsonSerializer) {
        this.channel = channel;
        this.jsonSerializer = jsonSerializer;
    }
    
    public <T> MessageDestination<T> makeMessageDestination(String queueName, Class<T> messageClass) {
        return new MessageDestination<T>(jsonSerializer, queueName, channel);
    }
}