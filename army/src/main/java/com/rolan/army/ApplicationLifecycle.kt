package com.rolan.army

/**
 * Created by Rolan.Wang on 1/28/21.6:58 PM
 * application Lifecycle
 */
internal class ApplicationLifecycle : Lifecycle {
    override fun addListener(listener: LifecycleListener) {
        listener.onStart()
    }

    override fun removeListener(listener: LifecycleListener) {
        // Do nothing.
    }
}