package com.rolan.army

import java.util.*

/**
 * Created by Rolan.Wang on 4/8/21.3:23 PM
 * single task
 */
class SingleTask(val taskName: String, val priority: TaskPriority, val callback: ResourceCallback) : Comparable<Any?> {

    var status: Status? = null
    val id: String = UUID.randomUUID().toString().replace("-","")
    val isShowing: Boolean
        get() = status == Status.SHOWING

    override fun compareTo(o: Any?): Int {
        return if (priority.priority > (o as SingleTask?)?.priority?.priority ?: 0) 1 else 0
    }
}