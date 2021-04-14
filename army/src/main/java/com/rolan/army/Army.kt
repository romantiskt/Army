package com.rolan.army

import android.annotation.SuppressLint
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import com.rolan.army.ArmyLog.openDebug
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.5:41 PM
 * Use it as follows：
 * 1.Load the required task, and the internal default will execute the corresponding callback according to the priority
 * Army.with(this).load("task name")
 * .priority(TaskPriority.LOW.setPriorityExtra(10))
 * .listener(new RequestListener() {
 * @Override
 * public void startTask() {
 * doSomeThing()
 * }
 * })
 * .post();
 *
 * 2.After the task completes execution
 * Army.with(MainActivity.this).cancel("task name");
 */
class Army internal constructor(val context: Context,
                                defaultRequestListeners: List<RequestListener?>,
                                val requestManagerRetriever: RequestManagerRetriever,
                                private val engine: IEngine) : ComponentCallbacks {
    val armyContext: ArmyContext = ArmyContext(context, defaultRequestListeners, engine)
    private val managers: MutableList<RequestManager> = ArrayList()

    /**
     * register RequestManager
     * @param requestManager
     */
    fun registerRequestManager(requestManager: RequestManager) {
        synchronized(managers) {
            check(!managers.contains(requestManager)) { "Cannot register already registered manager" }
            managers.add(requestManager)
        }
    }

    /**
     * unregister RequestManager
     * @param requestManager
     */
    fun unregisterRequestManager(requestManager: RequestManager) {
        synchronized(managers) {
            check(managers.contains(requestManager)) { "Cannot unregister not yet registered manager" }
            managers.remove(requestManager)
        }
    }

    /**
     * clear engine cache
     */
    fun clear() {
        engine.clearCache()
    }

    override fun onConfigurationChanged(configuration: Configuration) {}

    /**
     * low memory clear cache
     */
    override fun onLowMemory() {
        clear()
    }


    companion object {
        @Volatile
        @SuppressLint("StaticFieldLeak")
        private var army: Army? = null

        @Volatile
        private var isInitializing = false

        /**
         * @param context context
         * @return
         */
        @JvmStatic
        fun getIns(context: Context): Army {
            if (army == null) {
                synchronized(Army::class.java) {
                    if (army == null) {
                        checkAndInitializeArmy(context)
                    }
                }
            }
            return army!!
        }

        /**
         * open log,default close
         */
        @JvmStatic
        fun openLog() {
            openDebug()
        }

        @JvmStatic
        fun with(context: Context?): RequestManager {
            requireNotNull(context) { "You cannot start a load on a null Context" }
            return getRetriever(context).getIns(context)
        }

        private fun getRetriever(context: Context): RequestManagerRetriever {
            return getIns(context).requestManagerRetriever
        }

        private fun checkAndInitializeArmy(context: Context) {
            if (isInitializing) {
                return
            }
            isInitializing = true
            initializeArmy(context, ArmyBuilder())
            isInitializing = false
        }

        /**
         *Provide external custom ArmyBuilder, can set IEngine separately
         * @param context context
         * @param builder ArmyBuilder
         */
        @Synchronized
        fun init(context: Context, builder: ArmyBuilder) {
            if (army != null) {
                if (army != null) {
                    army?.context
                            ?.applicationContext
                            ?.unregisterComponentCallbacks(army)
                    army?.engine?.clearCache()
                }
                army = null
            }
            initializeArmy(context, builder)
        }

        private fun initializeArmy(context: Context, builder: ArmyBuilder) {
            val applicationContext = context.applicationContext
            val army = builder.build(applicationContext)
            applicationContext.registerComponentCallbacks(army)
            Companion.army = army
        }
    }

}