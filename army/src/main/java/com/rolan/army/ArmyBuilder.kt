package com.rolan.army

import android.content.Context
import com.rolan.army.RequestListener
import com.rolan.army.IEngine
import com.rolan.army.ArmyBuilder
import com.rolan.army.Army
import com.rolan.army.RequestManagerRetriever
import com.rolan.army.DefaultEngine
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Rolan.Wang on 1/28/21.5:49 PM
 * Army instance construction, externally recreable
 */
class ArmyBuilder {
    private var defaultRequestListeners: MutableList<RequestListener>? = null
    private var engine: IEngine? = null

    /**
     * Add a global listener. Each request will be carried with this callback
     * @param listener
     * @return
     */
    fun addGlobalRequestListener(listener: RequestListener): ArmyBuilder {
        if (defaultRequestListeners == null) {
            defaultRequestListeners = ArrayList()
        }
        defaultRequestListeners?.add(listener)
        return this
    }

    /**
     * Set up the custom engine
     * @param engine custom engine
     * @return
     */
    fun setEngine(engine: IEngine?): ArmyBuilder {
        this.engine = engine
        return this
    }

    fun build(context: Context): Army {
        defaultRequestListeners = if (defaultRequestListeners == null) {
            ArrayList()
        } else {
            Collections.unmodifiableList(defaultRequestListeners)
        }
        val requestManagerRetriever = RequestManagerRetriever()
        if (engine == null) {
            engine = DefaultEngine()
        }
        return Army(context, defaultRequestListeners!!, requestManagerRetriever, engine!!)
    }
}