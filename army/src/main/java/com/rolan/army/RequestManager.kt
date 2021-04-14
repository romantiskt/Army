package com.rolan.army

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.rolan.army.RequestManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Rolan.Wang on 1/28/21.6:16 PM
 * request manager
 */
class RequestManager(
        private val army: Army, private val lifecycle: Lifecycle, private val context: Context) : LifecycleListener {
    private val requestTracker: RequestTracker = RequestTracker()
    val defaultRequestListeners: CopyOnWriteArrayList<RequestListener>
    private val addSelfToLifecycle = Runnable { lifecycle.addListener(this@RequestManager) }
    private val mainHandler = Handler(Looper.getMainLooper())
    override fun onDestroy() {
        requestTracker.clearRequests()
        lifecycle.removeListener(this)
        mainHandler.removeCallbacks(addSelfToLifecycle)
        army.unregisterRequestManager(this)
    }

    override fun onStart() {}
    override fun onStop() {}
    fun cancel(taskName: String?) {
        army.armyContext.engine.release(taskName)
    }

    fun load(taskName: String?): RequestBuilder {
        return RequestBuilder(army, this, context).task(taskName)
    }

    /**
     * add global requestListener Only valid within the scope of the RequestManager
     * @param requestListener listener
     * @return
     */
    fun addGlobalRequestListener(requestListener: RequestListener): RequestManager {
        defaultRequestListeners.add(requestListener)
        return this
    }

    fun getDefaultRequestListeners(): List<RequestListener> {
        return defaultRequestListeners
    }

    fun track(request: Request?) {
        requestTracker.runRequest(request!!)
    }

    init {
        if (Util.isOnBackgroundThread) {
            mainHandler.post(addSelfToLifecycle)
        } else {
            lifecycle.addListener(this)
        }
        defaultRequestListeners = CopyOnWriteArrayList(army.armyContext.defaultRequestListeners)
        army.registerRequestManager(this)
    }
}