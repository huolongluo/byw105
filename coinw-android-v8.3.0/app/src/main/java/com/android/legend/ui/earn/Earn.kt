package com.android.legend.ui.earn

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.legend.common.util.ThemeUtil
import huolongluo.byw.R

enum class EarnType(val type: Int) {
    NORMAL(1),// 常规产品
    HOT(2)// 活动产品
}

enum class EarnTimeLimitType(val type: Int, val timeLimitName: String) {
    CURRENT(1, "CURRENT_FINANCIAL"),// 活期
    REGULAR(2, "REGULAR_FINANCIAL"),// 定期
    MIX(3, "BLEND_FINANCIAL")// 混合
}

enum class EarnBillActionType(val actionName: String) {
    SUBSCRIPTION("SUBSCRIPTION"), // 申购
    REDEMPTION("REDEMPTION"), // 赎回
    PROFIT("PROFIT") // 收益
}

fun RadioGroup.addEarnDeadlineButton(titles:Array<String>) {
    val idStart = 0x1000
    this.removeAllViews()
    /*
    <RadioButton
            android:id="@+id/tv_time_limit1"
            android:layout_width="64dp"
            android:layout_height="26dp"
            android:background="@drawable/bg_earn_time_limit_selected"
            android:button="@null"
            android:checked="true"
            android:gravity="center"
            android:padding="0dp"
            android:text="30天"
            android:textColor="?col_text_title"
            android:visibility="gone"
            tools:visibility="visible" />
     */
    titles.forEachIndexed { index, title ->
        val btn = RadioButton(context).apply {
            id = idStart + index
            isChecked = index == 0
            text = context.getString(R.string.earn_days, title)
            setBackgroundResource(R.drawable.bg_earn_time_limit_selected)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
            setTextColor(ThemeUtil.getThemeColor(context, R.attr.col_text_title))
            buttonDrawable = null
        }

        val lp = LinearLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.dp_64), context.resources.getDimensionPixelSize(R.dimen.dp_26))
        lp.leftMargin = context.resources.getDimensionPixelSize(R.dimen.dp_5)
        this.addView(btn,lp)
    }

}