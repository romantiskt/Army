package com.rolan.army

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.6:04 PM
 */
class RequestManagerRetriever : Handler.Callback {
    private val handler: Handler = Handler(Looper.getMainLooper(), this /* Callback */)
    private val factory: RequestManagerFactory = DEFAULT_FACTORY

    @Volatile
    private var applicationManager: RequestManager? = null
    private val pendingSupportRequestManagerFragments: MutableMap<FragmentManager, SupportRequestManagerFragment> = HashMap()
    private val pendingRequestManagerFragments: MutableMap<android.app.FragmentManager, RequestManagerFragment> = HashMap()
    override fun handleMessage(message: Message): Boolean {
        var handled = true
        var removed: Any? = null
        var key: Any? = null
        when (message.what) {
            ID_REMOVE_SUPPORT_FRAGMENT_MANAGER -> {
                val supportFm = message.obj as FragmentManager
                key = supportFm
                removed = pendingSupportRequestManagerFragments.remove(supportFm)
            }
            else -> handled = false
        }
        if (handled && removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: $key")
        }
        return handled
    }

    fun getIns(context: Context): RequestManager {
        if (Util.isOnMainThread && context !is Application) {
            if (context is FragmentActivity) {
                return get(context)
            } else if (context is Activity) {
                return get(context)
            } else if (context is ContextWrapper) {
                return getIns(context.baseContext)
            }
        }
        return getApplicationManager(context)
    }

    operator fun get(activity: FragmentActivity): RequestManager {
        return if (Util.isOnBackgroundThread) {
            getIns(activity.applicationContext)
        } else {
            assertNotDestroyed(activity)
            val fm = activity.supportFragmentManager
            supportFragmentGet(
                    activity, fm,  /*parentHint=*/null, isActivityVisible(activity))
        }
    }

    operator fun get(activity: Activity): RequestManager {
        return if (Util.isOnBackgroundThread) {
            getIns(activity.applicationContext)
        } else {
            assertNotDestroyed(activity)
            val fm = activity.fragmentManager
            fragmentGet(
                    activity, fm,  /*parentHint=*/null, isActivityVisible(activity))
        }
    }

    private fun fragmentGet(context: Context,
                            fm: android.app.FragmentManager,
                            parentHint: Fragment?,
                            isParentVisible: Boolean): RequestManager {
        val current = getRequestManagerFragment(fm, parentHint, isParentVisible)
        var requestManager = current.requestManager
        if (requestManager == null) {
            val army = Army.getIns(context)
            requestManager = factory.build(
                    army, current.glideLifecycle, context)
            current.requestManager = requestManager
        }
        return requestManager
    }

    private fun supportFragmentGet(
            context: Context,
            fm: FragmentManager,
            parentHint: androidx.fragment.app.Fragment?,
            isParentVisible: Boolean): RequestManager {
        val current = getSupportRequestManagerFragment(fm, parentHint, isParentVisible)
        var requestManager = current.requestManager
        if (requestManager == null) {
            val army = Army.getIns(context)
            requestManager = factory.build(
                    army, current.armyLifecycle, context)
            current.requestManager = requestManager
        }
        return requestManager
    }

    fun getSupportRequestManagerFragment(activity: FragmentActivity): SupportRequestManagerFragment {
        return getSupportRequestManagerFragment(
                activity.supportFragmentManager,  /*parentHint=*/null, isActivityVisible(activity))
    }

    fun getRequestManagerFragment(activity: Activity): RequestManagerFragment {
        return getRequestManagerFragment(
                activity.fragmentManager,  /*parentHint=*/null, isActivityVisible(activity))
    }

    private fun getRequestManagerFragment(
            fm: android.app.FragmentManager,
            parentHint: Fragment?,
            isParentVisible: Boolean): RequestManagerFragment {
        var current: RequestManagerFragment? = fm.findFragmentByTag(FRAGMENT_TAG) as RequestManagerFragment
        if (current == null) {
            current = pendingRequestManagerFragments[fm]
            if (current == null) {
                current = RequestManagerFragment()
                current.setParentFragmentHint(parentHint)
                if (isParentVisible) {
                    current.glideLifecycle.onStart()
                }
                pendingRequestManagerFragments[fm] = current
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss()
                handler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget()
            }
        }
        return current
    }

    private fun getSupportRequestManagerFragment(
            fm: FragmentManager, parentHint: androidx.fragment.app.Fragment?, isParentVisible: Boolean): SupportRequestManagerFragment {
        var current = fm.findFragmentByTag(FRAGMENT_TAG) as SupportRequestManagerFragment?
        if (current == null) {
            current = pendingSupportRequestManagerFragments[fm]
            if (current == null) {
                current = SupportRequestManagerFragment()
                current.setParentFragmentHint(parentHint)
                if (isParentVisible) {
                    current.armyLifecycle.onStart()
                }
                pendingSupportRequestManagerFragments[fm] = current
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss()
                handler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, fm).sendToTarget()
            }
        }
        return current
    }

    private fun getApplicationManager(context: Context): RequestManager {
        // Either an application context or we're on a background thread.
        if (applicationManager == null) {
            synchronized(this) {
                if (applicationManager == null) {
                    val army = Army.getIns(context.applicationContext)
                    applicationManager = factory.build(
                            army!!,
                            ApplicationLifecycle(),
                            context.applicationContext)
                }
            }
        }
        return applicationManager!!
    }

    /**
     * Used internally to create [RequestManager]s.
     */
    interface RequestManagerFactory {
        fun build(
                army: Army,
                lifecycle: Lifecycle,
                context: Context): RequestManager
    }

    companion object {
        private const val ID_REMOVE_FRAGMENT_MANAGER = 1
        private const val ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2
        private const val TAG = "RMRetriever"
        const val FRAGMENT_TAG = "com.rolan.army.manager"
        private fun isActivityVisible(activity: Activity): Boolean {
            // This is a poor heuristic, but it's about all we have. We'd rather err on the side of visible
            // and start requests than on the side of invisible and ignore valid requests.
            return !activity.isFinishing
        }

        private fun assertNotDestroyed(activity: Activity) {
            require(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed)) { "You cannot start a load for a destroyed activity" }
        }

        private val DEFAULT_FACTORY: RequestManagerFactory = object : RequestManagerFactory {
            override fun build(army: Army, lifecycle: Lifecycle, context: Context): RequestManager {
                return RequestManager(army, lifecycle, context)
            }
        }
    }

}