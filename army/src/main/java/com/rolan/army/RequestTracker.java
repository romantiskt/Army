package com.rolan.army;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Rolan.Wang on 1/29/21.2:54 PM
 * 描述：
 */
public class RequestTracker {

    private final Set<Request> requests =
            Collections.newSetFromMap(new WeakHashMap<Request, Boolean>());

    public void runRequest(@NonNull Request request) {
        requests.add(request);
        request.begin();
    }


    public void clearRequests() {
        for (Request request : Util.getSnapshot(requests)) {
            // It's unsafe to recycle the Request here because we don't know who might else have a
            // reference to it.
            clearRemoveAndMaybeRecycle(request, /*isSafeToRecycle=*/ false);
        }
    }

    private boolean clearRemoveAndMaybeRecycle(@Nullable Request request, boolean isSafeToRecycle) {
        if (request == null) {
            // If the Request is null, the request is already cleared and we don't need to search further
            // for its owner.
            return true;
        }
        boolean isOwnedByUs = requests.remove(request);
        // Avoid short circuiting.
        if (isOwnedByUs) {
            request.clear();
            if (isSafeToRecycle) {
                request.recycle();
            }
        }
        return isOwnedByUs;
    }

}
