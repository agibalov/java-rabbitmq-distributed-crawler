package me.loki2302;

import java.io.IOException;

import me.loki2302.progress.NewTaskAppeared;
import me.loki2302.progress.ProgressMessage;
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
    private final QueueingConsumer taskProgressConsumer;
    
    public ManagementService(
            JsonSerializer jsonSerializer, 
            Channel channel, 
            QueueingConsumer resultConsumer, 
            QueueingConsumer taskProgressConsumer) {
        
        this.jsonSerializer = jsonSerializer;
        this.channel = channel;
        this.resultConsumer = resultConsumer;
        this.taskProgressConsumer = taskProgressConsumer;
    }
    
    public void submitTask(Task task) {
        byte[] taskBytes = jsonSerializer.serialize(task);            
        byte[] progressBytes = jsonSerializer.serialize(new NewTaskAppeared());
        try {
            channel.basicPublish("", Rabbit.TASK_QUEUE_NAME, null, taskBytes);
            channel.basicPublish("", Rabbit.TASK_PROGRESS_QUEUE_NAME, null, progressBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String consumeResult() {
        try {
            Delivery delivery = resultConsumer.nextDelivery(100);
            if(delivery == null) {
                return null;
            }
            
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
    
    public ProgressMessage consumeProgressMessage() {
        try {
            Delivery delivery = taskProgressConsumer.nextDelivery(100);
            if(delivery == null) {
                return null;
            }
            
            ProgressMessage progressMessage = jsonSerializer.deserialize(delivery.getBody(), ProgressMessage.class);
            return progressMessage;
        } catch (ShutdownSignalException e) {
            throw new RuntimeException(e);
        } catch (ConsumerCancelledException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}