package com.android.legend.ui.finance

import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.model.finance.ZjFinanceBean
import com.android.legend.ui.taskcenter.TaskCenterActivity
import com.android.legend.ui.transfer.AccountTransferActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.common.event.ContractGoldEvent
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.base.BaseFinanceFragment
import huolongluo.byw.byw.manager.DialogManager
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.DateUtils
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.MToast
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import kotlinx.android.synthetic.main.fragment_finance_zj.*
import kotlinx.android.synthetic.main.inc_finance_account.*
import kotlinx.android.synthetic.main.inc_net_error.*
import kotlinx.android.synthetic.main.item_finance_zj.view.*
import kotlinx.android.synthetic.main.pop_all_zj.view.*
import org.greenrobot.eventbus.EventBus

/**
 * 赠金账户
 */
class ZjFinanceFragment : BaseFinanceFragment() {
    private val viewModel:ZjViewModel by viewModels()
    private val adatper by lazy { gAdapter() }
    private var list= mutableListOf<ZjFinanceBean>()
    private var isClose=false //资产是否关闭显示
    private var finance=""//折合的总资产
    private var allZjPopupWindow:PopupWindow?=null
    private var allTypePopupWindow:PopupWindow?=null
    private var zjIndex=0//赠金筛选的位置
    private var typeIndex=0//类型筛选的位置
    private var isVisibleToUser=true
    companion object{
        fun newInstance()=ZjFinanceFragment()
    }
    override fun getContentViewId(): Int {
        return R.layout.fragment_finance_zj
    }
    override fun initViewsAndEvents(rootView: View?) {
        tvFinanceEye.setOnClickListener {
            if(isClose){
                refreshTotalAsset(finance)
            }else{
                tv_totalasset.text = "********"
                tvUnit.text = "***"
            }
            refreshFinanceEyeUi(isClose)
            isClose=!isClose
        }
        tv_totalasset.text=getString(R.string.zj_finance)
        error_vew_ll.gone()
        error_vew_ll.setOnClickListener { getData() }
        llHideCoin.gone()
        tvOperate1.visible()
        tvOperate1.text=getString(R.string.unlock_more)
        tvOperate1.setOnClickListener { startActivity(Intent(requireContext(), TaskCenterActivity::class.java)) }
        setLeftDrawable(tvOperate1, R.mipmap.ic_finance_lock)
        llHideCoin.gone()
        rltSearch.gone()
        tvAllZj.setOnClickListener { showAllZjPop() }
        tvAllType.setOnClickListener { showAllTypePop() }
        ivRecord.setOnClickListener { ZjRecordActivity.launch(requireContext()) }
        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72)
        refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getData() })

        rv.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.adapter=adatper
        adatper.setFooterView(LayoutInflater.from(context).inflate(R.layout.footer_space, rv, false))

        initObserve()
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        //控制切换至当前界面时的业务操作
        this.isVisibleToUser = isVisibleToUser
        getData()
        super.setUserVisibleHint(isVisibleToUser)
    }
    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun initObserve(){
        viewModel.useData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            when {
                it.isSuccess -> {
                    SnackBarUtils.ShowBlue(requireActivity(), getString(R.string.use_success))
                    getData()
                }
                it.code == AppConstants.CODE.NEED_KYC -> {
                    DialogUtils.getInstance().showTwoButtonDialog(context,getString(R.string.need_kyc),getString(R.string.g8),
                        getString(R.string.authenticate),object : DialogUtils.onBnClickListener() {
                            override fun onLiftClick(dialog: AlertDialog?, view: View?) {
                                dialog?.dismiss()
                            }

                            override fun onRightClick(dialog: AlertDialog?, view: View?) {
                                dialog?.dismiss()
                                startActivity(Intent(context,AuthActivity::class.java))
                            }
                        })
                }
                else -> {
                    MToast.show(context, it.message)
                }
            }
        })
        viewModel.zjListData.observe(this, Observer {
            DialogManager.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "赠金资产列表数据：$it")
            if (it.isSuccess) {
                if (refresh_layout != null && refresh_layout.isRefreshing) {
                    SnackBarUtils.ShowBlue(activity, getString(R.string.dd45))
                }
                error_vew_ll.gone()
                it.data?.apply {
                    finance = "$unlock"
                    refreshTotalAsset(finance)
                    list.clear()
                    list.addAll(data)
                    adatper.setList(data)
                    filter()
                }
            } else {
                MToast.show(context, it.message)
                if (adatper.itemCount > 0) {
                    error_vew_ll.gone()
                } else {
                    error_vew_ll.visible()
                }
            }
            if (refresh_layout != null && refresh_layout.isRefreshing) {
                refresh_layout.isRefreshing = false
            }
        })
    }

    private fun getData(){
        if(!isVisibleToUser||!isAdded){
            return
        }
        if (refresh_layout != null && !refresh_layout.isRefreshing) {
            if (activity != null) {
                if (!requireActivity().isFinishing) {
                    DialogManager.INSTANCE.showProgressDialog(activity, getString(R.string.dd42))
                }
            }
        }
        if (refresh_layout != null && !BaseApp.isNetAvailable) {
            if (refresh_layout.isRefreshing) {
                refresh_layout.isRefreshing = false
            }
            DialogManager.INSTANCE.dismiss()
            SnackBarUtils.ShowRed(activity, getString(R.string.dd43) + "")
        }
        viewModel.getList(1)
    }
    private fun showAllZjPop(){
        if(allZjPopupWindow==null){
            val contentView = LayoutInflater.from(context).inflate(R.layout.pop_all_zj, null, false)
            allZjPopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            contentView.tvCancel.setOnClickListener{ allZjPopupWindow?.dismiss() }
            val tvs= arrayOf(contentView.tv1, contentView.tv2, contentView.tv3)
            tvs.forEachIndexed { index, view->
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
            0 -> {
                listFilter.addAll(list)
                tvAllZj.text = getString(R.string.all_zj)
            }
            1 -> {//现货
                listFilter.addAll(list.filter { it.taskAwardType == 1 })
                tvAllZj.text = getString(R.string.bb_cash)
            }
            2 -> {//合约
                listFilter.addAll(list.filter { it.taskAwardType == 2 })
                tvAllZj.text = getString(R.string.contract_cash)
            }
        }
        when(typeIndex){
            0 -> {
                adatper.setList(listFilter)
                tvAllType.text = getString(R.string.default_sort)
            }
            1 -> {//面额由大到小
                listFilter.sortByDescending { it.handleAwardAmount }
                adatper.setList(listFilter)
                tvAllType.text = getString(R.string.sort_num)
            }
            2 -> {//即将到期优先
                listFilter.sortByDescending { DateUtils.parseDate(it.taskExpireDate, DateUtils.FORMAT2).time }
                adatper.setList(listFilter)
                tvAllType.text = getString(R.string.expired_first)
            }
        }
        if(listFilter.size==0){
            no_data_view.visible()
            refresh_layout.gone()
        }else{
            no_data_view.gone()
            refresh_layout.visible()
        }
        adatper.notifyDataSetChanged()
    }
    private fun showAllTypePop(){
        if(allTypePopupWindow==null){
            val contentView = LayoutInflater.from(context).inflate(R.layout.pop_all_zj, null, false)
            allTypePopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            contentView.tvCancel.setOnClickListener{ allTypePopupWindow?.dismiss() }
            contentView.tv1.text=getString(R.string.default_sort)
            contentView.tv2.text=getString(R.string.sort_num)
            contentView.tv3.text=getString(R.string.expired_first)
            val tvs= arrayOf(contentView.tv1, contentView.tv2, contentView.tv3)
            tvs.forEachIndexed { index, view->
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
                        tvNum.text=PricingMethodUtil.getLargePrice(item.handleAwardAmount)
                        when(item.taskAwardType){
                            1 -> {//现货
                                tvName.text = getString(R.string.bb_cash)
                                tvLimit.gone()
                                tvTime.text = ""
                                tvItemUnitSpot.visible()
                                tvItemUnit.gone()
                                tvItemUnitSpot.text = if (TextUtils.isEmpty(item.awardCurrency)) AppConstants.COMMON.USDT
                                else item.awardCurrency
                                tvUse.text = getString(R.string.to_use)
                                tvTips1.gone()
                                tvTips2.gone()
                            }
                            2 -> {//合约
                                tvName.text = String.format(getString(R.string.contract_cash_s), "${item.conditionLevelAge}X")
                                tvLimit.visible()
                                tvLimit.text = if (TextUtils.isEmpty(item.awardCurrency)) getString(R.string.limit_usdt_contract)
                                else String.format(getString(R.string.mc_sdk_contract_experience_gold_limit_coin),
                                        "${item.awardCurrency}/${AppConstants.COMMON.USDT}")
                                tvTime.text = "${item.taskExpireDate}"
                                tvItemUnitSpot.gone()
                                tvItemUnit.visible()
                                tvItemUnit.text = AppConstants.COMMON.USDT
                                if (item.userTaskState == 0) { //待激活
                                    tvUse.text = getString(R.string.mc_sdk_to_activate)
                                    tvTips1.visible()
                                    tvTips2.visible()
                                    tvTips1.text = String.format(getString(R.string.mc_sdk_activation_condition), item.conditionActiveAmount)
                                    tvTips2.text = String.format(getString(R.string.mc_sdk_activation_progress), item.currentTransferInAmount)
                                } else {
                                    tvUse.text = getString(R.string.to_use)
                                    tvTips1.gone()
                                    tvTips2.gone()
                                }
                            }
                        }
                        tvUse.setOnClickListener {
                            if(item.taskAwardType==1){
                                DialogUtils.getInstance().showTwoButtonDialog(context, getString(R.string.sure_use_zj),
                                        getString(R.string.you_can_see_balance), getString(R.string.cc21), getString(R.string.cc31), object : DialogUtils.onBnClickListener() {
                                    override fun onLiftClick(dialog: AlertDialog?, view: View?) {
                                        AppHelper.dismissDialog(dialog)
                                    }

                                    override fun onRightClick(dialog: AlertDialog?, view: View?) {
                                        AppHelper.dismissDialog(dialog)
                                        DialogManager2.INSTANCE.showProgressDialog(context)
                                        viewModel.useZj(item.userTaskId)
                                    }

                                })
                            }else{
                                if(item.userTaskState==0){ //待激活
                                    AccountTransferActivity.launch(requireContext(), TransferAccount.WEALTH.value,
                                            TransferAccount.CONTRACT.value, item.awardCurrencyId, "", false, "")
                                }else{
                                    MainActivity.self?.gotoSwap("")
                                    EventBus.getDefault().post(ContractGoldEvent(item.userTaskId))
                                }
                            }
                        }
                    }
                }
            }
}