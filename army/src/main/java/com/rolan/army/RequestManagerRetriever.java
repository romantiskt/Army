package com.rolan.army;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rolan.Wang on 1/28/21.6:04 PM
 * 描述：
 */
public class RequestManagerRetriever implements Handler.Callback {

    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
    private static final String TAG = "RMRetriever";
    private final Handler handler;
    private final RequestManagerFactory factory;
    private volatile RequestManager applicationManager;
    final Map<FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments =
            new HashMap<>();
    static final String FRAGMENT_TAG = "com.rolan.army.manager";

    public RequestManagerRetriever() {
        this.factory = DEFAULT_FACTORY;
        handler = new Handler(Looper.getMainLooper(), this /* Callback */);
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case ID_REMOVE_SUPPORT_FRAGMENT_MANAGER:
                FragmentManager supportFm = (FragmentManager) message.obj;
                key = supportFm;
                removed = pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }


    public RequestManager get(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                return get((Activity) context);
            } else if (context instanceof ContextWrapper) {
                return get(((ContextWrapper) context).getBaseContext());
            }
        }

        return getApplicationManager(context);
    }

    public RequestManager get(@NonNull FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        } else {
            assertNotDestroyed(activity);
            FragmentManager fm = activity.getSupportFragmentManager();
            return supportFragmentGet(
                    activity, fm, /*parentHint=*/ null, isActivityVisible(activity));
        }
    }


    private RequestManager supportFragmentGet(
            @NonNull Context context,
            @NonNull FragmentManager fm,
            @Nullable Fragment parentHint,
            boolean isParentVisible) {
        SupportRequestManagerFragment current =
                getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            Army army = Army.get(context);
            requestManager =
                    factory.build(
                            army, current.getArmyLifecycle(), current.getRequestManagerTreeNode(), context);
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

    SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentActivity activity) {
        return getSupportRequestManagerFragment(
                activity.getSupportFragmentManager(), /*parentHint=*/ null, isActivityVisible(activity));
    }

    private SupportRequestManagerFragment getSupportRequestManagerFragment(
            @NonNull final FragmentManager fm, @Nullable Fragment parentHint, boolean isParentVisible) {
        SupportRequestManagerFragment current =
                (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingSupportRequestManagerFragments.get(fm);
            if (current == null) {
                current = new SupportRequestManagerFragment();
                current.setParentFragmentHint(parentHint);
                if (isParentVisible) {
                    current.getArmyLifecycle().onStart();
                }
                pendingSupportRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                handler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return current;
    }

    private static boolean isActivityVisible(Activity activity) {
        // This is a poor heuristic, but it's about all we have. We'd rather err on the side of visible
        // and start requests than on the side of invisible and ignore valid requests.
        return !activity.isFinishing();
    }

    private static void assertNotDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    private RequestManager getApplicationManager(@NonNull Context context) {
        // Either an application context or we're on a background thread.
        if (applicationManager == null) {
            synchronized (this) {
                if (applicationManager == null) {
                    Army army = Army.get(context.getApplicationContext());
                    applicationManager =
                            factory.build(
                                    army,
                                    new ApplicationLifecycle(),
                                    new EmptyRequestManagerTreeNode(),
                                    context.getApplicationContext());
                }
            }
        }

        return applicationManager;
    }

    /**
     * Used internally to create {@link RequestManager}s.
     */
    public interface RequestManagerFactory {
        @NonNull
        RequestManager build(
                @NonNull Army army,
                @NonNull Lifecycle lifecycle,
                @NonNull RequestManagerTreeNode requestManagerTreeNode,
                @NonNull Context context);
    }

    private static final RequestManagerFactory DEFAULT_FACTORY = new RequestManagerFactory() {
        @NonNull
        @Override
        public RequestManager build(@NonNull Army army, @NonNull Lifecycle lifecycle,
                                    @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context) {
            return new RequestManager(army, lifecycle, requestManagerTreeNode, context);
        }
    };
}
