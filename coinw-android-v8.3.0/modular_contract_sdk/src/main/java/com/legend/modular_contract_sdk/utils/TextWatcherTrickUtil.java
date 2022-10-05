package com.legend.modular_contract_sdk.utils;

import android.text.Editable;
import android.text.TextUtils;

public class TextWatcherTrickUtil {

    /**
     * 小数点左边补0
     * <p>
     * {@link android.text.TextWatcher#afterTextChanged(Editable)}
     */
    public static void fillZeroBeforePoint(Editable s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (s.charAt(0) == '.') {
            s.insert(0, "0");
        }
    }

    /**
     * 第一位禁止输入0
     * <p>
     * {@link android.text.TextWatcher#afterTextChanged(Editable)}
     */
    public static void noFirstZero(Editable s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (s.length() == 1 && s.charAt(0) == '0') {
            // s.replace(0, 1, "");
            s.delete(0, 1);
        }
    }

    /**
     * 限制数字整数、小数最长位数
     */
    public static void adjustLength(Editable s, int integerMaxLength, int decimalMaxLength) {
        if (TextUtils.isEmpty(s) || integerMaxLength < 0 || decimalMaxLength < 0) {
            return;
        }
        String str = s.toString();
        String[] split = str.split("\\.");
        if (split.length > 0) {
            int l = split[0].length();
            if (l > integerMaxLength) {
                s.replace(integerMaxLength, str.length(), "");
                return;
            }
        }
        if (split.length > 1) {
            int l = split[1].length();
            if (l > decimalMaxLength) {
                s.replace(split[0].length() + 1 + decimalMaxLength, str.length(), "");
                return;
            }
        }
    }
}
