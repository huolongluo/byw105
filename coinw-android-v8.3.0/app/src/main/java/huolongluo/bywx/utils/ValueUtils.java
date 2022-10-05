package huolongluo.bywx.utils;
import android.text.TextUtils;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import huolongluo.byw.util.noru.NorUtils;
public class ValueUtils {
    public static String getValue(String val, int place) {
        if (TextUtils.isEmpty(val)) {
            return val;
        }
        return NorUtils.NumberFormat(place).format(DoubleUtils.parseDouble(val));
    }

    public static String getValue(String val) {
        return getValue(val, 5);
    }

    public static String getValue(Float val) {
        return NorUtils.NumberFormat(5).format(Double.valueOf(val));
    }

    public static String getValue(Double val) {
        return NorUtils.NumberFormat(5).format(val);
    }

    public static String getString(String val) {
        if (TextUtils.isEmpty(val)) {
            return "";
        }
        return val;
    }

    public static String getString(String val, String defVal) {
        if (TextUtils.isEmpty(val)) {
            return defVal;
        }
        return val;
    }

    public static Float getFloat(String val) {
        try {
            return Float.parseFloat(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static Float getFloat(Double val) {
        try {
            return val == null ? null : val.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static Long getLong(String val) {
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
    public static int getInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getFormattedValue(float value) {
        String pattern;
        if (value > 10000f) {
            pattern = "#0.0";
        } else if (value > 1000f) {
            pattern = "#0.00";
        } else if (value > 100f) {
            pattern = "#0.000";
        } else if (value > 10f) {
            pattern = "#0.0000";
        } else {
            pattern = "#0.00000";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    public static void setValue(TextView tv, Double val) {
        if (tv == null || val == null) {
            return;
        }
        setValue(tv, getValue(val));
    }

    public static void setValue(TextView tv, String val) {
        if (tv == null) {
            return;
        }
        tv.setText(val == null ? "" : val);
    }
}
