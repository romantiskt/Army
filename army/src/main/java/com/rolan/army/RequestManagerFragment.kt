package com.rolan.army

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.os.Build
import android.util.Log
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.7:04 PM
 */
class RequestManagerFragment @SuppressLint("ValidFragment") internal constructor(val glideLifecycle: ActivityFragmentLifecycle) : Fragment() {
    private val childRequestManagerFragments: MutableSet<RequestManagerFragment> = HashSet()
    var requestManager: RequestManager? = null
    private var rootRequestManagerFragment: RequestManagerFragment? = null
    private var parentFragmentHint: Fragment? = null

    constructor() : this(ActivityFragmentLifecycle()) {}

    private fun addChildRequestManagerFragment(child: RequestManagerFragment) {
        childRequestManagerFragments.add(child)
    }

    private fun removeChildRequestManagerFragment(child: RequestManagerFragment) {
        childRequestManagerFragments.remove(child)
    }

    fun setParentFragmentHint(parentFragmentHint: Fragment?) {
        this.parentFragmentHint = parentFragmentHint
        if (parentFragmentHint != null && parentFragmentHint.activity != null) {
            registerFragmentWithRoot(parentFragmentHint.activity)
        }
    }

    @get:TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private val parentFragmentUsingHint: Fragment?
        private get() {
            val fragment: Fragment? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                parentFragment
            } else {
                null
            }
            return fragment ?: parentFragmentHint
        }

    private fun registerFragmentWithRoot(activity: Activity) {
        unregisterFragmentWithRoot()
        rootRequestManagerFragment = Army.getIns(activity)?.requestManagerRetriever?.getRequestManagerFragment(activity)
        if (!equals(rootRequestManagerFragment)) {
            rootRequestManagerFragment?.addChildRequestManagerFragment(this)
        }
    }

    private fun unregisterFragmentWithRoot() {
        if (rootRequestManagerFragment != null) {
            rootRequestManagerFragment!!.removeChildRequestManagerFragment(this)
            rootRequestManagerFragment = null
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            registerFragmentWithRoot(activity)
        } catch (e: IllegalStateException) {
            // OnAttach can be called after the activity is destroyed, see #497.
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to register fragment with root", e)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        unregisterFragmentWithRoot()
    }

    override fun onStart() {
        super.onStart()
        glideLifecycle.onStart()
    }

    override fun onStop() {
        super.onStop()
        glideLifecycle.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        glideLifecycle.onDestroy()
        unregisterFragmentWithRoot()
    }

    override fun toString(): String {
        return super.toString() + "{parent=" + parentFragmentUsingHint + "}"
    }

    companion object {
        private const val TAG = "RMFragment"
    }
}