package com.legend.common.util;
import android.graphics.Color;
public class ColorUtil {
    //颜色转16进制
    public static String Color_16(int color) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("#");
        stringBuffer.append(Integer.toHexString(Color.alpha(color)));
        stringBuffer.append(Integer.toHexString(Color.red(color)));
        stringBuffer.append(Integer.toHexString(Color.green(color)));
        stringBuffer.append(Integer.toHexString(Color.blue(color)));
        return stringBuffer.toString();
    }

    public static String Color_16_NoAlpha(int color) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("#");
        stringBuffer.append(Integer.toHexString(Color.red(color)));
        stringBuffer.append(Integer.toHexString(Color.green(color)));
        stringBuffer.append(Integer.toHexString(Color.blue(color)));
        return stringBuffer.toString();
    }
}
