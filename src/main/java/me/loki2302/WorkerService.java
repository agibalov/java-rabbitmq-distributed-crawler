package me.loki2302;

import me.loki2302.progress.NewTaskAppeared;
import me.loki2302.progress.ProgressMessage;
import me.loki2302.progress.TaskDone;
import me.loki2302.tasks.Task;

public class WorkerService {
    private final MessageDestination<Task> taskDestination;
    private final MessageDestination<ProgressMessage> taskProgressDestination;
    private final MessageDestination<String> resultDestination;
    private final MessageSource<Task> taskSource;
    
    public WorkerService(
            MessageDestination<Task> taskDestination,
            MessageDestination<ProgressMessage> taskProgressDestination,
            MessageDestination<String> resultDestination,
            MessageSource<Task> taskSource) {
        this.taskDestination = taskDestination;
        this.taskProgressDestination = taskProgressDestination;
        this.resultDestination = resultDestination;
        this.taskSource = taskSource;
    }
    
    public void submitTask(Task task) {
        taskDestination.putMessage(task);
        taskProgressDestination.putMessage(new NewTaskAppeared());
    }
    
    public void submitTaskDone() {          
        taskProgressDestination.putMessage(new TaskDone());
    }
    
    public Task consumeTask() {
        return taskSource.getMessage();
    }
    
    public void submitResult(String result) {
        resultDestination.putMessage(result);
    }
}