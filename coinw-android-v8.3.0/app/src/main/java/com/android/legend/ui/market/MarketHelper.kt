package com.android.legend.ui.market

import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.google.android.material.tabs.TabLayout
import huolongluo.byw.byw.bean.MarketListBean
import kotlinx.android.synthetic.main.fragment_market_first.*
import kotlinx.android.synthetic.main.item_market_pair.view.*

//行情的帮助类
object MarketHelper {
    var areaFirstList = arrayListOf<MarketAreaBean>()//第一级分区所有数据的列表
    var pairList= mutableListOf<MarketListBean>()//存储最新的所有的币对的列表

    val ETF_AREA_ID = "4" // EFT 分区ID

    var firstCurrentClickPosition = 0 //当前第一级选中的位置，使用该值需谨慎，否则容易出错，比如行情该值在etf,帮助类的isEtf就会为true，侧滑栏不能使用该值判断
    var isSecondBBLast=false //解决DrawerLayout和viewpager滑动冲突，记录侧滑栏第二级是否已经是最后一个
    var isBBLast=false //记录币币侧滑栏是否已是最后一个，当侧滑栏已处于最后一个关闭再打开通过该字段控制侧滑栏冲突
    var bbSideSearchTxt=""//记录币币侧滑栏搜索的文本，用于其列表展示做筛选
    @JvmStatic
    fun addAreaFirstList(list:ArrayList<MarketAreaBean>){
        areaFirstList.clear()
        areaFirstList.addAll(list)
        areaFirstList=ignoreList(areaFirstList)
    }
    @JvmStatic
    fun addPairList(list:MutableList<MarketListBean>){
        if(list.isEmpty()) return
        pairList.clear()
        pairList.addAll(list)
    }
    //第二级分区所有数据的列表
    @JvmStatic
    fun getAreaSecondList(firstPosition:Int=firstCurrentClickPosition):ArrayList<MarketAreaBean>{
        if(areaFirstList.isEmpty()) return arrayListOf()
        return ignoreList(areaFirstList[firstPosition].tradeAreaVos)
    }
    //对三级列表的active做忽略 false忽略不展示
    @JvmStatic
    fun ignoreList(list:ArrayList<MarketAreaBean>):ArrayList<MarketAreaBean>{
        if(list.isEmpty()) return list
        val l= arrayListOf<MarketAreaBean>()
        l.addAll(list)
        list.clear()
        l.filter { it.active }.forEach { bean->
            list.add(bean)
        }
        return list
    }
    //验证2级tab是否忽略不显示
    @JvmStatic
    fun isIgnore(secondListIndex: Int):Boolean{
        if(isSelf())//行情自选只显示币币，忽略etf和合约
        {
            if(getAreaSecondList()[secondListIndex].id!=MarketAreaIdEnum.SECOND_BB.id){
                return true
            }
        }
        return false
    }
    @JvmStatic
    fun setSelected(tv: TextView) {
        tv.isSelected = true
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
    }
    @JvmStatic
    fun setNormal(tv: TextView) {
        tv.isSelected = false
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    }
    @JvmStatic
    fun getAreas(areaCoinsStr: String?):List<String>?{
        return areaCoinsStr?.split("_")
    }
    @JvmStatic
    fun isContract() = areaFirstList[firstCurrentClickPosition].id == MarketAreaIdEnum.CONTRACT.id //仅用于行情判断
    @JvmStatic
    fun isEtf() = areaFirstList[firstCurrentClickPosition].id == MarketAreaIdEnum.ETF.id
    @JvmStatic
    fun isSelf() = areaFirstList[firstCurrentClickPosition].id == MarketAreaIdEnum.SELF.id
    @JvmStatic
    fun setTextLong(tv:TextView,str:String){
        if(str.length>10){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f)
        }else{
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
        }
    }
}