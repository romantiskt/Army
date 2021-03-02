package com.rolan.army;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolan.Wang on 1/28/21.6:43 PM
 * 描述：
 */
public class RequestBuilder extends BaseRequestOptions<RequestBuilder>{
    private final Context context;
    private final RequestManager requestManager;
    private final Army army;
    private final ArmyContext armyContext;

    private List<RequestListener> requestListeners;

    public RequestBuilder(Army army, RequestManager requestManager,Context context ) {
        this.context = context;
        this.requestManager = requestManager;
        this.army = army;
        this.armyContext=army.getArmyContext();
        initRequestListeners(requestManager.getDefaultRequestListeners());
    }

    private void initRequestListeners(List<RequestListener> requestListeners) {
        for (RequestListener listener : requestListeners) {
            addListener((RequestListener) listener);
        }
    }

    private Request buildRequest(){
        return SingleRequest.obtain(context,armyContext.getEngine(),armyContext,requestListeners,this);
    }



    public void post(){
        Request request = buildRequest();
        requestManager.track(request);
    }


    private RequestBuilder addListener(
            @Nullable RequestListener requestListener) {
        if (requestListener != null) {
            if (this.requestListeners == null) {
                this.requestListeners = new ArrayList<>();
            }
            this.requestListeners.add(requestListener);
        }
        return this;
    }

    public RequestBuilder listener(
            @Nullable RequestListener requestListener) {
        this.requestListeners = null;
        return addListener(requestListener);
    }
}
