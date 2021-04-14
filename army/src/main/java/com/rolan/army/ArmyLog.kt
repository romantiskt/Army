package com.rolan.army

import android.util.Log

/**
 * Created by Rolan.Wang on 2/3/21.3:37 PM
 * global log config
 */
internal object ArmyLog {
    private const val TAG = "army"
    private var DEBUG = false
    fun openDebug() {
        DEBUG = true
    }

    fun d(msg: String?) {
        if (DEBUG) {
            Log.d(TAG, msg!!)
        }
    }
}