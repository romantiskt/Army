package com.rolan.army;

import android.app.Activity;
import android.content.Context;

import java.util.List;

/**
 * Created by Rolan.Wang on 1/29/21.2:39 PM
 * 描述：
 */
public class SingleRequest implements Request, ResourceCallback {
    private Engine engine;
    private ArmyContext armyContext;
    private Context context;
    private List<RequestListener> requestListeners;
    private BaseRequestOptions<?> requestOptions;

    public SingleRequest(Context context, Engine engine, ArmyContext armyContext, List<RequestListener> requestListeners, BaseRequestOptions<?> requestOptions) {
        this.context = context;
        this.engine = engine;
        this.armyContext = armyContext;
        this.requestListeners = requestListeners;
        this.requestOptions=requestOptions;
    }

    public static SingleRequest obtain(Context context,
                                       Engine engine,
                                       ArmyContext armyContext,
                                       List<RequestListener> requestListeners,
                                       BaseRequestOptions<?> requestOptions) {
        return new SingleRequest(context, engine, armyContext, requestListeners,requestOptions);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void recycle() {

    }

    @Override
    public void begin() {
        engine.post(requestOptions.getTaskName(),requestOptions.getPriority(),this);
    }

    @Override
    public void clear() {
        engine.release(requestOptions.getTaskName());
    }

    @Override
    public void canShow() {
        if (requestListeners != null&&context!=null) {
            if (context instanceof Activity){
                if(((Activity) context).isFinishing()){
                    ArmyLog.d("页面已经销毁，不再回调");
                    return;
                }
            }
            for (RequestListener listener : requestListeners) {
                listener.canShow();
            }
        }
    }
}
