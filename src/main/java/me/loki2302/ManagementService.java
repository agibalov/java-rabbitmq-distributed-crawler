package me.loki2302;

import java.io.IOException;

import me.loki2302.tasks.Task;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class ManagementService {
    private final JsonSerializer jsonSerializer;
    private final Channel channel;
    private final QueueingConsumer resultConsumer;
    
    public ManagementService(JsonSerializer jsonSerializer, Channel channel, QueueingConsumer resultConsumer) {
        this.jsonSerializer = jsonSerializer;
        this.channel = channel;
        this.resultConsumer = resultConsumer;
    }
    
    public void submitTask(Task task) {
        byte[] taskBytes = jsonSerializer.serialize(task);            
        try {
            channel.basicPublish("", "task-queue", null, taskBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String consumeResult() {
        try {
            Delivery delivery = resultConsumer.nextDelivery();
            String playerName = jsonSerializer.deserialize(delivery.getBody(), String.class);
            return playerName;
        } catch (ShutdownSignalException e) {
            throw new RuntimeException(e);
        } catch (ConsumerCancelledException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}