package com.legend.modular_contract_sdk.common.theme

import android.content.Context
import androidx.annotation.AttrRes

fun Context.getThemeColor(@AttrRes colorRes: Int): Int {
    val typeArray = this.theme.obtainStyledAttributes(intArrayOf(colorRes))
    val color = typeArray.getColor(0, 0xFFFFFF)
    typeArray.recycle()
    return color
}