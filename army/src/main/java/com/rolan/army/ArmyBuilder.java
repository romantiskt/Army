package com.rolan.army;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by Rolan.Wang on 1/28/21.5:49 PM
 * 描述：
 */
public final class ArmyBuilder {

    private List<RequestListener> defaultRequestListeners;
    private Engine engine;

    Army builder(@NonNull Context context){
        if (defaultRequestListeners == null) {
            defaultRequestListeners = Collections.emptyList();
        } else {
            defaultRequestListeners = Collections.unmodifiableList(defaultRequestListeners);
        }

        RequestManagerRetriever requestManagerRetriever =
                new RequestManagerRetriever();
        if (engine == null) {
            engine = new Engine();
        }
        return new Army(context,defaultRequestListeners,requestManagerRetriever,engine);
    }
}
