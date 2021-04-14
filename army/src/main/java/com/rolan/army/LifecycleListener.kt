package com.rolan.army

/**
 * Created by Rolan.Wang on 1/28/21.6:16 PM
 * Lifecycle [android.app.Fragment] and [android.app.Activity]
 */
interface LifecycleListener {
    fun onDestroy()
    fun onStart()
    fun onStop()
}