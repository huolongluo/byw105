package com.github.mikephil.charting.utils;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
public class ValueUtils {
    public static String getValue(String val) {
        if (TextUtils.isEmpty(val)) {
            return val;
        }
        BigDecimal bd = new BigDecimal(val);
        return bd.toPlainString();
    }

    public static String getValue(Float val) {
        BigDecimal bd = new BigDecimal(val);
        return bd.toPlainString();
    }

    public static String getValue(Float val, int scale) {
        StringBuffer s = new StringBuffer();
        s.append("0");
        if (scale > 0) {
            s.append(".");
        }
        for (int i = 0; i < scale; i++) {
            s.append("0");
        }
        DecimalFormat df = new DecimalFormat(s.toString());
        return df.format(val);
    }

    public static String getValue(Double val, int scale) {
        StringBuffer s = new StringBuffer();
        s.append("0");
        if (scale > 0) {
            s.append(".");
        }
        for (int i = 0; i < scale; i++) {
            s.append("0");
        }
        DecimalFormat df = new DecimalFormat(s.toString());
        return df.format(val);
    }
}
