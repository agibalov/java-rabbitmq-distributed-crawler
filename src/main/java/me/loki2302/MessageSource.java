package me.loki2302;

import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class MessageSource<T> {
    private final JsonSerializer jsonSerializer; 
    private final QueueingConsumer consumer;
    private final Class<T> messageClass;
    
    public MessageSource(JsonSerializer jsonSerializer, QueueingConsumer consumer, Class<T> messageClass) {
        this.jsonSerializer = jsonSerializer;
        this.consumer = consumer;
        this.messageClass = messageClass;
    }
    
    public T getMessage() {
        Delivery delivery;
        try {
            delivery = consumer.nextDelivery(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        if(delivery == null) {
            return null;
        }
        
        byte[] messageBody = delivery.getBody();
        return jsonSerializer.deserialize(messageBody, messageClass);
    }
}