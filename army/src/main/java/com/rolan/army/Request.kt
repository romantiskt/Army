package com.rolan.army

/**
 * Created by Rolan.Wang on 1/29/21.2:38 PM
 * task request
 */
interface Request {
    val isComplete: Boolean
    fun recycle()
    fun begin()
    fun clear()
}