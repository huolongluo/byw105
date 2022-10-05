package com.legend.modular_contract_sdk.utils


import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat

fun String?.getNum(): String {
    try {
        return if (this != null && this.isNotEmpty()) BigDecimal(this).toString() else ""
    } catch (e: Exception) {
        return ""
    }
}

fun String.getDoubleValue(): Double {
    try {
        return this.toDouble()
    } catch (e: Exception) {
        return 0.0
    }
}

//setScale(1)表示保留一位小数，默认用四舍五入方式 
//setScale(1,BigDecimal.ROUND_DOWN)直接删除多余的小数位，如2.35会变成2.3 
//setScale(1,BigDecimal.ROUND_UP)进位处理，2.35变成2.4 
//setScale(1,BigDecimal.ROUND_HALF_UP)四舍五入，2.35变成2.4
//setScaler(1,BigDecimal.ROUND_HALF_DOWN)四舍五入，2.35变成2.3，如果是5则向下舍
//setScaler(1,BigDecimal.ROUND_CEILING)接近正无穷大的舍入
//setScaler(1,BigDecimal.ROUND_FLOOR)接近负无穷大的舍入，数字>0和ROUND_UP作用一样，数字<0和ROUND_DOWN作用一样
//setScaler(1,BigDecimal.ROUND_HALF_EVEN)向最接近的数字舍入，如果与两个相邻数字的距离相等，则向相邻的偶数舍入。
/**
 * scale 保留位数
 * withZero 是否补零
 * roundingMode 格式化模式
 * withSymbol true:大于0时加 "+"
 * withK_M 大于1000时结尾加"K" 大于1000000时结尾加"M"
 */
fun String?.getNum(scale: Int, withZero: Boolean = false, roundingMode: Int = BigDecimal.ROUND_DOWN, withSymbol:Boolean = false, withK_M:Boolean = false): String {
    var result = ""
    try {
        if (this != null && this.isNotEmpty()) {

            val bigDecimal = BigDecimal(this)
            var doubleValue = bigDecimal.setScale(scale, roundingMode).toDouble()

            var k_mSymbol = ""

            if (withK_M){
                if (bigDecimal.divide(BigDecimal("1000000")).toDouble() >= 1){
                    doubleValue = bigDecimal.divide(BigDecimal("1000000")).toDouble()
                    k_mSymbol = "M"
                } else if (bigDecimal.divide(BigDecimal("1000")).toDouble() >= 1){
                    doubleValue = bigDecimal.divide(BigDecimal("1000")).toDouble()
                    k_mSymbol = "K"
                }
            }

            val countZero = scale.let {
                val sb = StringBuilder()
                if(scale>0){
                    sb.append(".")
                }
                for (i in 0 until scale) {
                    if (withZero) sb.append("0")
                    else sb.append("#")
                }
                sb.toString()
            }
            val df = DecimalFormat("#0${countZero}")
            result = df.format(doubleValue)

            if (withSymbol && result.toDouble() > 0){
                result = "+$result"
            }

            if (withK_M){
                result = "$result$k_mSymbol"
            }

        }


        return result
    } catch (e: Exception) {
        return "0".getNum(scale, withZero)
    }
}

fun String?.getDouble(): Double {
    try {
        return if (this != null && this.isNotEmpty()) BigDecimal(this).toDouble() else 0.0
    } catch (e: Exception) {
        return 0.0
    }
}

fun String?.getInt(): Int {
    return try {
        if (this != null && this.isNotEmpty()) BigDecimal(this).toInt() else 0
    } catch (e: Exception) {
        0
    }
}

fun Long.getDate(pattern: String? = null): String {
    return if (pattern != null) {
        TimeUtils.millis2String(this, pattern)
    } else {
        TimeUtils.millis2String(this)
    }
}

fun Double.getPrecision():Int{
    val df = DecimalFormat("##.#############")
    val result = df.format(this)
    val index = result.indexOf(".")
    if (index < 0){
        return 0
    }

    return result.length - index - 1

}

infix fun Long.rangeTime(endTime: Long): String {
    return TimeUtils.millis2String(this, "yyyy.MM.dd") + "-" + TimeUtils.millis2String(endTime, "yyyy.MM.dd")
}

infix fun String.stringTimeToLong(pattern: String): Long {
    val format = SimpleDateFormat(pattern)
    return format.parse(this).time
}

fun String.changePartTextSize(context: Context, start: Int, end: Int, sizeSp: Float): SpannableString? {
    val span = SpannableString(this)
    span.setSpan(AbsoluteSizeSpan(ViewUtil.sp2px(context, sizeSp)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return span
}

fun String.getStringByColor(ctx: Context, @ColorRes color: Int): SpannableString? {
    val ssb = SpannableString(this)
    ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(ctx, color)), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ssb
}

fun String.getStringByColorValue(color: Int): SpannableString? {
    val ssb = SpannableString(this)
    ssb.setSpan(ForegroundColorSpan(color), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ssb
}

// 获取步长 传入5 返回 0.00001, 传入6 返回 0.000001,以此类推
fun Int.getMinStep() : String{
    if (this <= 0){
        return "0"
    }
    val countZero = this.let {
        val sb = StringBuilder()
        if(this>0){
            sb.append("0.")
        }
        for (i in 1 until this) {
            sb.append("0")
        }
        sb.append("1")
        sb.toString()
    }
    return countZero
}