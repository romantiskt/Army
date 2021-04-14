package com.rolan.army

import android.os.Looper
import java.util.*

/**
 * Created by Rolan.Wang on 1/28/21.6:56 PM
 */
object Util {
    val isOnMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()
    val isOnBackgroundThread: Boolean
        get() = !isOnMainThread

    @JvmStatic
    fun <T> getSnapshot(other: Collection<T>): List<T> {
        val result: MutableList<T> = ArrayList(other.size)
        for (item in other) {
            if (item != null) {
                result.add(item)
            }
        }
        return result
    }
}