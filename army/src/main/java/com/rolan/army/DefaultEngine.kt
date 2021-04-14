package com.rolan.army

import com.rolan.army.ArmyLog.d

/**
 * Created by Rolan.Wang on 1/29/21.3:09 PM
 * engine default
 */
class DefaultEngine : IEngine {
    var taskBlockingDeque: MutableList<SingleTask>? = ArrayList()
    override fun post(taskName: String?, priority: TaskPriority?, callback: ResourceCallback?) {
        val task = SingleTask(taskName!!, priority!!, callback!!)
        checkQueue(task)
    }

    override fun release(taskName: String?): Boolean {
        if (taskName == null || taskName.isEmpty()) return false
        var removeKey: SingleTask? = null
        for (task in taskBlockingDeque!!) {
            if (taskName == task.taskName) {
                removeKey = task
                break
            }
        }
        if (removeKey != null) {
            taskBlockingDeque!!.remove(removeKey)
            d(String.format("task remove：%s", removeKey))
            checkQueue(null)
            return true
        }
        return false
    }

    override fun clearCache() {
        taskBlockingDeque!!.clear()
    }

    private fun push(task: SingleTask?) {
        if (task == null) {
            d(String.format("cannot add empty task"))
            return
        }
        d(String.format("task add：%s", task.taskName))
        taskBlockingDeque!!.add(task)
    }

    private fun findFirstTask(): SingleTask? {
        val iterator: Iterator<SingleTask> = taskBlockingDeque!!.iterator()
        var task: SingleTask? = null
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (task == null) {
                task = next
                if (task.isShowing) {
                    return task
                }
                continue
            } else {
                if (next.isShowing) return next
                if (next.priority.priority > task.priority.priority) {
                    task = next
                }
            }
        }
        return task
    }

    private fun checkQueue(newTask: SingleTask?) {
        if (taskBlockingDeque != null && taskBlockingDeque!!.size > 0) {
            val taskEntity = findFirstTask()
            if (taskEntity == null) {
                showNewTask(newTask)
            }
            if (!taskEntity!!.isShowing) {
                if (newTask != null) {
                    if (taskEntity.priority.priority > newTask.priority.priority) {
                        showNewTask(taskEntity)
                        push(newTask)
                    } else {
                        showNewTask(newTask)
                    }
                } else {
                    showNewTask(taskEntity)
                }
            } else {
                if (newTask != null) {
                    if (newTask.priority.priority >= TaskPriority.FORCE.priority) {
                        showNewTask(newTask)
                    } else {
                        push(newTask)
                    }
                }
            }
        } else {
            showNewTask(newTask)
        }
    }

    private fun showNewTask(newTask: SingleTask?) {
        if (newTask?.callback != null) {
            newTask.status = Status.SHOWING
            newTask.callback.canShow(newTask)
            if (!taskBlockingDeque!!.contains(newTask)) push(newTask)
        }
    }
}