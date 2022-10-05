@file:JvmName("DoubleUtils")
package com.android.legend.extension

import java.math.RoundingMode
import java.text.DecimalFormat
private val decimalFormat = DecimalFormat().also {
    it.isGroupingUsed = false
}

/**
 * @param minDigit         最小精度
 * @param maxDigit         最大精度
 * @param positivePrefix   是否使用正数 + 前导符号
 */
@JvmOverloads
fun Double.formatString(
    minDigit: Int = 0,
    maxDigit: Int = Int.MAX_VALUE,
    positivePrefix: Boolean = false
): String {
    decimalFormat.minimumFractionDigits = minDigit
    decimalFormat.maximumFractionDigits = maxDigit
    decimalFormat.roundingMode = RoundingMode.DOWN
    val s = decimalFormat.format(this)
    if (positivePrefix) {
        if (this > 0) {
            return "+$s"
        }
    }
    return s
}

/**
 * @param digits            固定精度
 * @param positivePrefix   是否使用正数 + 前导符号
 */
@JvmOverloads
fun Double.formatStringByDigits(digits: Int, positivePrefix: Boolean = false): String {
    return formatString(digits, digits, positivePrefix)
}


/**
 * @param minDigit         最小精度
 * @param maxDigit         最大精度
 * @param positivePrefix   是否使用正数 + 前导符号
 */
@JvmOverloads
fun Float.formatString(
    minDigit: Int = 0,
    maxDigit: Int = Int.MAX_VALUE,
    positivePrefix: Boolean = false
): String {
    // float直接转化成double会精度损失，这里将float先转为string，再转成double
    val double = this.toString().toDouble()
    return double.formatString(minDigit, maxDigit, positivePrefix)
}