package me.loki2302.progress;

public interface ProgressMessageVisitor {
    void visitNewTaskAppeared(NewTaskAppeared message);
    void visitTaskDone(TaskDone message);
}