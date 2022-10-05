package com.legend.common.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import com.legend.common.R

object ThemeUtil {
    fun getThemeColor(context: Context, @AttrRes colorRes: Int): Int {
        val typeArray = context.theme.obtainStyledAttributes(intArrayOf(colorRes))
        val color = typeArray.getColor(0, 0xFFFFFF)
        typeArray.recycle()
        return color
    }

    fun getThemeColorStateList(context: Context, @AttrRes colorRes: Int): ColorStateList {
        val typeArray = context.theme.obtainStyledAttributes(intArrayOf(colorRes))
        val color = typeArray.getColorStateList(0)
        typeArray.recycle()
        return color
    }

    fun getThemeDrawable(context: Context, @AttrRes colorRes: Int): Drawable {
        val typeArray = context.theme.obtainStyledAttributes(intArrayOf(colorRes))
        val drawable = typeArray.getDrawable(0)
        typeArray.recycle()
        return drawable
    }

    fun getUpColor(context: Context) : Int{
        val typeArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.up_color))
        val color = typeArray.getColor(0, 0xFFFFFF)
        typeArray.recycle()
        return color
    }

    fun getDropColor(context: Context) : Int{
        val typeArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.drop_color))
        val color = typeArray.getColor(0, 0xFFFFFF)
        typeArray.recycle()
        return color
    }
}