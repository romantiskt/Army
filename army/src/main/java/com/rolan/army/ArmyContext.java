package com.rolan.army;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by Rolan.Wang on 1/28/21.6:48 PM
 * 描述：
 */
class ArmyContext extends ContextWrapper {

    private final List<RequestListener> defaultRequestListeners;
    private final Engine engine;

    public ArmyContext( @NonNull Context context,
                       @NonNull List<RequestListener> defaultRequestListeners,
                        Engine engine) {
        super(context.getApplicationContext());
        this.defaultRequestListeners = defaultRequestListeners;
        this.engine=engine;
    }

    public List<RequestListener> getDefaultRequestListeners() {
        return defaultRequestListeners;
    }


    public Engine getEngine() {
        return engine;
    }
}
