package com.android.legend.ui.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.legend.base.BaseFragment
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.view.tablayout.SlidingAdapter
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.gson.reflect.TypeToken
import huolongluo.byw.R
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.sp.SpUtils2
import kotlinx.android.synthetic.main.fragment_market_second.*

/**
 * 币币行情侧滑栏第二级(因为侧滑栏不显示第一级)，逻辑参考MarketSecondFragment
 */
open class MarketBBSideSecondFragment :MarketSecondFragment(){
    private val bbAreaSecondList by lazy { arguments?.getSerializable("bbAreaSecondList") as ArrayList<MarketAreaBean> }
    companion object {
        fun newInstance(secondPosition: Int,bbAreaSecondList:ArrayList<MarketAreaBean>)
        :MarketBBSideSecondFragment {
            val fragment=MarketBBSideSecondFragment()
            val bundle=Bundle()
            bundle.putInt("secondPosition", secondPosition)
            bundle.putSerializable("bbAreaSecondList",bbAreaSecondList)
            fragment.arguments= bundle
            return fragment
        }
    }

    override fun initView(view: View) {
        super.initView(view)
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(MarketHelper.isSecondBBLast&&position==thirdFragments.size-1){//解决滑动冲突 最后一个需要关闭侧滑栏
                    MainActivity.self?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    MarketHelper.isBBLast=true
                }else if (MainActivity.self?.drawer_layout?.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_UNLOCKED) {
                    MainActivity.self?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
                    MarketHelper.isBBLast=false
                }
            }
        })
    }
    open fun isEtf():Boolean{
        return false
    }
    override fun getThirdFragment(thirdPosition:Int):MarketBBSideThirdFragment{
        return MarketBBSideThirdFragment.newInstance(secondPosition,thirdPosition,bbAreaSecondList)
    }
    override fun getAreaSecondList():ArrayList<MarketAreaBean>{
        return bbAreaSecondList
    }
}