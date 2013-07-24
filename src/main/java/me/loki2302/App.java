package me.loki2302;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import me.loki2302.progress.ProgressMessage;
import me.loki2302.tasks.Task;

import org.codehaus.jackson.map.ObjectMapper;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;

public class App {    
    public static void main(String[] args) throws InterruptedException, IOException {        
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.RabbitHostName);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        CrawlerProtocol.initialize(channel);
        CrawlerProtocol.reset(channel);
        channel.close();
        connection.close();
        
        Thread managerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ManagementService managementService = makeManagementService(Config.RabbitHostName);                                
                ManagerApp managerApp = new ManagerApp(managementService);
                managerApp.run();                
            }            
        });
        
        List<Thread> workerThreads = new ArrayList<Thread>();
        for(int i = 0; i < 80; ++i) {
            Thread workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    WorkerService workerService = makeWorkerService(Config.RabbitHostName);                    
                    WorkerApp workerApp = new WorkerApp(workerService);
                    workerApp.run();                
                }            
            });
            workerThreads.add(workerThread);
        }        
        
        managerThread.start();
        for(Thread workerThread : workerThreads) {
            workerThread.start();
        }
        
        managerThread.join();
        for(Thread workerThread : workerThreads) {
            workerThread.join();
        }
    }
    
    private static ManagementService makeManagementService(String rabbitHostName) {
        Channel channel = makeChannel(rabbitHostName);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSerializer jsonSerializer = new JsonSerializer(objectMapper);
        
        MessageDestinationFactory destinationFactory = new MessageDestinationFactory(channel, jsonSerializer);
        MessageDestination<Task> taskDestination = destinationFactory
                .makeMessageDestination(CrawlerProtocol.TASK_QUEUE_NAME, Task.class);
        MessageDestination<ProgressMessage> taskProgressDestination = destinationFactory
                .makeMessageDestination(CrawlerProtocol.TASK_PROGRESS_QUEUE_NAME, ProgressMessage.class);
        
        MessageSourceFactory sourceFactory = new MessageSourceFactory(channel, jsonSerializer);
        MessageSource<String> resultSource = sourceFactory
                .makeMessageSource(CrawlerProtocol.RESULT_QUEUE_NAME, String.class);
        MessageSource<ProgressMessage> taskProgressSource = sourceFactory.makeMessageSource(
                CrawlerProtocol.TASK_PROGRESS_QUEUE_NAME,
                ProgressMessage.class);
        
        ManagementService managementService = new ManagementService(
                taskDestination,
                taskProgressDestination,
                resultSource,
                taskProgressSource);
        
        return managementService;
    }
    
    private static WorkerService makeWorkerService(String rabbitHostName) {
        Channel channel = makeChannel(rabbitHostName);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSerializer jsonSerializer = new JsonSerializer(objectMapper);
        
        MessageDestinationFactory destinationFactory = new MessageDestinationFactory(channel, jsonSerializer);
        MessageDestination<Task> taskDestination = destinationFactory
                .makeMessageDestination(CrawlerProtocol.TASK_QUEUE_NAME, Task.class);
        MessageDestination<ProgressMessage> taskProgressDestination = destinationFactory
                .makeMessageDestination(CrawlerProtocol.TASK_PROGRESS_QUEUE_NAME, ProgressMessage.class);
        MessageDestination<String> resultDestination = destinationFactory
                .makeMessageDestination(CrawlerProtocol.RESULT_QUEUE_NAME, String.class);
        
        MessageSourceFactory sourceFactory = new MessageSourceFactory(channel, jsonSerializer);
        MessageSource<Task> taskSource = sourceFactory
                .makeMessageSource(CrawlerProtocol.TASK_QUEUE_NAME, Task.class);
        
        WorkerService workerService = new WorkerService(
                taskDestination,
                taskProgressDestination,
                resultDestination,
                taskSource);
        
        return workerService;        
    }
        
    private static Channel makeChannel(String rabbitHostName) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(rabbitHostName);
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();                       
            CrawlerProtocol.initialize(channel);
            return channel;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
