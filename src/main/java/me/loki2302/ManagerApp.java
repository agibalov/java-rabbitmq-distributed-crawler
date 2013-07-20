package me.loki2302;

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
        while(true) {
            String playerName = managementService.consumeResult();
            if(playerName == null) {
                continue;
            }
            
            ++playerCount;
            System.out.printf("[%d] Got player: '%s'\n", playerCount, playerName);
        }
    }
}