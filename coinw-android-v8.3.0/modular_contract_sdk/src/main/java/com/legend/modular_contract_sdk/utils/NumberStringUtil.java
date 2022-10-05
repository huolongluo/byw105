package com.legend.modular_contract_sdk.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * 数字字符串工具类
 * Created by xtdhwl on 24/08/2017.
 */

public class NumberStringUtil {

    private static final boolean DEBUG = false;

    private final static String DF_BTC = "0.########";  //比特币展示格式,不填充0
    private final static String DF_BTC_FILL_ZERO = "0.00000000";  //比特币展示格式,填充0
    public final static int PRECISION_CNY = 2;     //人民币默认保留位数
    public final static int PRECISION_BTC = 8;     //BTC默认保留位数
    public final static int MAX_DIGITS = 32;     //最大小数位

    /**
     * The empty String {@code ""}.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * RoundingMode UP
     * 远离零方向舍入的舍入模式
     * <p>
     * RoundingMode DOWN
     * 向零方向舍入的舍入模式
     * <p>
     * RoundingMode HALF_UP
     * 4舍5入
     **/
    private static final RoundingMode ROUNDING_MODE_FOR_FORMAT_UTIL = RoundingMode.DOWN;

    /**
     * 格式化样式
     */
    public enum AmountStyle {

        /**
         * 数据:1
         * 小数:8
         * 结果:1.00000000
         * 格式化填充,使用逗号分隔
         */
        FillZero,

        /**
         * 数据:10000
         * 小数:8
         * 结果:10000
         * 格式化填充,不使用逗号分隔
         */
        FillZeroNoComma,

        /**
         * 数据:1
         * 小数:8
         * 结果:1
         * 格式化不填充,使用逗号分隔
         */
        NotFillZero,

        /**
         * 数据:1
         * 小数:8
         * 结果:1
         * 格式化不填充,不使用逗号分隔
         */
        NotFillZeroNoComma
    }
    public static String formatVolume(double volume) {
        if (volume > 1000) {
            return NumberStringUtil.formatAmount(volume / 1000,2)+ "K";
        } else {
            return NumberStringUtil.formatAmount(volume,2);
        }
    }
    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(String number, int digits) {
        try {
            return formatAmount(new BigDecimal(number), digits);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(double number, int digits) {
        try {
            return formatAmount(BigDecimal.valueOf(number), digits, AmountStyle.FillZero);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(BigDecimal number, int digits) {
        try {
            return formatAmount(number, digits, AmountStyle.FillZero);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(String number, int digits, AmountStyle style) {
        return formatAmount(number, digits, style, ROUNDING_MODE_FOR_FORMAT_UTIL);
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(String number, int digits, AmountStyle style, RoundingMode roundingMode) {
        try {
            return formatAmount(new BigDecimal(number), digits, style, roundingMode);
        } catch (Exception e) {

        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(double number, int digits, AmountStyle style) {
        return formatAmount(number, digits, style, ROUNDING_MODE_FOR_FORMAT_UTIL);
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(double number, int digits, AmountStyle style, RoundingMode roundingMode) {
        try {
            return formatAmount(BigDecimal.valueOf(number), digits, style, roundingMode);
        } catch (Exception e) {
//            Logger.e(e, e.getMessage());
        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(BigDecimal number, int digits, AmountStyle style) {
        return formatAmount(number, digits, style, ROUNDING_MODE_FOR_FORMAT_UTIL);
    }

    /**
     * 格式化金额
     *
     * @param number 金额
     * @param digits 小数位
     * @return
     */
    public static String formatAmount(BigDecimal number, int digits, AmountStyle style, RoundingMode roundingMode) {
        try {
            String df = createDF(digits, style);
            DecimalFormat decimalFormat = getDecimalInstance(df);
            decimalFormat.setRoundingMode(roundingMode);
            return decimalFormat.format(number);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        //TODO 修改为 "NaN"
        return "0";
    }

    /**
     * String转BigDecimal
     *
     * @param value
     * @param digits
     * @param style
     * @return
     * @throws ParseException
     */
    public static BigDecimal parseAmount(String value, int digits, AmountStyle style) throws ParseException {
        String df = createDF(digits, style);
        DecimalFormat formatter = getDecimalInstance(df);
        formatter.setParseBigDecimal(true);
        BigDecimal amount = (BigDecimal) formatter.parse(value);
        return amount;
    }

    /**
     * String转BigDecimal
     *
     * @param value
     * @return
     */
    public static BigDecimal parseAmount(String value) {
        BigDecimal result = new BigDecimal(value);
        return result;
    }

    /**
     * String转BigDecimal
     *
     * @param value
     * @param digits
     * @return
     */
    public static BigDecimal parseAmount(String value, int digits) {
        BigDecimal result = new BigDecimal(value);
        result = result.setScale(digits, ROUNDING_MODE_FOR_FORMAT_UTIL);
        return result;
    }

    /**
     * 获取double型小数位数
     *
     * @param value
     * @return
     */
    @Deprecated
    public static int getNumberDecimalDigits(double value) {
        try {
            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
            decimalFormat.applyPattern("0.################################");
            String valueStr = decimalFormat.format(value);
            return getNumberDecimalDigits(valueStr);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        return 0;
    }

    /**
     * 获取double型小数位数
     *
     * @param value
     * @return
     */
    public static int getNumberDecimalDigits(BigDecimal value) {
        try {
            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
            decimalFormat.applyPattern("0.################################");
            String valueStr = decimalFormat.format(value);
            return getNumberDecimalDigits(valueStr);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }
        return 0;
    }

    /**
     * 获取double型小数位数
     *
     * @param value
     * @return
     */
    public static int getNumberDecimalDigits(String value) {
        int digits = 0;
        try {
            String s = value;
            int indexOf = s.lastIndexOf(".");
            if (indexOf > 0) {
                digits = s.length() - 1 - indexOf;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digits;
    }

    private static String createDF(int digit, AmountStyle style) {
        StringBuilder df;//= new StringBuilder("#,##0.");
        if (style == AmountStyle.FillZero) {
            df = new StringBuilder("#,##0");
            if (digit > 0) {
                df.append(".");
                for (int i = 0; i < digit; i++) {
                    df.append("0");
                }
            }
        } else if (style == AmountStyle.FillZeroNoComma) {
            df = new StringBuilder("0");
            if (digit > 0) {
                df.append(".");
                for (int i = 0; i < digit; i++) {
                    df.append("0");
                }
            }
        } else if (style == AmountStyle.NotFillZero) {
            df = new StringBuilder("#,##0");
            if (digit > 0) {
                df.append(".");
                for (int i = 0; i < digit; i++) {
                    df.append("#");
                }
            }
        } else if (style == AmountStyle.NotFillZeroNoComma) {
            df = new StringBuilder("0");
            if (digit > 0) {
                df.append(".");
                for (int i = 0; i < digit; i++) {
                    df.append("#");
                }
            }
        } else {
            throw new IllegalArgumentException("不支持此样式:" + style);
        }
        if (DEBUG) {
            Logger.i("AmountStyle:" + df.toString());
        }
        return df.toString();
    }

    /**
     * 手机号敏感字段编码
     * 13012345678  -> 130****5678
     *
     * @param mobile
     * @return
     */
    public static String encoderMobile(String mobile) {
        if (null == mobile || mobile.length() < 3) {
            return mobile;
        }
        StringBuilder sb = new StringBuilder();
        if (!mobile.matches("^[0-9]*$")) {
            return mobile;
        }
        if (mobile.length() == 11) {
            return sb.append(mobile, 0, 3).append("****").
                    append(mobile.substring(mobile.length() - 4)).toString();
        }
        return sb.append(mobile, 0, 3).append("******").toString();
    }

    /**
     * 邮箱加密
     *
     * @param email
     * @return
     */
    public static String encoderEmail(String email) {
        if (null == email || !email.contains("@")) {
            return email;
        }
        String[] emailSplit = email.split("@");
        if (emailSplit.length == 2 && emailSplit[0].length() > 3) {
            return emailSplit[0].substring(0, 3) + "****@" + emailSplit[1];
        }

        return email;
    }

    /**
     * 身份证敏感字段编码
     *
     * @return
     */
    public static String encodeIdentityCard(String card) {
        if (card.replace(" ", "").length() == 15) { //15位身份证
            return encodeString(card, 3, 4);
        } else {                                  //18位身份证
            return encodeString(card, 3, 4);
        }
    }

    private static String encodeString(String str, int head, int tail) {
        //去掉空格
        int sumTail = str.replace(" ", "").length() - head - tail + head;
        StringBuffer desc = new StringBuffer();
        StringBuffer src = new StringBuffer(str);
        int count = 0;
        for (int i = 0; i < src.length(); i++) {
            String substring = src.substring(i, i + 1);
            if (substring != null && !substring.trim().equals("")) {
                count++;
                if (count > head && count <= sumTail) {
                    desc.append("*");
                    continue;
                }
            }
            desc.append(substring);
        }
        return desc.toString();
    }

    /**
     * 格式化计价单位
     * {@link #formatAmount(double, int)}
     *
     * @param number
     * @return
     */
    @Deprecated
    public static String formatAmount(double number) {
        return formatAmount(number, PRECISION_BTC);
    }

    /**
     * 格式化计价单位
     * {@link #formatAmount(BigDecimal, int)}
     *
     * @param number
     * @return
     */
    @Deprecated
    public static String formatAmount(BigDecimal number) {
        return formatAmount(number, PRECISION_BTC);
    }

    /**
     * 获取DecimalFormat，默认Locale使用ENGLISH，处理俄语小数点为逗号的情况
     *
     * @param pattern
     * @return
     */
    private static DecimalFormat getDecimalInstance(String pattern) {
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        decimalFormat.applyPattern(pattern);
        return decimalFormat;
    }

    @Deprecated
    public static BigDecimal parseBTCAmount(String from) throws ParseException {
        DecimalFormat formatter = getDecimalInstance(DF_BTC);
        formatter.setParseBigDecimal(true);
        BigDecimal amount = (BigDecimal) formatter.parse(from);
        return amount;
    }

    @Deprecated
    public static BigDecimal parseBTCAmountFillZero(String from) throws ParseException {
        DecimalFormat formatter = getDecimalInstance(DF_BTC_FILL_ZERO);
        formatter.setParseBigDecimal(true);
        BigDecimal amount = (BigDecimal) formatter.parse(from);
        return amount;
    }

    /**
     * 格式化人民币
     * 默认不使用逗号分隔，如需分隔，直接使用{@link #formatAmount(double, int, AmountStyle)}
     * TIPS:改动AmountStyle可能会引起EditText数字转换异常
     *
     * @param cny
     * @return
     */
    public static String formatCny(double cny) {
        return formatAmount(cny, PRECISION_CNY, AmountStyle.FillZeroNoComma);
    }

    public static String formatCny(String cny) {
        return formatAmount(cny, PRECISION_CNY, AmountStyle.FillZeroNoComma);
    }

    /**
     * 格式化BTC
     * 默认不使用逗号分隔，如需分隔，直接使用{@link #formatAmount(double, int, AmountStyle)}
     *
     * @param value
     * @return
     */
    public static String formatBtc(double value) {
        return formatAmount(value, PRECISION_BTC, AmountStyle.FillZeroNoComma);
    }

    /**
     * 格式化BTC
     * 默认不使用逗号分隔，如需分隔，直接使用{@link #formatAmount(BigDecimal, int, AmountStyle)}
     *
     * @param value
     * @return
     */
    public static String formatBtc(BigDecimal value) {
        return formatAmount(value, PRECISION_BTC, AmountStyle.FillZeroNoComma);
    }

    /**
     * 百分数专用
     *
     * @param value
     * @return
     */
    public static String formatPercent(double value) {
        return formatPercent(value, ROUNDING_MODE_FOR_FORMAT_UTIL);
    }

    /**
     * 百分数专用
     *
     * @param value
     * @return
     */
    public static String formatPercent(double value, RoundingMode roundingMode) {
        return formatAmount(value, 2, AmountStyle.FillZeroNoComma, roundingMode);
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
