package com.rolan.army;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolan.Wang on 1/28/21.5:41 PM
 * 描述：
 */
public class Army {
    private static volatile Army army;
    private static volatile boolean isInitializing;
    private final ArmyContext armyContext;
    private Engine engine;
    private final List<RequestManager> managers = new ArrayList<>();
    private final RequestManagerRetriever requestManagerRetriever;
    public Army(@NonNull Context context,
                @NonNull List<RequestListener> defaultRequestListeners,
                RequestManagerRetriever requestManagerRetriever,
                Engine engine) {
        armyContext =
                new ArmyContext(context, defaultRequestListeners,engine);
        this.requestManagerRetriever = requestManagerRetriever;
        this.engine=engine;
    }

    @NonNull
    ArmyContext getArmyContext() {
        return armyContext;
    }

    public Context getContext() {
        return armyContext.getBaseContext();
    }

    public static Army get(@NonNull Context context) {
        if (army == null) {
            synchronized (Army.class) {
                if (army == null) {
                    checkAndInitializeArmy(context);
                }
            }
        }
        return army;
    }

    public static void openLog(){
        ArmyLog.openDebug();
    }

    @NonNull
    public static RequestManager with(@NonNull Context context) {
        return getRetriever(context).get(context);
    }

    private static RequestManagerRetriever getRetriever(@Nullable Context context) {
        return Army.get(context).getRequestManagerRetriever();
    }

    private static void checkAndInitializeArmy(@NonNull Context context) {
        if (isInitializing) {
            return;
        }
        isInitializing = true;
        initializeArmy(context, new ArmyBuilder());
        isInitializing = false;
    }

    private static void initializeArmy(Context context, ArmyBuilder builder) {
        Context applicationContext = context.getApplicationContext();
        Army army = builder.builder(applicationContext);
        Army.army = army;
    }

    public RequestManagerRetriever getRequestManagerRetriever() {
        return requestManagerRetriever;
    }

    void registerRequestManager(RequestManager requestManager) {
        synchronized (managers) {
            if (managers.contains(requestManager)) {
                throw new IllegalStateException("Cannot register already registered manager");
            }
            managers.add(requestManager);
        }
    }

    void unregisterRequestManager(RequestManager requestManager) {
        synchronized (managers) {
            if (!managers.contains(requestManager)) {
                throw new IllegalStateException("Cannot unregister not yet registered manager");
            }
            managers.remove(requestManager);
        }
    }

    public void clear(){
        engine.clearCache();
    }
}
