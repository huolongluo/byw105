package com.legend.modular_contract_sdk.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

/**
 * 切换字体的工具类
 */
object McTypefaceUtil {

    val TYPEFACE_DINPRO_MEDIUM = "fonts/DINPRO-MEDIUM.OTF"
    val TYPEFACE_DINPRO_BOLD = "fonts/DINPRO-BOLD.OTF"

    fun setDinproMedium(context: Context, tv: TextView) {
        setTypeface(context, tv, "fonts/DINPRO-MEDIUM.OTF")
    }

    fun setDinproBold(context: Context, tv: TextView) {
        setTypeface(context, tv, "fonts/DINPRO-BOLD.OTF")
    }

    fun setTypeface(context: Context, tv: TextView, path: String) {
        tv.typeface = Typeface.createFromAsset(context.assets, path)
    }
}