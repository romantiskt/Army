package com.rolan.army

import java.util.*

/**
 * Created by Rolan.Wang on 1/29/21.2:54 PM
 */
class RequestTracker {
    private val requests = Collections.newSetFromMap(WeakHashMap<Request, Boolean>())
    fun runRequest(request: Request) {
        requests.add(request)
        request.begin()
    }

    fun clearRequests() {
        for (request in Util.getSnapshot(requests)) {
            clearRemoveAndMaybeRecycle(request, false)
        }
    }

    private fun clearRemoveAndMaybeRecycle(request: Request?, isSafeToRecycle: Boolean): Boolean {
        if (request == null) {
            return true
        }
        val isOwnedByUs = requests.remove(request)
        // Avoid short circuiting.
        if (isOwnedByUs) {
            request.clear()
            if (isSafeToRecycle) {
                request.recycle()
            }
        }
        return isOwnedByUs
    }
}