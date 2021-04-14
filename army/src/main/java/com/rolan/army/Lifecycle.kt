package com.rolan.army

interface Lifecycle {
    fun addListener(listener: LifecycleListener)
    fun removeListener(listener: LifecycleListener)
}