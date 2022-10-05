package com.legend.modular_contract_sdk.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Spencer on 5/2/18.
 */
public final class HandlerUtils {

    private static Handler INSTANCE;

    public static void mainThreadPost(Runnable runnable) {
        getINSTANCE().post(runnable);
    }

    private static Handler getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (HandlerUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Handler(Looper.getMainLooper());
                }
            }
        }
        return INSTANCE;
    }

    public static void mainThreadPostDelayed(Runnable runnable, long delayMillis) {
        getINSTANCE().postDelayed(runnable, delayMillis);
    }

    private HandlerUtils() {
        // No instance.
    }

}
