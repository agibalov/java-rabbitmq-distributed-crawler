package me.loki2302.tasks;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonProperty;

public class ProcessLetterTask implements Task {
    @JsonProperty
    public String url;
    
    @JsonProperty
    public boolean processPagination;
    
    @Override
    public void accept(TaskVisitor visitor) throws IOException {
        visitor.visitLetterTask(this);            
    }
}