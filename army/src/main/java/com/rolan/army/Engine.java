package com.rolan.army;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Rolan.Wang on 1/29/21.3:09 PM
 * 描述：
 */
public class Engine {

    List<SingleTask> taskBlockingDeque = new ArrayList<>();

    public Engine() {

    }

    private SingleTask findFirstTask() {
        Iterator<SingleTask> iterator = taskBlockingDeque.iterator();
        SingleTask task = null;
        while (iterator.hasNext()) {
            SingleTask next = iterator.next();
            if (task == null) {
                task = next;
                if (task.isShowing()) {
                    return task;
                }
                continue;
            } else {
                if (next.isShowing()) return next;
                if (next.getPriority().getPriority() > task.getPriority().getPriority()) {
                    task = next;
                }
            }
        }
        return task;

    }

    public void post(String taskName, TaskPriority priority, ResourceCallback callback) {
        SingleTask task = new SingleTask(taskName, priority, callback);
        checkQueue(task);
    }

    private void push(SingleTask task){
        if(task==null){
            ArmyLog.d(String.format("不能添加一个空任务"));
            return;
        }
        ArmyLog.d(String.format("添加任务：%s", task.taskName));
        taskBlockingDeque.add(task);
    }

    private void checkQueue(SingleTask newDialogTask) {
        if (taskBlockingDeque != null && taskBlockingDeque.size() > 0) {//已有弹窗
            SingleTask dialogEntity = findFirstTask();
            if (dialogEntity == null) {//队列里没有，这不太可能，除非多线程情况下
                showNewDialog(newDialogTask);
            }
            if (!dialogEntity.isShowing()) {//此时没有弹窗展示
                if (newDialogTask != null) {
                    if (dialogEntity.getPriority().getPriority() > newDialogTask.getPriority().getPriority()) {
                        showNewDialog(dialogEntity);
                        push(newDialogTask);
                    } else {
                        showNewDialog(newDialogTask);
                    }
                } else {
                    showNewDialog(dialogEntity);
                }

            } else {//有弹窗展示，则不再展示，添加进集合就行
                if (newDialogTask != null) {
                    if (newDialogTask.getPriority().getPriority() >= TaskPriority.FORCE.getPriority()) {//强制版本就直接弹
                        showNewDialog(newDialogTask);
                    } else {
                        push(newDialogTask);
                    }
                }
            }
        } else {
            showNewDialog(newDialogTask);
        }
    }

    private void showNewDialog(SingleTask newDialogTask) {
        if (newDialogTask != null && newDialogTask.callback != null) {
            newDialogTask.setStatus(Status.SHOWING);
            newDialogTask.callback.canShow();
            if (!taskBlockingDeque.contains(newDialogTask))
                push(newDialogTask);
        }
    }

    public boolean release(String taskName) {
        if (taskName == null || taskName.length() == 0) return false;
        SingleTask removeKey = null;
        for (SingleTask task : taskBlockingDeque) {
            if (taskName.equals(task.taskName)) {
                removeKey = task;
            }
        }
        if (removeKey != null) {
            taskBlockingDeque.remove(removeKey);
            ArmyLog.d(String.format("移除任务：%s", removeKey));
            checkQueue(null);
            return true;
        }
        return false;
    }

    public void clearCache() {
        taskBlockingDeque.clear();
    }


    public static class SingleTask {
        String taskName;
        TaskPriority priority;
        ResourceCallback callback;
        Status status;

        public SingleTask(String taskName, TaskPriority priority, ResourceCallback callback) {
            this.taskName = taskName;
            this.priority = priority;
            this.callback = callback;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public boolean isShowing() {
            return status == Status.SHOWING;
        }

        public TaskPriority getPriority() {
            return priority;
        }


    }


    public enum Status {
        SHOWING, WAITING, DISMISS
    }
}
