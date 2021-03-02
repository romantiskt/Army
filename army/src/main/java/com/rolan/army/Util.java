package com.rolan.army;

import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rolan.Wang on 1/28/21.6:56 PM
 * 描述：
 */
public class Util {
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    public static <T> List<T> getSnapshot(@NonNull Collection<T> other) {
        List<T> result = new ArrayList<>(other.size());
        for (T item : other) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
}
