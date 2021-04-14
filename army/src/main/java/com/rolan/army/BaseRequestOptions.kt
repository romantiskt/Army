package com.rolan.army

/**
 * Created by Rolan.Wang on 1/29/21.4:18 PM
 * request base
 */
abstract class BaseRequestOptions<T : BaseRequestOptions<T>> {
    var taskName: String? = null
        private set
    var priority: TaskPriority? = null
        private set

    fun priority(priority: TaskPriority): T {
        this.priority = priority
        return this as T
    }

    fun task(taskName: String?): T {
        this.taskName = taskName
        return this as T
    }
}