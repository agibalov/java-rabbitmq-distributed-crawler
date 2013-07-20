package me.loki2302;

import me.loki2302.progress.NewTaskAppeared;
import me.loki2302.progress.ProgressMessageVisitor;
import me.loki2302.progress.TaskDone;

public class ProgressDeltasProgressMessageVisitor implements ProgressMessageVisitor {
    private int newTaskCount;
    private int finishedTaskCount;
    
    @Override
    public void visitNewTaskAppeared(NewTaskAppeared message) {
        newTaskCount = 1;
    }

    @Override
    public void visitTaskDone(TaskDone message) {
        finishedTaskCount = 1;
    }        
    
    public int getNewTask() {
        return newTaskCount;
    }
    
    public int getFinishedTaskCount() {
        return finishedTaskCount;
    }
}