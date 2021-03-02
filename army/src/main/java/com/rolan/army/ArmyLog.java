package com.rolan.army;

import android.util.Log;

/**
 * Created by Rolan.Wang on 2/3/21.3:37 PM
 * 描述：
 */
class ArmyLog {
    private final static String TAG = "army";

    private static boolean DEBUG = false;

    static void openDebug() {
        DEBUG = true;
    }

    static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
