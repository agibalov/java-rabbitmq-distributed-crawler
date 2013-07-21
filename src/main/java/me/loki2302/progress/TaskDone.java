package me.loki2302.progress;

public class TaskDone implements ProgressMessage {   
    @Override
    public void accept(ProgressMessageVisitor visitor) {
        visitor.visitTaskDone(this);            
    }
}