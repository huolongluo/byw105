package com.legend.modular_contract_sdk.base

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.legend.common.base.ThemeActivity
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.net.requestStateHandle
import com.legend.modular_contract_sdk.utils.StatusBarUtils2
import org.greenrobot.eventbus.EventBus


open abstract class BaseActivity<VM : BaseViewModel> : ThemeActivity() {

    lateinit var mViewModel: VM

    var mMarketListenerList: MutableList<MarketListener> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置状态栏背景及字体颜色
        StatusBarUtils2.setStatusBar(this)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        mViewModel = createViewModel()
        mViewModel.requestStateLiveData.observe(this, Observer {
            it?.let {
                requestStateHandle(this, it)
            }
        })

    }

    protected fun getViewModel() = mViewModel

    protected abstract fun createViewModel(): VM

    protected fun applyToolBar(title: String, subTitle: String = "", rightBtnImgResId: Int = -1, rightBtnClick: ((View) -> Unit)? = null) {

        findViewById<View>(R.id.ivLeftIcon)?.setOnClickListener { onBackPressed() }

        findViewById<TextView>(R.id.tvTitle)?.text = title

        findViewById<TextView>(R.id.tvSubTitle)?.apply {
            text = subTitle
            if (TextUtils.isEmpty(subTitle)) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
        }

        findViewById<ImageView>(R.id.ivRightIcon)?.apply {
            if (rightBtnImgResId > 0) setImageResource(rightBtnImgResId)

            setOnClickListener {
                rightBtnClick?.invoke(it)
            }
        }
    }

    override fun onDestroy() {
        mMarketListenerList.forEach {
            MarketListenerManager.unsubscribe(it)
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }
//    override fun getDelegate(): AppCompatDelegate {
//        return SkinAppCompatDelegateImpl.get(this, this)
//    }
}