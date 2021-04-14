package com.rolan.army

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.7:04 PM
 */
class SupportRequestManagerFragment  constructor(val armyLifecycle: ActivityFragmentLifecycle) : Fragment() {
    var requestManager: RequestManager? = null
    private var parentFragmentHint: Fragment? = null
    private var rootRequestManagerFragment: SupportRequestManagerFragment? = null
    private val childRequestManagerFragments: MutableSet<SupportRequestManagerFragment> = HashSet()

    constructor() : this(ActivityFragmentLifecycle()) {}

    override fun onStart() {
        super.onStart()
        armyLifecycle.onStart()
    }

    override fun onStop() {
        super.onStop()
        armyLifecycle.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        armyLifecycle.onDestroy()
        unregisterFragmentWithRoot()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            registerFragmentWithRoot(activity!!)
        } catch (e: IllegalStateException) {
            // OnAttach can be called after the activity is destroyed, see #497.
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to register fragment with root", e)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        parentFragmentHint = null
        unregisterFragmentWithRoot()
    }

    private val parentFragmentUsingHint: Fragment
        private get() {
            val fragment = parentFragment
            return fragment ?: parentFragmentHint!!
        }

    fun setParentFragmentHint(parentFragmentHint: Fragment?) {
        this.parentFragmentHint = parentFragmentHint
        if (parentFragmentHint != null && parentFragmentHint.activity != null) {
            registerFragmentWithRoot(parentFragmentHint.activity!!)
        }
    }

    private fun registerFragmentWithRoot(activity: FragmentActivity) {
        unregisterFragmentWithRoot()
        rootRequestManagerFragment = Army.getIns(activity)?.requestManagerRetriever?.getSupportRequestManagerFragment(activity)
        if (!equals(rootRequestManagerFragment)) {
            rootRequestManagerFragment!!.addChildRequestManagerFragment(this)
        }
    }

    private fun unregisterFragmentWithRoot() {
        if (rootRequestManagerFragment != null) {
            rootRequestManagerFragment!!.removeChildRequestManagerFragment(this)
            rootRequestManagerFragment = null
        }
    }

    private fun addChildRequestManagerFragment(child: SupportRequestManagerFragment) {
        childRequestManagerFragments.add(child)
    }

    private fun removeChildRequestManagerFragment(child: SupportRequestManagerFragment) {
        childRequestManagerFragments.remove(child)
    }

    companion object {
        private const val TAG = "SupportRMFragment"
    }
}