package me.loki2302.tasks;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(ProcessAbcTask.class),
    @JsonSubTypes.Type(ProcessLetterTask.class),
    @JsonSubTypes.Type(ProcessPlayerTask.class)
})
public interface Task {        
    void accept(TaskVisitor visitor) throws IOException;
}