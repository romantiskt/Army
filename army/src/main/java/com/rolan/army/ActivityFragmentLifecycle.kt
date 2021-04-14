package com.rolan.army

import com.rolan.army.Util.getSnapshot
import java.util.*

/**
 * fragment Lifecycle
 */
class ActivityFragmentLifecycle : Lifecycle {
    private val lifecycleListeners = Collections.newSetFromMap(WeakHashMap<LifecycleListener, Boolean>())
    private var isStarted = false
    private var isDestroyed = false
    override fun addListener(listener: LifecycleListener) {
        lifecycleListeners.add(listener)
        if (isDestroyed) {
            listener.onDestroy()
        } else if (isStarted) {
            listener.onStart()
        } else {
            listener.onStop()
        }
    }

    override fun removeListener(listener: LifecycleListener) {
        lifecycleListeners.remove(listener)
    }

    fun onStart() {
        isStarted = true
        for (lifecycleListener in getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStart()
        }
    }

    fun onStop() {
        isStarted = false
        for (lifecycleListener in getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStop()
        }
    }

    fun onDestroy() {
        isDestroyed = true
        for (lifecycleListener in getSnapshot(lifecycleListeners)) {
            lifecycleListener.onDestroy()
        }
    }
}