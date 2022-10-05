package com.legend.modular_contract_sdk.base

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.legend.common.base.ThemeFragment
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.event.EmptyEvent
import com.legend.modular_contract_sdk.common.showLoading
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.net.LoadState
import com.legend.modular_contract_sdk.component.net.requestStateHandle
import com.lxj.xpopup.core.BasePopupView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open abstract class BaseFragment<VM : BaseViewModel> : ThemeFragment() {
    lateinit var mViewModel: VM

    internal var mMarketListenerList: MutableList<MarketListener> = mutableListOf()

    lateinit var mRootView: View

    var mLoadingPopView: BasePopupView? = null

    var mLoadStateObserver = Observer<LoadState?> {
        this.context?.let { context ->
            it?.let {
                requestStateHandle(context, it)
            }
        }
    }

    var mLoadingDialogObserver = Observer<Boolean> { isShow ->
        context?.let { context ->
            if (isShow) {
                mLoadingPopView = showLoading(context)
            } else {
                mLoadingPopView?.let {
                    it.dismiss()
                    mLoadingPopView = null
                }
            }
        }
    }

    var mShowMessageObserver = Observer<String> { msg ->
        if(TextUtils.isEmpty(msg)) return@Observer
        context?.let { context ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        mViewModel = createViewModel()

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (ModularContractSDK.theme > 0) {
            context?.theme?.applyStyle(ModularContractSDK.theme, false)
        } else {
            context?.theme?.applyStyle(R.style.mc_sdk_Theme_LegendExchanger, false)
        }

        mRootView = createRootView(inflater, container, savedInstanceState)
        return mRootView
    }

    protected fun applyToolBar(title: String, subTitle: String = "", rightBtnImgResId: Int = -1, rightBtnClick: ((View) -> Unit)? = null) {

        mRootView.findViewById<View>(R.id.ivLeftIcon)?.setOnClickListener { activity?.onBackPressed() }

        mRootView.findViewById<TextView>(R.id.tvTitle)?.text = title

        mRootView.findViewById<TextView>(R.id.tvSubTitle)?.apply {
            text = subTitle
            if (TextUtils.isEmpty(subTitle)) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
        }

        mRootView.findViewById<ImageView>(R.id.ivRightIcon)?.apply {
            if (rightBtnImgResId > 0) setImageResource(rightBtnImgResId)

            setOnClickListener {
                rightBtnClick?.invoke(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::mRootView.isInitialized && leftMenuIsBack()) {
            mRootView.findViewById<View>(R.id.ivLeftIcon)?.setOnClickListener {
                activity?.finish()
            }
        }
    }

    protected fun getViewModel() = mViewModel

    protected abstract fun createViewModel(): VM

    protected abstract fun createRootView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View

    protected open fun leftMenuIsBack() = true

    override fun onResume() {
        super.onResume()
        if (!mViewModel.requestStateLiveData.hasObservers()) {
            mViewModel.requestStateLiveData.value = null
            mViewModel.requestStateLiveData.observe(this, mLoadStateObserver)
        }

        if (!mViewModel.showLoadingLiveData.hasObservers()) {
            mViewModel.showLoadingLiveData.observe(this, mLoadingDialogObserver)
        }

        if (!mViewModel.showMessageLiveData.hasObservers()) {
            if(!TextUtils.isEmpty(mViewModel.showMessageLiveData.value)){
                mViewModel.showMessageLiveData.value=null
            }
            mViewModel.showMessageLiveData.observe(this, mShowMessageObserver)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mViewModel.requestStateLiveData.hasObservers()) {
            mViewModel.requestStateLiveData.removeObserver(mLoadStateObserver)
        }

        if (mViewModel.showLoadingLiveData.hasObservers()) {
            mViewModel.showLoadingLiveData.removeObserver(mLoadingDialogObserver)
        }

        if (mViewModel.showMessageLiveData.hasObservers()) {
            mViewModel.showMessageLiveData.removeObserver(mShowMessageObserver)
        }
    }

    protected fun setLeftIcon(iconRes: Int, clickListener: () -> Unit) {
        if (::mRootView.isInitialized) {

            mRootView.findViewById<ImageView>(R.id.ivLeftIcon)?.apply {
                setOnClickListener {
                    clickListener()
                }
            }
        }
    }

    protected fun removeAllMarketListener() {
        CoinwHyUtils.removeAllMarketListener(mMarketListenerList)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EmptyEvent) {

    }

    override fun onDestroy() {
        removeAllMarketListener()//为了怕一些地方的onpause未remove此处保留
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }
}