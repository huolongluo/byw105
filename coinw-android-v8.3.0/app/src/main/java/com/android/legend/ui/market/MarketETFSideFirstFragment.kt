package com.android.legend.ui.market

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.android.legend.extension.gone
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.google.android.material.tabs.TabLayoutMediator
import huolongluo.byw.R
import huolongluo.byw.byw.ui.activity.main.MainActivity
import kotlinx.android.synthetic.main.fragment_market_bb_side.*
import kotlinx.android.synthetic.main.fragment_market_bb_side.tabLayout
import kotlinx.android.synthetic.main.fragment_market_bb_side.viewPager

//etf行情侧滑栏第一级，逻辑参考MarketFirstFragment
class MarketETFSideFirstFragment : MarketBBSideFirstFragment() {
    companion object {
        fun newInstance() = MarketETFSideFirstFragment()
    }

    override fun open() {
        super.open()
        MainActivity.self?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun initView(view: View) {
        super.initView(view)
        rltSearch.gone()
    }
    override fun getSecondFragment(index:Int):MarketBBSideSecondFragment{
        return MarketETFSideSecondFragment.newInstance(index)
    }
    override fun isEtf():Boolean{
        return true
    }
}