package com.legend.modular_contract_sdk.ui.chart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.common.UserConfigStorage
import com.legend.modular_contract_sdk.utils.McConstants
import kotlinx.android.synthetic.main.mc_sdk_window_kline_select_time.view.*
import kotlin.math.max

class McKLineSelectTimeWindow(private val context: Context,
                              private val callback: (position: Int, text: CharSequence) -> Unit) : PopupWindow(context) {
    private val whiteColor = ContextCompat.getColor(context, R.color.mc_sdk_white)
    private val unSelectColor = ContextCompat.getColor(context, R.color.mc_sdk_a5a2be)
    private val views: List<TextView>
    private val initPosition = 5


    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.mc_sdk_window_kline_select_time, null)
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(null)

        views = listOf(rootView.tv1min, rootView.tv5min, rootView.tv1week)
        setListener(rootView)
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor)
        views.forEach { it.setTextColor(unSelectColor) }
        // 设置view
        when (UserConfigStorage.getSelectTime()) {
            McConstants.KLINE.SELECT_TIME_ARRAY[5] -> views[0].setTextColor(whiteColor)
            McConstants.KLINE.SELECT_TIME_ARRAY[6] -> views[1].setTextColor(whiteColor)
        }
    }

    private fun setListener(rootView: View) {
        val mainClickListener: (View) -> Unit = { v ->
            views.forEach { (it as TextView).setTextColor(unSelectColor) }
            val tv = v as TextView
            tv.setTextColor(whiteColor)
            val position = initPosition + max(views.indexOf(v), 0)
            val selectTime = McConstants.KLINE.SELECT_TIME_ARRAY[position]
            UserConfigStorage.setSelectTime(selectTime)
            callback(position, tv.text)
            dismiss()
        }
        views.forEach { it.setOnClickListener(mainClickListener) }
    }

}