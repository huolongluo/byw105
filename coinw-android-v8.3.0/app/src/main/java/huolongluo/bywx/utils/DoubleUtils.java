package huolongluo.bywx.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

import huolongluo.byw.log.Logger;

public class DoubleUtils {

    public static double parseDouble(String data) {
        if (TextUtils.isEmpty(data)) {
            return 0.00000d;
        }
        try {
            return Double.parseDouble(data);
        } catch (Exception e) {
            //TODO 记录服务器返回数据的堆栈日志
            e.printStackTrace();
        }
        return 0.00000d;
    }

    public static String getValue(DecimalFormat format, double data) {
        if (format != null) {
            try {
                return format.format(data);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
        return "";
    }
}
