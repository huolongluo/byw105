package com.android.legend.ui.finance

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.legend.base.BaseActivity
import com.android.legend.extension.gone
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.model.finance.ZjFinanceBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.activity_zj_record.*
import kotlinx.android.synthetic.main.item_finance_zj.view.*
import kotlinx.android.synthetic.main.pop_all_zj.view.*

class ZjRecordActivity : BaseActivity() {
    private val viewModel:ZjViewModel by viewModels()
    private val adatper by lazy { gAdapter() }
    private var list= mutableListOf<ZjFinanceBean>()
    private var allZjPopupWindow: PopupWindow?=null
    private var allTypePopupWindow: PopupWindow?=null
    private var zjIndex=0//赠金筛选的位置
    private var typeIndex=0//类型筛选的位置
    companion object{
        fun launch(context: Context){
            context.startActivity(Intent(context,ZjRecordActivity::class.java))
        }
    }
    override fun initTitle(): String {
        return getString(R.string.use_record)
    }

    override fun getContentViewId(): Int {
         return R.layout.activity_zj_record
    }

    override fun initView() {
        tvAllZj.setOnClickListener { showAllZjPop() }
        tvAllType.setOnClickListener { showAllTypePop() }

        rv.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.adapter=adatper
        adatper.setFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space, rv, false))
    }

    override fun initData() {
        getData()
    }

    override fun initObserve() {
        viewModel.zjListData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "赠金记录列表数据：$it")
            if (it.isSuccess) {
                it.data?.apply {
                    list.clear()
                    list.addAll(data)
                    adatper.setList(data)
                    filter()
                }
            } else {
                MToast.show(this, it.message)
            }
        })
    }
    private fun getData(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.getList(2)
    }
    private fun showAllZjPop(){
        if(allZjPopupWindow==null){
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_all_zj, null, false)
            allZjPopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            contentView.tvCancel.setOnClickListener{ allZjPopupWindow?.dismiss() }
            val tvs= arrayOf(contentView.tv1,contentView.tv2,contentView.tv3)
            tvs.forEachIndexed { index,view->
                view.setOnClickListener {
                    if(it.isSelected) return@setOnClickListener
                    tvs.forEach { view-> view.isSelected=false }
                    it.isSelected=true
                    allZjPopupWindow?.dismiss()
                    zjIndex=index
                    filter()
                }
            }
            contentView.tv1.isSelected=true
            val location=IntArray(2)
            tvAllZj.getLocationOnScreen(location)
        }
        allZjPopupWindow?.showAtLocation(tvAllZj, Gravity.BOTTOM, 0, 0)
    }
    private fun filter(){
        if(list==null){
            return
        }
        val listFilter= mutableListOf<ZjFinanceBean>()
        when(zjIndex){
            0->{
                listFilter.addAll(list)
                tvAllZj.text=getString(R.string.all_zj)
            }
            1->{//现货
                listFilter.addAll(list.filter { it.taskAwardType==1 })
                tvAllZj.text=getString(R.string.bb_cash)
            }
            2->{//合约
                listFilter.addAll(list.filter { it.taskAwardType==2 })
                tvAllZj.text=getString(R.string.contract_cash)
            }
        }
        when(typeIndex){
            0->{
                adatper.setList(listFilter)
                tvAllType.text=getString(R.string.all_type)
            }
            1->{//已使用
                adatper.setList(listFilter.filter {
                    if(it.taskAwardType==1) it.userTaskState==3&&it.userTaskAwardState==2 else it.userTaskState==3}
                )
                tvAllType.text=getString(R.string.used)
            }
            2->{//已过期
                adatper.setList(listFilter.filter { it.userTaskState==4 })
                tvAllType.text=getString(R.string.expired)
            }
        }
        adatper.notifyDataSetChanged()
    }
    private fun showAllTypePop(){
        if(allTypePopupWindow==null){
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_all_zj, null, false)
            allTypePopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            contentView.tvCancel.setOnClickListener{ allTypePopupWindow?.dismiss() }
            contentView.tv1.text=getString(R.string.all_type)
            contentView.tv2.text=getString(R.string.used)
            contentView.tv3.text=getString(R.string.expired)
            val tvs= arrayOf(contentView.tv1,contentView.tv2,contentView.tv3)
            tvs.forEachIndexed { index,view->
                view.setOnClickListener {
                    if(it.isSelected) return@setOnClickListener
                    tvs.forEach { view-> view.isSelected=false }
                    it.isSelected=true
                    allTypePopupWindow?.dismiss()
                    typeIndex=index
                    filter()
                }
            }
            contentView.tv1.isSelected=true
            val location=IntArray(2)
            tvAllType.getLocationOnScreen(location)
        }
        allTypePopupWindow?.showAtLocation(tvAllType, Gravity.BOTTOM, 0, 0)
    }
    private fun gAdapter(): BaseQuickAdapter<ZjFinanceBean, BaseViewHolder> =
            object : BaseQuickAdapter<ZjFinanceBean, BaseViewHolder>(R.layout.item_finance_zj){

                override fun convert(helper: BaseViewHolder, item: ZjFinanceBean) {
                    helper.itemView.apply {
                        tvNum.text= PricingMethodUtil.getLargePrice(item.handleAwardAmount)
                        when(item.taskAwardType){
                            1 -> {//现货
                                tvName.text = getString(R.string.bb_cash)
                                tvLimit.invisible()
                                tvTime.text = ""
                                tvItemUnitSpot.visible()
                                tvItemUnit.gone()
                                tvItemUnitSpot.text=if(TextUtils.isEmpty(item.awardCurrency)) AppConstants.COMMON.USDT
                                else item.awardCurrency
                            }
                            2 -> {//合约
                                tvName.text = String.format(getString(R.string.contract_cash_s), "${item.conditionLevelAge}X")
                                tvLimit.visible()
                                tvLimit.text=if(TextUtils.isEmpty(item.awardCurrency)) getString(R.string.limit_usdt_contract)
                                else String.format(getString(R.string.mc_sdk_contract_experience_gold_limit_coin),
                                        "${item.awardCurrency}/${AppConstants.COMMON.USDT}")
                                tvTime.text = "${item.taskExpireDate}"
                                tvItemUnitSpot.gone()
                                tvItemUnit.visible()
                                tvItemUnit.text= AppConstants.COMMON.USDT
                            }
                        }
                        tvUse.gone()
                        ivState.visible()
                        if((item.taskAwardType==1&&item.userTaskState == 3 && item.userTaskAwardState == 2)||
                                item.taskAwardType==2&&item.userTaskState==3) {//已使用
                            ivState.setImageResource(R.mipmap.ic_zj_record_used)
                        }else if(item.userTaskState==4){
                            ivState.setImageResource(R.mipmap.ic_zj_record_expired)
                        }else{
                            ivState.setImageResource(R.mipmap.ic_zj_record_used)
                        }
                    }
                }
            }
}