package com.rolan.army;

import androidx.annotation.NonNull;

/**
 * Created by Rolan.Wang on 1/29/21.4:18 PM
 * 描述：
 */
public abstract class BaseRequestOptions<T extends BaseRequestOptions<T>> {

    private String taskName;
    private TaskPriority priority;

    public T priority(@NonNull TaskPriority priority) {
        this.priority=priority;
        return (T) this;
    }


    public T task(String taskName){
        this.taskName=taskName;
        return (T)this;
    }


    public String getTaskName() {
        return taskName;
    }

    public TaskPriority getPriority() {
        return priority;
    }
}
