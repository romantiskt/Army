package com.rolan.army;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Rolan.Wang on 1/28/21.6:16 PM
 * 描述：
 */
public class RequestManager implements LifecycleListener {
    protected final Army army;
    protected final Context context;
    final Lifecycle lifecycle;
    private final RequestManagerTreeNode treeNode;
    private final RequestTracker requestTracker;
    private final CopyOnWriteArrayList<RequestListener> defaultRequestListeners;
    private final Runnable addSelfToLifecycle = new Runnable() {
        @Override
        public void run() {
            lifecycle.addListener(RequestManager.this);
        }
    };
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public RequestManager(
            @NonNull Army army, @NonNull Lifecycle lifecycle,
            @NonNull RequestManagerTreeNode treeNode, @NonNull Context context) {
        this.army = army;
        this.context = context;
        this.lifecycle = lifecycle;
        this.treeNode = treeNode;
        this.requestTracker=new RequestTracker();
        if (Util.isOnBackgroundThread()) {
            mainHandler.post(addSelfToLifecycle);
        } else {
            lifecycle.addListener(this);
        }
        defaultRequestListeners =
                new CopyOnWriteArrayList<>(army.getArmyContext().getDefaultRequestListeners());

        army.registerRequestManager(this);
    }

    @Override
    public void onDestroy() {
        requestTracker.clearRequests();
        lifecycle.removeListener(this);
        mainHandler.removeCallbacks(addSelfToLifecycle);
        army.unregisterRequestManager(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    public void cancel(@Nullable String taskName){
        army.getArmyContext().getEngine().release(taskName);
    }

    public RequestBuilder load(@Nullable String taskName) {
        return new RequestBuilder(army,this,context).task(taskName);
    }

    public RequestManager addDefaultRequestListener(RequestListener requestListener) {
        defaultRequestListeners.add(requestListener);
        return this;
    }

    public List<RequestListener> getDefaultRequestListeners() {
        return defaultRequestListeners;
    }

    public void track(Request request) {
        requestTracker.runRequest(request);
    }
}
