package me.loki2302;

import me.loki2302.progress.ProgressMessage;
import me.loki2302.tasks.ProcessAbcTask;

public class ManagerApp {
    private final ManagementService managementService;
    
    public ManagerApp(ManagementService managementService) {
        this.managementService = managementService;
    }
    
    public void run() {
        ProcessAbcTask processAbcTask = new ProcessAbcTask();
        processAbcTask.url = "http://www.nhl.com/ice/playersearch.htm";
        managementService.submitTask(processAbcTask);
        
        int playerCount = 0;
        int taskCount = 0;
        int finishedTaskCount = 0;
        
        while(true) {
            String playerName = managementService.consumeResult();
            if(playerName != null) {
                ++playerCount;
                System.out.printf("[%d] Got player: '%s'\n", playerCount, playerName);
            }
            
            ProgressMessage progressMessage = managementService.consumeProgressMessage();
            if(progressMessage != null) {
                ProgressDeltasProgressMessageVisitor v = new ProgressDeltasProgressMessageVisitor();
                progressMessage.accept(v);
                taskCount += v.getNewTask();
                finishedTaskCount += v.getFinishedTaskCount();
                System.out.printf(
                        "Total: %d, Finished: %d, Progress: %f\n", 
                        taskCount, 
                        finishedTaskCount, 
                        100 * (finishedTaskCount / (double)taskCount));                
            }
            
            if(taskCount == finishedTaskCount) {
                break;                    
            }
        }
        
        System.out.printf("DONE! There were %d players", playerCount);
    }
}