package com.rolan.army;

/**
 * Created by Rolan.Wang on 1/29/21.2:38 PM
 * 描述：
 */
public interface Request {

    boolean isComplete();

    void recycle();

    void begin();


    void clear();
}
