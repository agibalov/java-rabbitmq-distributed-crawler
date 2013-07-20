package me.loki2302.tasks;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonProperty;

public class ProcessPlayerTask implements Task {
    @JsonProperty
    public String url;

    @Override
    public void accept(TaskVisitor visitor) throws IOException {
        visitor.visitPlayerTask(this);
    }
}