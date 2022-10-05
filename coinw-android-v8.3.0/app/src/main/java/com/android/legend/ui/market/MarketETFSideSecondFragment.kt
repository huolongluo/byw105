package com.android.legend.ui.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
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
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.sp.SpUtils2
import kotlinx.android.synthetic.main.fragment_market_second.*

/**
 * etf行情侧滑栏第二级(因为侧滑栏不显示第一级)，逻辑参考MarketSecondFragment
 */
class MarketETFSideSecondFragment :MarketBBSideSecondFragment(){
    companion object {
        fun newInstance(secondPosition: Int)
        :MarketETFSideSecondFragment {
            val fragment=MarketETFSideSecondFragment()
            val bundle=Bundle()
            bundle.putInt("secondPosition", secondPosition)
            fragment.arguments= bundle
            return fragment
        }
    }
    override fun getAreaSecondList():ArrayList<MarketAreaBean>{
        MarketHelper.areaFirstList.forEachIndexed { index, bean->
            if(bean.id==MarketAreaIdEnum.ETF.id){
                return MarketHelper.getAreaSecondList(index)
            }
        }
        return arrayListOf()
    }
    override fun getFirstAreaCoinStr():List<String>?{
        MarketHelper.areaFirstList.forEach { bean->
            if(bean.id==MarketAreaIdEnum.ETF.id){
                return MarketHelper.getAreas(bean.areaCoinsStr)
            }
        }
        return null
    }
    override fun isEtf():Boolean{
        return true
    }
    override fun getThirdFragment(thirdPosition:Int):MarketETFSideThirdFragment{
        return MarketETFSideThirdFragment.newInstance(secondPosition,thirdPosition)
    }
}