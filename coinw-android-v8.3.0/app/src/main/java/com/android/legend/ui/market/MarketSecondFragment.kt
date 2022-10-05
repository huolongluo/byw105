package com.android.legend.ui.market

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.android.legend.base.BaseFragment
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.view.tablayout.SlidingAdapter
import com.flyco.tablayout.listener.OnTabSelectListener
import huolongluo.byw.R
import kotlinx.android.synthetic.main.fragment_market_second.*
/**
 * zh行情第二级 主流区  DAO区
 */
open class MarketSecondFragment :BaseFragment(){
    private lateinit var thirdAdapter:SlidingAdapter
    var thirdFragments = mutableListOf<MarketThirdFragment>()
    val secondPosition by lazy { requireArguments().getInt("secondPosition", 0) }//本fragment在tab内的位置
    companion object {
        fun newInstance(secondPosition: Int):MarketSecondFragment {
            val fragment=MarketSecondFragment()
            val bundle=Bundle()
            bundle.putInt("secondPosition", secondPosition)
            fragment.arguments= bundle
            return fragment
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_market_second
    }

    override fun initView(view: View) {
        thirdAdapter=getAreaThirdAdapter()
        viewPager.adapter = thirdAdapter
    }

    override fun initData() {
        initThirdFragment()
    }
    private fun initThirdFragment(){
        val areaSecondList= getAreaSecondList()
        thirdFragments.clear()
        if(areaSecondList==null||areaSecondList.size==0){
            tabLayout.gone()
            val areas= getFirstAreaCoinStr()
            if(areas!=null&&areas.isNotEmpty()){
                for (index in areas.indices){
                    thirdFragments.add(getThirdFragment(index))
                }
                tabLayout.visible()
                val titles= arrayOfNulls<String>(areas.size)
                for (index in areas.indices){
                    titles[index]=areas[index]
                }
                tabLayout.setViewPager(viewPager, titles)
            }else{
                thirdFragments.add(getThirdFragment(0))
            }

        }else{
            if(areaSecondList.size>secondPosition){
                val areas=MarketHelper.getAreas(areaSecondList[secondPosition].areaCoinsStr)
                if(areas!=null){
                    for (index in areas.indices){
                        thirdFragments.add(getThirdFragment(index))
                    }
                    tabLayout.visible()
                    val titles= arrayOfNulls<String>(areas.size)
                    for (index in areas.indices){
                        titles[index]=areas[index]
                    }
                    tabLayout.setViewPager(viewPager, titles)
                    tabLayout.setOnTabSelectListener(object : OnTabSelectListener {
                        override fun onTabSelect(position: Int) {
                            viewPager.currentItem = position
                        }

                        override fun onTabReselect(position: Int) {
                        }
                    })
                }
            }
        }
    }
    open fun getAreaSecondList():ArrayList<MarketAreaBean>{
        return MarketHelper.getAreaSecondList()
    }
    //获取对应的第一级的areaCoinsStr字段，用于第二级为空显示第三级 firstPosition: 行情为点击的位置，币币侧滑栏不使用，etf为行情etf位置
    open fun getFirstAreaCoinStr():List<String>?{
        if(MarketHelper.areaFirstList.isEmpty()||MarketHelper.areaFirstList.size<=MarketHelper.firstCurrentClickPosition) return null
        return MarketHelper.getAreas(MarketHelper.areaFirstList[MarketHelper.firstCurrentClickPosition].areaCoinsStr)
    }
    open fun getThirdFragment(thirdPosition:Int):MarketThirdFragment{
        if(MarketHelper.isContract()){
            return MarketSwapFragment.newInstance(secondPosition,thirdPosition)
        }
        return MarketThirdFragment.newInstance(secondPosition,thirdPosition)
    }

    //第三级viewpager
    private fun getAreaThirdAdapter(): SlidingAdapter = object : SlidingAdapter(this) {
        override fun getItemCount() :Int{
            return thirdFragments.size
        }
        override fun createFragment(position: Int) = thirdFragments[position]
        override fun getPageTitle(position: Int): CharSequence {
            return ""
        }
    }
}