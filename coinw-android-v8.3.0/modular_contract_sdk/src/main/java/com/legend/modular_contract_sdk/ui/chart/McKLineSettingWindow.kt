package com.legend.modular_contract_sdk.ui.chart

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.common.UserConfigStorage
import kotlinx.android.synthetic.main.mc_sdk_window_kline_setting.view.*

class McKLineSettingWindow(private val context: Context,
                           private val mainIndexCallback: (mainIndex: McMainIndex) -> Unit,
                           private val subIndexCallback: (subIndex: McSubIndex) -> Unit) : PopupWindow(context) {
    private val whiteColor = ThemeUtil.getThemeColor(context, R.attr.colorAccent)
    private val unselectedColor = ContextCompat.getColor(context, R.color.mc_sdk_a5a2be)

    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.mc_sdk_window_kline_setting, null)
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        //兼容性问题，必须设置保证mBackground不为null，否则6.0一下的手机点击外部无法关闭
        setBackgroundDrawable(ColorDrawable())

        // 设置view
        val mainIndex = UserConfigStorage.getKLineMainIndex()
        val subIndex = UserConfigStorage.getKLineSubIndex()
        when (mainIndex) {
            McMainIndex.MA -> rootView.tvMa.setTextColor(whiteColor)
            McMainIndex.BOLL -> rootView.tvBoll.setTextColor(whiteColor)
            McMainIndex.EMA -> rootView.tvEma.setTextColor(whiteColor)
            McMainIndex.SAR -> {
            }/*暂时不需要这个指标*/
            McMainIndex.NONE -> rootView.ivMainIndex.setImageResource(R.drawable.mc_sdk_eye2)
            else -> rootView.tvMa.setTextColor(whiteColor)
        }
        when (subIndex) {
            McSubIndex.MACD -> rootView.tvMacd.setTextColor(whiteColor)
            McSubIndex.KDJ -> rootView.tvKdj.setTextColor(whiteColor)
            McSubIndex.RSI -> {
            }/*暂时不需要这个指标*/
            McSubIndex.OBV -> {
            }/*暂时不需要这个指标*/
            McSubIndex.WR -> {
            }/*暂时不需要这个指标*/
            McSubIndex.NONE -> rootView.ivSubIndex.setImageResource(R.drawable.mc_sdk_eye2)
            else -> rootView.tvMacd.setTextColor(whiteColor)
        }
        setListener(rootView)
    }

    private fun setListener(rootView: View) {
        // 设置主指标
        val mainIndexViews = listOf(rootView.tvMa, rootView.tvEma, rootView.tvBoll, rootView.ivMainIndex)
        val mainClickListener: (View) -> Unit = { v ->
            mainIndexViews.forEach {
                if (it == rootView.ivMainIndex) {
                    rootView.ivMainIndex.setImageResource(R.drawable.mc_sdk_kline_eyes_open)
                } else {
                    (it as TextView).setTextColor(unselectedColor)
                }
            }
            if (v == rootView.ivMainIndex) {
                rootView.ivMainIndex.setImageResource(R.drawable.mc_sdk_kline_eyes_close)
                UserConfigStorage.saveKLineMainIndex(McMainIndex.NONE)
                mainIndexCallback(McMainIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                val mainIndex = when (v) {
                    rootView.tvEma -> McMainIndex.EMA
                    rootView.tvBoll -> McMainIndex.BOLL
                    else -> McMainIndex.MA
                }
                UserConfigStorage.saveKLineMainIndex( mainIndex)
                mainIndexCallback(mainIndex)
            }
        }
        mainIndexViews.forEach { it.setOnClickListener(mainClickListener) }
        // 副图指标
        val subIndexViews = listOf(rootView.tvMacd, rootView.tvKdj, rootView.ivSubIndex)
        val subClickListener: (View) -> Unit = { v ->
            subIndexViews.forEach {
                if (it == rootView.ivSubIndex) {
                    rootView.ivSubIndex.setImageResource(R.drawable.mc_sdk_kline_eyes_open)
                } else {
                    (it as TextView).setTextColor(unselectedColor)
                }
            }
            if (v == rootView.ivSubIndex) {
                rootView.ivSubIndex.setImageResource(R.drawable.mc_sdk_kline_eyes_close)
                UserConfigStorage.saveKLineSubIndex( McSubIndex.NONE)
                subIndexCallback(McSubIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                val subIndex = when (v) {
                    rootView.tvKdj -> McSubIndex.KDJ
                    else -> McSubIndex.MACD
                }
                UserConfigStorage.saveKLineSubIndex( subIndex)
                subIndexCallback(subIndex)
            }
        }
        subIndexViews.forEach { it.setOnClickListener(subClickListener) }
    }

}