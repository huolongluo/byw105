package huolongluo.byw.reform.home.activity.kline2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import huolongluo.byw.R
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants
import huolongluo.bywx.utils.AppUtils
import kotlinx.android.synthetic.main.window_kline_select_time.view.*
import kotlin.math.max


class KLineSelectTimeWindow(private val context: Context,
                            @Kline2Constants.TradeType private val tradeType: Int,
                            private val callback: (position: Int, text: CharSequence) -> Unit) : PopupWindow(context) {
    private val whiteColor = ContextCompat.getColor(context, R.color.white)
    private val unSelectColor = ContextCompat.getColor(context, R.color.color_a5a2be)
    private val views: List<TextView>
    private val initPosition = 5


    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.window_kline_select_time, null)
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(null)

        views = listOf(rootView.tv1min, rootView.tv5min, rootView.tv30min, rootView.tv6hour, rootView.tv1week, rootView.tv1month)
        if (tradeType == Kline2Constants.TRADE_TYPE_LEVER) {
            rootView.tv6hour.visibility = View.GONE
            rootView.vDivider6Hour.visibility = View.GONE
            rootView.tv1month.visibility = View.GONE
            rootView.vDivider1Month.visibility = View.GONE
        }

        setListener(rootView)
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor)
        views.forEach { it.setTextColor(unSelectColor) }
        // 设置view
        when (AppUtils.getSelectTime()) {
            Kline2Constants.SELECT_TIME_ARRAY[5] -> views[0].setTextColor(whiteColor)
            Kline2Constants.SELECT_TIME_ARRAY[6] -> views[1].setTextColor(whiteColor)
            Kline2Constants.SELECT_TIME_ARRAY[7] -> views[2].setTextColor(whiteColor)
            Kline2Constants.SELECT_TIME_ARRAY[8] -> views[3].setTextColor(whiteColor)
            Kline2Constants.SELECT_TIME_ARRAY[9] -> views[4].setTextColor(whiteColor)
            Kline2Constants.SELECT_TIME_ARRAY[10] -> views[5].setTextColor(whiteColor)
        }
    }

    private fun setListener(rootView: View) {
        val mainClickListener: (View) -> Unit = { v ->
            views.forEach { (it as TextView).setTextColor(unSelectColor) }
            val tv = v as TextView
            tv.setTextColor(whiteColor)
            val position = initPosition + max(views.indexOf(v), 0)
            val selectTime = Kline2Constants.SELECT_TIME_ARRAY[position]
            AppUtils.setSelectTime(selectTime, tradeType)
            callback(position, tv.text)
            dismiss()
        }
        views.forEach { it.setOnClickListener(mainClickListener) }
    }

}