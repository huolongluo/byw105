package com.legend.modular_contract_sdk.utils;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

/**
 * Created by Spencer on 5/28/18.
 */
public final class SoftKeyboardUtils {

    public static boolean isShown(Context context) {
        InputMethodManager inputManager = getInputManager(context);
        return inputManager != null && inputManager.isAcceptingText();
    }

    @Nullable
    private static InputMethodManager getInputManager(Context context) {
        if (context == null) {
            return null;
        }

        return (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
    }

    public static void showSoftKeyboard(final View view) {
        showSoftKeyboard(view, 0, false);
    }

    public static void showSoftKeyboard(final View view, final long delayedTime, final boolean force) {
        if (view == null) {
            return;
        }

        final InputMethodManager inputMethodManager = getInputManager(view.getContext());
        if (inputMethodManager == null) {
            return;
        }

        view.requestFocus();
        view.setFocusable(true);

        HandlerUtils.mainThreadPostDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (force) {
                            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                        } else {
                            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                },
                delayedTime
        );
    }

    public static void showSoftKeyboard(final View view, final long delayedTime) {
        showSoftKeyboard(view, delayedTime, false);
    }

    public static void hideSoftKeyboard(final View view) {
        hideSoftKeyboard(view, 0);
    }

    public static void hideSoftKeyboard(final View view, final long delayedTime) {
        if (view == null) {
            return;
        }

        final InputMethodManager inputMethodManager = getInputManager(view.getContext());
        if (inputMethodManager == null) {
            return;
        }

        HandlerUtils.mainThreadPostDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, delayedTime);
    }

    private SoftKeyboardUtils() {
        // no instance.
    }
}
