package com.legend.modular_contract_sdk.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 剪贴板工具类
 * Created by Spencer on 7/18/16.
 */
public final class McClipboardUtils {

    private McClipboardUtils() {
    }

    /**
     * 复制指定文本
     */
    public static void copyingToClipboard(Context context, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(content, content);
        clipboardManager.setPrimaryClip(clip);
    }

    public static String readStringFromClipboard(Context context) {
        return readStringFromClipboard(context, 0);
    }

    public static String readStringFromClipboard(Context context, int index) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClip = clipboardManager.getPrimaryClip();
        if (primaryClip != null) {
            ClipData.Item primaryClipItem = primaryClip.getItemAt(index);

            if (primaryClipItem.getText() != null) {
                return primaryClipItem.getText().toString();
            } else {
                return "";
            }
        } else {
            return "";
        }

    }
    public static final void copyText(Context context, CharSequence text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("copy",text));
        }
    }

    public static final String getPasteText(Context context) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData=cm.getPrimaryClip();
            ClipData.Item item=clipData.getItemAt(0);
            return item.getText().toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
