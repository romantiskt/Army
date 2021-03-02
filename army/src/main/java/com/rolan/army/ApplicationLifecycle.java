package com.rolan.army;

import androidx.annotation.NonNull;

/**
 * Created by Rolan.Wang on 1/28/21.6:58 PM
 * 描述：
 */
class ApplicationLifecycle implements Lifecycle {
    @Override
    public void addListener(@NonNull LifecycleListener listener) {
        listener.onStart();
    }

    @Override
    public void removeListener(@NonNull LifecycleListener listener) {
        // Do nothing.
    }
}