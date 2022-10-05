package huolongluo.byw.reform.home.activity.kline2

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import huolongluo.byw.R
import huolongluo.byw.util.SPUtils
import huolongluo.byw.view.kline.MainIndex
import huolongluo.byw.view.kline.SubIndex
import kotlinx.android.synthetic.main.window_kline_setting.view.*


class KLineSettingWindow(private val context: Context,
                         private val mainIndexCallback: (mainIndex: MainIndex) -> Unit,
                         private val subIndexCallback: (subIndex: SubIndex) -> Unit) : PopupWindow(context) {
    private val whiteColor = ContextCompat.getColor(context, R.color.white)
    private val unselectedColor = ContextCompat.getColor(context, R.color.color_a5a2be)

    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.window_kline_setting, null)
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        //兼容性问题，必须设置保证mBackground不为null，否则6.0一下的手机点击外部无法关闭
        setBackgroundDrawable(ColorDrawable())

        // 设置view
        val mainIndex = SPUtils.getKLineMainIndex(context)
        val subIndex = SPUtils.getKLineSubIndex(context)
        when (mainIndex) {
            MainIndex.MA -> rootView.tvMa.setTextColor(whiteColor)
            MainIndex.BOLL -> rootView.tvBoll.setTextColor(whiteColor)
            MainIndex.EMA -> rootView.tvEma.setTextColor(whiteColor)
            MainIndex.SAR -> {
            }/*暂时不需要这个指标*/
            MainIndex.NONE -> rootView.ivMainIndex.setImageResource(R.mipmap.eye2)
            else -> rootView.tvMa.setTextColor(whiteColor)
        }
        when (subIndex) {
            SubIndex.MACD -> rootView.tvMacd.setTextColor(whiteColor)
            SubIndex.KDJ -> rootView.tvKdj.setTextColor(whiteColor)
            SubIndex.RSI -> {
            }/*暂时不需要这个指标*/
            SubIndex.OBV -> {
            }/*暂时不需要这个指标*/
            SubIndex.WR -> {
            }/*暂时不需要这个指标*/
            SubIndex.NONE -> rootView.ivSubIndex.setImageResource(R.mipmap.eye2)
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
                    rootView.ivMainIndex.setImageResource(R.mipmap.kline_eyes_open)
                } else {
                    (it as TextView).setTextColor(unselectedColor)
                }
            }
            if (v == rootView.ivMainIndex) {
                rootView.ivMainIndex.setImageResource(R.mipmap.kline_eyes_close)
                SPUtils.saveKLineMainIndex(context, MainIndex.NONE)
                mainIndexCallback(MainIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                val mainIndex = when (v) {
                    rootView.tvEma -> MainIndex.EMA
                    rootView.tvBoll -> MainIndex.BOLL
                    else -> MainIndex.MA
                }
                SPUtils.saveKLineMainIndex(context, mainIndex)
                mainIndexCallback(mainIndex)
            }
        }
        mainIndexViews.forEach { it.setOnClickListener(mainClickListener) }
        // 副图指标
        val subIndexViews = listOf(rootView.tvMacd, rootView.tvKdj, rootView.ivSubIndex)
        val subClickListener: (View) -> Unit = { v ->
            subIndexViews.forEach {
                if (it == rootView.ivSubIndex) {
                    rootView.ivSubIndex.setImageResource(R.mipmap.kline_eyes_open)
                } else {
                    (it as TextView).setTextColor(unselectedColor)
                }
            }
            if (v == rootView.ivSubIndex) {
                rootView.ivSubIndex.setImageResource(R.mipmap.kline_eyes_close)
                SPUtils.saveKLineSubIndex(context, SubIndex.NONE)
                subIndexCallback(SubIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                val subIndex = when (v) {
                    rootView.tvKdj -> SubIndex.KDJ
                    else -> SubIndex.MACD
                }
                SPUtils.saveKLineSubIndex(context, subIndex)
                subIndexCallback(subIndex)
            }
        }
        subIndexViews.forEach { it.setOnClickListener(subClickListener) }
    }

}