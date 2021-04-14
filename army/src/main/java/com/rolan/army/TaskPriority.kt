package com.rolan.army

/**
 * Created by Rolan.Wang on 2/3/21.11:06 AM
 */
enum class TaskPriority(var priority: Int) {
    /**
     * 1000~2000 low
     * 2000~3000 normal
     * 3000~4000 importance
     * 4000~5000 admin
     * 5000~6000 force Whether or not there is a task ahead, if there is, it will be covered
     */
    LOW(1000), NORMAL(2000), IMPORTANCE(3000), ADMIN(4000), FORCE(5000);

    fun setPriorityExtra(priority: Int): TaskPriority {
        if (priority > 0) {
            if (priority >= 1000) { //保证不随机加优先级
                this.priority = priority % 1000
            } else {
                this.priority += priority
            }
        }
        return this
    }
}