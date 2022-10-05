package com.android.legend.ui.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.coinw.biz.event.BizEvent
import com.android.coinw.biz.event.BizEvent.SelectCurrentPage
import com.android.legend.base.BaseFragment
import com.android.legend.extension.formatStringByDigits
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.socketio.SocketIOClient.subscribe
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.present.HotMoneyPresenter
import huolongluo.byw.byw.ui.present.MarketDataPresent
import huolongluo.byw.io.AppConstants
import huolongluo.byw.util.sp.SpUtils2
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.fragment_market_first.rv
import kotlinx.android.synthetic.main.fragment_market_third.*
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.*
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvLeftCoinName
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvPrice
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvRightCoinName
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * etf行情侧滑栏第三级，逻辑参考MarketThirdFragment
 */
class MarketETFSideThirdFragment : MarketBBSideThirdFragment() {

    companion object {
        fun newInstance(secondPosition: Int, thirdPosition: Int): MarketETFSideThirdFragment {
            val fragment = MarketETFSideThirdFragment()
            val bundle = Bundle()
            bundle.putInt("secondPosition", secondPosition)
            bundle.putInt("thirdPosition", thirdPosition)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getFirstId():Int{
        return MarketAreaIdEnum.ETF.id
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
            if(bean.id==getFirstId()){
                return MarketHelper.getAreas(bean.areaCoinsStr)
            }
        }
        return null
    }
    override fun isSelf() = false

    override fun isEtf():Boolean{
        return true
    }
}