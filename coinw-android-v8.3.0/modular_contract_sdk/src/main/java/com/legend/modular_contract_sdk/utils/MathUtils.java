package com.legend.modular_contract_sdk.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
/**
 * Created by zj on 2018/3/7.
 */
public class MathUtils {
    private static final int DEF_DIV_SCALE = 8;
    private static final double DOUBLE_GWEI = 1000000000;
    public static final long LONG_GWEI = 1000000000;

    private MathUtils() {

    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1
     *            被加数
     * @param v2
     *            加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(String v1, String v2) {
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return 0;
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    public static String add2String(String v1, String v2) {
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return "0";
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }
    /**
     * 提供精确的减法运算。
     *
     * @param v1
     *            被减数
     * @param v2
     *            减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double sub(String v1, String v2) {
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return 0;
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1
     *            被乘数
     * @param v2
     *            乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(String v1, String v2) {
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return 0;
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }
    public static double div(String v1, String v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @param scale
     *            表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }

        if (v2 == 0.0) {
            return 0.0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public static double div(String v1, String v2, int scale) {
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return 0;
        }

        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }

        if (Double.parseDouble(v2) == 0.0) {
            return 0.0;
        }

        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public static double round(String v, int scale) {
        if (TextUtils.isEmpty(v)) {
            return 0;
        }

        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public static double round(String v) {
        if (TextUtils.isEmpty(v)) {
            return 0;
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, 8, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public static String Long2RDoubleString(long l, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }

        double v = (double)l;
        double r = round(div(v, DOUBLE_GWEI), scale);
        return Double.toString(r);
    }

    public static double Long2RDouble(long l, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }

        double v = (double)l;
        double r = round(div(v, DOUBLE_GWEI), scale);
        return r;
    }
    public static boolean stringToBoolean(String str){
        boolean temp=false;
        try {
            temp=Boolean.parseBoolean(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return temp;
    }
}