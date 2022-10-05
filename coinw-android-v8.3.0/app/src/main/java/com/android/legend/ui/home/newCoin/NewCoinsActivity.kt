package com.android.legend.ui.home.newCoin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.legend.base.BaseActivity
import com.android.legend.extension.*
import com.android.legend.model.home.newCoin.NewCoinBean
import com.android.legend.ui.share.ShareActivity
import com.android.legend.util.TimerUtil
import com.android.legend.util.glide.TransformationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.common.util.ThemeUtil
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.log.Logger
import huolongluo.byw.util.DateUtil
import huolongluo.byw.util.DateUtils
import huolongluo.byw.util.StringUtil
import huolongluo.byw.util.Util
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.activity_new_coins.*
import kotlinx.android.synthetic.main.item_new_coin_increase.view.*
import kotlinx.android.synthetic.main.item_new_coin_online.view.*
import kotlinx.coroutines.*

/**
 * 新币严选页面
 */
class NewCoinsActivity : BaseActivity() {
    private val viewModel:NewCoinsViewModel by viewModels()
    private var allList= mutableListOf<NewCoinBean>()
    private val allAdapter by lazy { gAllAdapter() }
    private val increaseAdapter by lazy { gIncreaseAdapter() }
    private var state=STATE_COMING_SOON //0 待上线 1 已上线
    private var isTimerStop=false
    private var isFirst=true //第一次检查未上线没有数据展示已上线
    companion object{
        const val STATE_COMING_SOON=0
        const val STATE_ALREADY_ONLINE=1
        fun launch(context: Context){
            context.startActivity(Intent(context,NewCoinsActivity::class.java))
        }
    }

    override fun initTitle(): String {
        return getString(R.string.coin_choice)
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_new_coins
    }

    override fun initView() {
        tvAll.isSelected=true
        tvAll.setOnClickListener {
            if(tvAll.isSelected) return@setOnClickListener
            tvAll.isSelected=true
            vAll.visible()
            rltAllProject.visible()

            tvIncrease.isSelected=false
            vIncrease.invisible()
            llIncrease.gone()
        }
        tvIncrease.setOnClickListener {
            if(tvIncrease.isSelected) return@setOnClickListener
            tvIncrease.isSelected=true
            vIncrease.visible()
            llIncrease.visible()

            tvAll.isSelected=false
            vAll.invisible()
            rltAllProject.gone()
            viewModel.getNewCoins(STATE_ALREADY_ONLINE)
        }

        rvAll.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvAll.adapter=allAdapter
        allAdapter.setFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space,rvAll,false))

        rvIncrease.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvIncrease.adapter=increaseAdapter
        increaseAdapter.setFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space,rvIncrease,false))

        tvComingSoon.isSelected=true
        tvComingSoon.setOnClickListener {
            if(tvComingSoon.isSelected) return@setOnClickListener
            tvComingSoon.isSelected=true
            tvAlreadyOnline.isSelected=false
            viewModel.getNewCoins(STATE_COMING_SOON)
        }
        tvAlreadyOnline.setOnClickListener {
            if(tvAlreadyOnline.isSelected) return@setOnClickListener
            clickTvAlreadyOnline()
        }

    }

    override fun initData() {
        viewModel.getNewCoins(state)
        startTimer()
    }

    override fun initObserve() {
        viewModel.newCoinData.observe(this, Observer {
            Logger.getInstance().debug(TAG,"新币列表数据：$it")
            if(it.isSuccess){
                if(tvAll.isSelected){
                    if(isFirst&&tvComingSoon.isSelected&&it.data?.size==0){
                        clickTvAlreadyOnline()
                        return@Observer
                    }
                    isFirst=false
                    allList=it.data!!
                    allAdapter.setNewInstance(allList)
                    allAdapter.notifyDataSetChanged()
                }else{
                    increaseAdapter.setNewInstance(it.data)
                    increaseAdapter.notifyDataSetChanged()
                }
            }else{
                MToast.show(this, it.message, 1)
            }
        })
    }
    private fun clickTvAlreadyOnline(){
        tvAlreadyOnline.isSelected=true
        tvComingSoon.isSelected=false
        viewModel.getNewCoins(STATE_ALREADY_ONLINE)
    }
    override fun onDestroy() {
        super.onDestroy()
        isTimerStop=true
    }

    private fun startTimer(){
        GlobalScope.launch(Dispatchers.IO) {
            while (!isTimerStop){
                delay(1000)
                withContext(Dispatchers.Main){
                    allAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun gAllAdapter(): BaseQuickAdapter<NewCoinBean, BaseViewHolder> =
            object : BaseQuickAdapter<NewCoinBean, BaseViewHolder>(R.layout.item_new_coin_online){

                override fun convert(helper: BaseViewHolder, item: NewCoinBean) {

                    helper.itemView.apply {
                        ivShare.setOnClickListener { ShareActivity.launch(this@NewCoinsActivity,item.poster) }
                        Glide.with(this@NewCoinsActivity).asBitmap().load(item.banner)
                                .listener(object: RequestListener<Bitmap>{

                                    override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                        ivBanner.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                ivBanner.width/2)
                                        return false
                                    }

                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                        return false
                                    }

                                })
                                .into(ivBanner)
                        tvName.text=item.base
                        tvAnalysisReport.setOnClickListener { gotoH5(this@NewCoinsActivity,item.researchReportAddress,
                        getString(R.string.analysis_report)) }
                        tvTitle.text=item.onlineReason
                        tvContent.text= StringUtil.fromHtml(item.intro)
                        tvTime.text=item.quotationTime
                        if(item.state== STATE_COMING_SOON){
                            tvTrade.isEnabled=false
                            val time=(DateUtils.getDate(item.quotationTime,DateUtils.FORMAT1).time-System.currentTimeMillis())/1000
                            tvTrade.text=String.format(getString(R.string.countdown_to_start_distance_activity),DateUtils.formatLongToTimeStr(time))
                        }else{
                            tvTrade.isEnabled=true
                            tvTrade.text=getString(R.string.go_trade)
                        }
                        tvTrade.setOnClickListener {
                            MainActivity.self?.gotoTrade(MarketListBean(item.id, item.quote, item.base))
                            finish()
                        }
                    }
                }
            }
    private fun gIncreaseAdapter(): BaseQuickAdapter<NewCoinBean, BaseViewHolder> =
            object : BaseQuickAdapter<NewCoinBean, BaseViewHolder>(R.layout.item_new_coin_increase){
                override fun convert(helper: BaseViewHolder, item: NewCoinBean) {
                    helper.itemView.apply {
                        when (helper.layoutPosition) {
                            0 -> {
                                tvRank.gone()
                                ivRank.visible()
                                ivRank.setImageResource(R.mipmap.ic_new_coin_rank1)
                            }
                            1 -> {
                                tvRank.gone()
                                ivRank.visible()
                                ivRank.setImageResource(R.mipmap.ic_new_coin_rank2)
                            }
                            2 -> {
                                tvRank.gone()
                                ivRank.visible()
                                ivRank.setImageResource(R.mipmap.ic_new_coin_rank3)
                            }
                            else -> {
                                tvRank.visible()
                                ivRank.gone()
                                tvRank.text="${helper.layoutPosition+1}"
                            }
                        }
                        tvIncreaseName.text=item.base
                        tvOpenPrice.text="$${item.open}"
                        tvHighestPrice.text="$${item.high}"
                        tvHighestIncrease.text="${item.rate?.toDouble()?.formatStringByDigits(2,true)}%"
                        if(item.rate?.toDoubleOrNull()?:0.0>=0.0){
                            tvHighestIncrease.background= ThemeUtil.getThemeDrawable(context, R.attr.bg_buy_btn)
                        }else{
                            tvHighestIncrease.background= ThemeUtil.getThemeDrawable(context, R.attr.bg_sell_btn)
                        }
                    }
                }
            }
}