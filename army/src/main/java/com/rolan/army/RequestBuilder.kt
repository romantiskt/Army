package com.rolan.army

import android.content.Context
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.6:43 PM
 * request builder
 */
class RequestBuilder(private val army: Army, private val requestManager: RequestManager, private val context: Context) : BaseRequestOptions<RequestBuilder>() {
    private val armyContext: ArmyContext = army.armyContext
    private var requestListeners: MutableList<RequestListener>? = null
    private fun initRequestListeners(requestListeners: List<RequestListener>) {
        for (listener in requestListeners) {
            addListener(listener)
        }
    }

    private fun buildRequest(): Request {
        return SingleRequest.obtain(context, armyContext.engine, armyContext, requestListeners, this)
    }

    fun post() {
        val request = buildRequest()
        requestManager.track(request)
    }

    private fun addListener(
            requestListener: RequestListener?): RequestBuilder {
        if (requestListener != null) {
            if (requestListeners == null) {
                requestListeners = ArrayList()
            }
            requestListeners?.add(requestListener)
        }
        return this
    }

    fun listener(
            requestListener: RequestListener?): RequestBuilder {
        return addListener(requestListener)
    }

    init {
        initRequestListeners(requestManager.defaultRequestListeners)
    }
}