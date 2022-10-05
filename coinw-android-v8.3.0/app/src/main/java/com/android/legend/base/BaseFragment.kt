package com.android.legend.base

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.legend.common.base.ThemeFragment
import huolongluo.byw.R
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment :ThemeFragment(){
    protected var TAG = this.javaClass.name
    protected lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            toolbar.setNavigationOnClickListener(View.OnClickListener { v: View? ->
                // 退出当前页面
                requireActivity().finish()
            })
            toolbar.setTitle(initAnimationTitle())
            val tvTitle: TextView = view.findViewById<TextView>(R.id.tvTitle)
            if (tvTitle != null) {
                tvTitle.setText(initTitle())
            }
            if (!TextUtils.isEmpty(initRightText())) {
                val tvRight: TextView = view.findViewById<TextView>(R.id.tvRight)
                tvRight.visibility = View.VISIBLE
                tvRight.setText(initRightText())
                tvRight.setOnClickListener(initRightTextClickListener())
                if (initRightTextColor() != 0) {
                    tvRight.setTextColor(ContextCompat.getColor(requireContext(), initRightTextColor()))
                }
            }
        }

        initView(view)
        initData()
        initObserve()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getContentViewId(), container, false)
    }

    override fun onResume() {
        super.onResume()
        if(isRegisterEventBus()){
            if(!EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().register(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(isRegisterEventBus()){
            if(EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().unregister(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    abstract fun getContentViewId():Int
    abstract fun initView(view:View)
    abstract fun initData()
    open fun initObserve() {}
    open fun isRegisterEventBus():Boolean{return false}

    //初始化居中文本的title
    protected open fun initTitle(): String? {
        return ""
    }

    //初始化带动画文本的title
    protected open fun initAnimationTitle(): String? {
        return ""
    }

    //toolbar最右边的文本，默认不显示
    protected open fun initRightText(): String? {
        return ""
    }

    protected open fun initRightTextColor(): Int {
        return 0
    }

    protected open fun initRightTextClickListener(): View.OnClickListener? {
        return null
    }
}