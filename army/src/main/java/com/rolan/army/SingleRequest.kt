package com.rolan.army

import android.app.Activity
import android.content.Context
import com.rolan.army.ArmyLog.d

/**
 * Created by Rolan.Wang on 1/29/21.2:39 PM
 */
class SingleRequest(private val context: Context?, private val engine: IEngine, private val armyContext: ArmyContext?, private val requestListeners: List<RequestListener>?, private val requestOptions: BaseRequestOptions<*>) : Request, ResourceCallback {
    override val isComplete: Boolean
        get() = false

    override fun recycle() {}
    override fun begin() {
        engine.post(requestOptions.taskName, requestOptions.priority, this)
    }

    override fun clear() {
        engine.release(requestOptions.taskName)
    }

    override fun canShow(newTask: SingleTask?) {
        if (requestListeners != null && context != null) {
            if (context is Activity) {
                if (context.isFinishing) {
                    d("page has destory")
                    return
                }
            }
            for (listener in requestListeners) {
                listener.startTask(newTask)
            }
        }
    }

    companion object {
        fun obtain(context: Context?,
                   engine: IEngine,
                   armyContext: ArmyContext?,
                   requestListeners: List<RequestListener>?,
                   requestOptions: BaseRequestOptions<*>): SingleRequest {
            return SingleRequest(context, engine, armyContext, requestListeners, requestOptions)
        }
    }
}