package me.loki2302.tasks;

import java.io.IOException;

public interface TaskVisitor {
    void visitAbcTask(ProcessAbcTask task) throws IOException;
    void visitLetterTask(ProcessLetterTask task) throws IOException;
    void visitPlayerTask(ProcessPlayerTask task) throws IOException;
}