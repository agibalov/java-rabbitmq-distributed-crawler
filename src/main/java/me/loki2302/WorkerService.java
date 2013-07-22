package me.loki2302;

import java.io.IOException;

import me.loki2302.progress.NewTaskAppeared;
import me.loki2302.progress.TaskDone;
import me.loki2302.tasks.Task;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class WorkerService {
    private final JsonSerializer jsonSerializer;
    private final Channel channel;
    private final QueueingConsumer taskConsumer;
    
    public WorkerService(JsonSerializer jsonSerializer, Channel channel, QueueingConsumer taskConsumer) {
        this.jsonSerializer = jsonSerializer;
        this.channel = channel;
        this.taskConsumer = taskConsumer;
    }
    
    public void submitTask(Task task) {
        byte[] taskBytes = jsonSerializer.serialize(task);            
        byte[] progressBytes = jsonSerializer.serialize(new NewTaskAppeared());
        try {
            channel.basicPublish("", CrawlerProtocol.TASK_QUEUE_NAME, null, taskBytes);
            channel.basicPublish("", CrawlerProtocol.TASK_PROGRESS_QUEUE_NAME, null, progressBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void submitTaskDone() {          
        byte[] progressBytes = jsonSerializer.serialize(new TaskDone());
        try {
            channel.basicPublish("", CrawlerProtocol.TASK_PROGRESS_QUEUE_NAME, null, progressBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Task consumeTask() {
        try {
            Delivery delivery = taskConsumer.nextDelivery();               
            Task task = jsonSerializer.deserialize(delivery.getBody(), Task.class);
            return task;
        } catch (ShutdownSignalException e) {
            throw new RuntimeException(e);
        } catch (ConsumerCancelledException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void submitResult(String result) {
        byte[] resultBytes = jsonSerializer.serialize(result);            
        try {
            channel.basicPublish("", CrawlerProtocol.RESULT_QUEUE_NAME, null, resultBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}