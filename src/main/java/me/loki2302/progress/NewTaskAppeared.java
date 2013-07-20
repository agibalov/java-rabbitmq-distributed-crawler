package me.loki2302.progress;


public class NewTaskAppeared implements ProgressMessage {
    @Override
    public void accept(ProgressMessageVisitor visitor) {
        visitor.visitNewTaskAppeared(this);            
    }
}