package com.rolan.army

/**
 * Created by Rolan.Wang on 4/8/21.3:19 PM
 */
interface IEngine {
    fun clearCache()
    fun release(taskName: String?): Boolean
    fun post(taskName: String?, priority: TaskPriority?, callback: ResourceCallback?)
}