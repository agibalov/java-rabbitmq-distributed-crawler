package me.loki2302.progress;


import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(NewTaskAppeared.class),
    @JsonSubTypes.Type(TaskDone.class)
})
public interface ProgressMessage {
    void accept(ProgressMessageVisitor visitor);
}