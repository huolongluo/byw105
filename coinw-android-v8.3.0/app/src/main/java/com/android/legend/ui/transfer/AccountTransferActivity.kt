package com.android.legend.ui.transfer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.coinw.biz.event.BizEvent.Trade.RefreshMarketInfo
import com.android.legend.base.BaseActivity
import com.android.legend.common.isLogin
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.model.transfer.AccountPercentBean
import com.android.legend.model.transfer.TransferBean
import com.android.legend.view.InputPriceTextWatcher
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.bean.CoinAddressBean
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.fragment.contractTab.ContractTransferEntity
import huolongluo.byw.byw.ui.fragment.contractTab.nima.DialogUtils.Companion.getInstance
import huolongluo.byw.helper.FaceVerifyHelper
import huolongluo.byw.heyue.HeYueUtil
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.AgreementUtils
import huolongluo.byw.util.Constant
import huolongluo.byw.util.Util
import huolongluo.byw.util.noru.NorUtils
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.MToast
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.DataUtils
import kotlinx.android.synthetic.main.activity_account_transfer.*
import kotlinx.android.synthetic.main.item_account_transfer_record_pop.*
import kotlinx.android.synthetic.main.item_account_transfer_record_pop.tvName
import kotlinx.android.synthetic.main.item_account_transfer_record_pop.view.*
import kotlinx.android.synthetic.main.pop_transfer_coin_list.view.*
import org.greenrobot.eventbus.EventBus

/**
 * 账户划转页面
 */
class AccountTransferActivity : BaseActivity() {
    private val viewModel:AccountTransferViewModel by viewModels()
    private val instance by lazy { this }

    private lateinit var srcAccount:String//划转的源账户
    private lateinit var targetAccount:String //划转的目标账户
    private var coinId:Int=-1
    private var url:String?=null
    private var isNeedSwitch=false //以前都是币币到某币，如果该值为true则需要一次switch操作,暂时只支持币币到买币
    private var coinName: String? =null

    private var transferBean: TransferBean?=null //当前的资产对象
    private var accountListPopupWindow:PopupWindow?=null
    private var coinListPopupWindow:PopupWindow?=null
    private lateinit var rcvFilter:RecyclerView
    private var llAccount:LinearLayout?=null
    private var coinList= arrayListOf<CoinAddressBean>()

    private var isSwitch=false //记录是否进行过切换操作
    private var isBdbAgreementOpen=true //接口返回的未开通列表，初始为true
    private var isContractAgreementOpen=true
    private var isClickAccount1=true //切换账户时是否点击的account1

    companion object{
         fun launch(context: Context,srcAccount:String?,targetAccount:String?,coinId:Int?,url:String?,isNeedSwitch:Boolean?,coinName:String?,intentFlag:Int?){
            if(!isAgreementOpen(context,srcAccount,targetAccount)){//协议未开通不能跳转划转
                return
            }

            var intent=Intent(context, AccountTransferActivity::class.java)
            intent.putExtra("srcAccount",srcAccount)
            intent.putExtra("targetAccount",targetAccount)
            intent.putExtra("coinId",coinId)
            intent.putExtra("url",url)
            intent.putExtra("isNeedSwitch",isNeedSwitch)
            intent.putExtra("coinName",coinName)
             if(intentFlag!=null){
                 intent.flags = intentFlag
             }
            context.startActivity(intent)
        }
        fun launch(context: Context,srcAccount:String?,targetAccount:String?,coinId:Int?,url:String?,isNeedSwitch:Boolean?,coinName:String?){
            this.launch(context,srcAccount,targetAccount,coinId,url,isNeedSwitch,coinName,null)
        }
        //检查协议开通情况，不用检测币贷宝，以前对入口做了验证未开通不能进入划转
        private fun isAgreementOpen(context: Context, srcAccount:String?, targetAccount:String?):Boolean{
            if(isContract(srcAccount,targetAccount)&&!AgreementUtils.isHyOpen()){//合约账户同时未开通则弹出合约开通页面
                HeYueUtil.getInstance().openHY()
                return false
            }

            return true
        }
        private fun isContract(srcAccount:String?, targetAccount:String?):Boolean{
            return TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)||
                    TextUtils.equals(targetAccount, TransferAccount.CONTRACT.value)
        }
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_account_transfer
    }
    override fun initTitle(): String {
        return getString(R.string.qa11)
    }

    override fun initView() {
        if(!isLogin()){
            finish()
        }
        llTransferRecord.setOnClickListener{ AccountTransferRecordActivity.launch(this) }
        llAccount1.setOnClickListener{ showAccountListPop(true) }
        llAccount2.setOnClickListener { showAccountListPop(false) }
        rltSwitch.setOnClickListener{
            clearData()
            switch()
        }
        tvTransfer.setOnClickListener{ transfer() }
        rltSelectCoin.setOnClickListener{ showCoinListPop() }
        tvAll.setOnClickListener{
            if(transferBean==null) return@setOnClickListener

            if (TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)) {
                etAmount.setText("${transferBean?.contractTransferAvailable}")
            } else {
                etAmount.setText("${transferBean?.accountAvailable}")
            }


        }
        etAmount.addTextChangedListener(object : InputPriceTextWatcher(etAmount){
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                var num=s.toString()
                if(!TextUtils.isEmpty(num)&&num.contains(".")&&num.length-num.indexOf(".")>9){
                    num=NorUtils.NumberFormatNo(8).format(num.toDouble())
                    etAmount.setText(num)
                    etAmount.setSelection(num.length)
                }
            }
        })

    }

    override fun initData() {
        srcAccount=intent.getStringExtra("srcAccount")?: TransferAccount.WEALTH.value //默认资产到买币
        targetAccount=intent.getStringExtra("targetAccount")?: TransferAccount.SPOT.value
        coinId=intent.getIntExtra("coinId",-1)
        url=intent.getStringExtra("url")
        isNeedSwitch=intent.getBooleanExtra("isNeedSwitch",false)
        coinName=intent.getStringExtra("coinName")?.toUpperCase()

        if(Constant.IS_BDB_CLOSE&&isBdb()){//币贷宝关闭如果首页配置了入口也可能会进入划转，此时改为默认账户
            srcAccount= TransferAccount.WEALTH.value
            targetAccount= TransferAccount.SPOT.value
        }

        initAccountName()
    }

    override fun initObserve() {
        viewModel.transferData.observe(this, Observer { it -> //读取划转数据
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "viewModel.transferData $it")
            if (it.isSuccess) {
                it.data?.bizVo?.let {
                    //此处调用顺序不可随意更改
                    initAgreement(it.unavailableAccounts)
                    if(!isSameAccount(it.account,it.targetAccount)) return@Observer
                    initCoinList(it.availableCoins,it.coinId)
                    initFinance(it)
                    initPercent(it.accountTotalPercent)
                }
            } else {
                if(TextUtils.equals(it.message,getString(R.string.net_exp))){//因为该接口data内嵌套了一层data，需要区分错误提示文案
                    MToast.show(this, it.message, 1)
                }else{
                    MToast.show(this, it.data?.bizMsg, 1)//接口给的异常文案
                }
            }
        })
        viewModel.transferResult.observe(this, Observer { it -> //划转结果
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "viewModel.transferResult $it")
            if (it.isSuccess) {
                when (it.data?.bizCode) {
                    "123" -> {//需要faceId验证code
                        aliVerify()
                        return@Observer
                    }
                    "124" -> {//黑名单
                        FaceVerifyHelper.getInstance().aliVerifyBlack(this, it.message)
                        return@Observer
                    }
                }
                etAmount.setText("")
                getFinance()
                if(TextUtils.equals(it.data?.bizCode,"unknown")){//提交申请成功
                    MToast.show(this, it.data?.bizMsg, 1)
                }
                it.data?.bizVo?.let{
                    when{
                        it.bizType.contains(TransferAccount.OTC.value)->{
                            if(!isOtc()) return@Observer
                            MToast.show(this, getString(R.string.str_transfer_succeed), 1)
//                            showOtcSuccess()
                        }
                        it.bizType.contains(TransferAccount.CONTRACT.value)->{
                            if(!isContract()) return@Observer
                            if(it.contractVo==null){
                                MToast.show(this, getString(R.string.str_transfer_succeed), 1)
                                return@Observer
                            }
                            if(it.contractVo.tipsType!=1){
                                showContractSuccess(it.contractVo)
                            }else{
                                MToast.show(this, getString(R.string.str_transfer_succeed), 1)
                                return@Observer
                            }
                        }
                        else->MToast.show(this, getString(R.string.str_transfer_succeed), 1)
                    }
                }
            } else {
                if(TextUtils.equals(it.message,getString(R.string.net_exp))){//因为该接口data内嵌套了一层data，需要区分错误提示文案
                    MToast.show(this, it.message, 1)
                }else{
                    MToast.show(this, it.data?.bizMsg, 1)//接口给的异常文案
                }
            }
        })
        viewModel.contractMudInfo.observe(this, Observer {
            Logger.getInstance().debug(TAG, "viewModel.transferResult $it")
            if (it.isSuccess) {
                if(it.data?.data?.isGainAll==false){
                    //此处控制nima提示是否需要显示
                    tvTipBottom.visible()
                }
            }
        })
        viewModel.nmInfo.observe(this, Observer {
            Logger.getInstance().debug(TAG, "viewModel.transferResult $it")
            if (it.isSuccess) {
                url=it.data?.data?.url
                //此处控制nima提示需要的文案
                tvTipBottom.text=String.format(getString(R.string.finish_auth), it.data?.data?.oneLevelTransfer,
                        it.data?.data?.oneLevelMud, it.data?.data?.twoLevelTransfer, it.data?.data?.twoLevelMud)
            }
        })
    }
    private fun isWealth():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.WEALTH.value)||
                TextUtils.equals(targetAccount, TransferAccount.WEALTH.value)
    }
    private fun isSpot():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.SPOT.value)||
                TextUtils.equals(targetAccount, TransferAccount.SPOT.value)
    }
    private fun isOtc():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.OTC.value)||
                TextUtils.equals(targetAccount, TransferAccount.OTC.value)
    }
    private fun isContract():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)||
                TextUtils.equals(targetAccount, TransferAccount.CONTRACT.value)
    }
    private fun isEarn():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.EARN.value)||
                TextUtils.equals(targetAccount, TransferAccount.EARN.value)
    }
    private fun isBdb():Boolean{
        return TextUtils.equals(srcAccount, TransferAccount.BDB.value)||
                TextUtils.equals(targetAccount, TransferAccount.BDB.value)
    }
    private fun initAccountName(){
        tvAccountName1.text=getAccountName(srcAccount)
        tvAccountName2.text=getAccountName(targetAccount)
        if(isContract()){
            viewModel.getNMInfo()
        }
        if(isNeedSwitch){
            switch()
        }else{
            getAllData()
        }
    }
    //初始化协议开通情况
    private fun initAgreement(unavailableAccounts:MutableList<String>?){
        if(unavailableAccounts==null) return
        for(account:String in unavailableAccounts){
            when {
                TextUtils.equals(account, TransferAccount.CONTRACT.value) -> {//合约未开通
                    isContractAgreementOpen=false
                }
                TextUtils.equals(account, TransferAccount.BDB.value) -> {
                    isBdbAgreementOpen=false
                }
            }
        }
        refreshUiAgreement()
    }
    private fun initCoinList(availableCoins:MutableList<CoinAddressBean>?,coinId: Int){
        Logger.getInstance().debug(TAG,"initCoinList availableCoins:$availableCoins")
        if(availableCoins==null) return
        coinList.clear()
        coinList.addAll(availableCoins)
        if(this.coinId==-1&&coinList.size>0){//没有默认取第一个
            refreshCoin(coinList[0].coinId,coinList[0].coinName)
        }else{
            for (coin in availableCoins){
                if (coin.coinId==coinId){
                    refreshCoin(coin.coinId,coin.coinName)
                    break
                }
            }
        }
    }
    private fun initFinance(transferBean: TransferBean){
        this.transferBean=transferBean
        getTvAccountFinanceSrc().text="${transferBean.accountTotal} $coinName"
        getTvAccountFinanceTarget().text="${transferBean.targetTotal} $coinName"
        // 划转账户为合约账户时 取 contractTransferAvailable 其他账户时取 accountAvailable
        if(TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)){
           tvCanTransfer.text=" ${getString(R.string.qa13)}${transferBean.contractTransferAvailable} $coinName"
        } else {
            tvCanTransfer.text=" ${getString(R.string.qa13)}${transferBean.accountAvailable} $coinName"
        }

    }
    private fun initPercent(accountTotalPercent:MutableList<AccountPercentBean>?){//初始化账户余额资金构成
        if(isContract()){
            tvTipBottom.gone()
            if(TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)&&TextUtils.equals(coinName,"USDT")&&accountTotalPercent!=null){//合约转币币
                var available:String?=null
                var mud:String?=null
                for (bean in accountTotalPercent){
                    if (TextUtils.equals(bean.name,"NIMA")){
                        mud=bean.amount
                        continue
                    }
                    if (TextUtils.equals(bean.name,"AVAILABLE")){
                        available=bean.amount
                        continue
                    }
                }
                if(mud==null||available==null||mud.toDouble()<=0) return
                tvNimaBalance.visible()
                tvGrant.visible()
                tvNimaBalance.text=String.format(getString(R.string.str_balance) + ":%sUSDT",available)
                tvGrant.text=String.format(getString(R.string.nm)+":%sUSDT",mud)
            }else if(TextUtils.equals(coinName,"USDT")){
                viewModel.getContractMudInfo()
            }
        }

    }
    //该接口返回的数据协议和币种列表是null,切币调用该接口
    private fun getFinance(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.getTransferFinance(srcAccount,targetAccount,coinId)
    }
    //切账户和进页面调用该接口，能拿到所有数据
    private fun getAllData(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.getTransferData(srcAccount,targetAccount,coinId)
    }
    private fun getTvAccountFinanceSrc():TextView{
        return if(isSwitch) tvAccountFinance2 else tvAccountFinance1
    }
    private fun getTvAccountFinanceTarget():TextView{
        return if(isSwitch) tvAccountFinance1 else tvAccountFinance2
    }
    private fun transfer(){//划转
        if(isContract()&&!isContractAgreementOpen){
            MToast.show(this, getString(R.string.no_open_des), 1)
            return
        }
        if(isBdb()&&!isBdbAgreementOpen){
            MToast.show(this, getString(R.string.bdb_account_no_open), 1)
            return
        }
//        if(TextUtils.equals(srcAccount, TransferAccount.CONTRACT.value)&&transferBean?.accountAvailable?.toDoubleOrNull()?:0.0>0.0&&
//                transferBean?.contractTransferAvailable?.toDoubleOrNull()?:1.0==0.0){//合约全仓持有持仓不能划转
//            MToast.show(this, getString(R.string.contract_cant_transfer), 1)
//            return
//        }
        viewModel.transfer(this,transferBean,etAmount.text.toString(),coinId)
    }
    private fun switch(){
        isSwitch=!isSwitch
        val y1=llAccount1.translationY
        val y2=llAccount2.translationY
        var curPosition=Util.dp2px(this,46f).toFloat()
        if(y2<0) curPosition=0f
        revertAccount()
        getAllData()
        val outAnimator: ObjectAnimator = ObjectAnimator.ofFloat(llAccount1, "translationY", y1, curPosition)
        val inAnimator: ObjectAnimator = ObjectAnimator.ofFloat(llAccount2, "translationY", y2, -curPosition)
        val animSet = AnimatorSet()
        animSet.playTogether(outAnimator, inAnimator)
        animSet.duration = 500
        animSet.start()
    }
    //isAccount1:是否点击的账户1
    private fun showAccountListPop(isAccount1:Boolean){
        isClickAccount1=isAccount1
        if (accountListPopupWindow == null) {
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_transfer_accout_list, null, false)
            accountListPopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
            llAccount = contentView.findViewById<LinearLayout>(R.id.llAccount)
            val tvCancel = contentView.findViewById<TextView>(R.id.tvCancel)
            tvCancel.setOnClickListener {
                if (accountListPopupWindow != null) {
                    accountListPopupWindow?.dismiss()
                }
            }
            initAccountListView()
            accountListPopupWindow?.setOnDismissListener(PopupWindow.OnDismissListener {})
            accountListPopupWindow?.isOutsideTouchable = true
            accountListPopupWindow?.isTouchable = true
            val location = IntArray(2)
            rltSelectCoin.getLocationOnScreen(location)
            accountListPopupWindow?.showAtLocation(rltSelectCoin, Gravity.BOTTOM, 0, 0)
        } else {
            initAccountListView()
            accountListPopupWindow?.showAtLocation(rltSelectCoin, Gravity.BOTTOM, 0, 0)
        }
    }
    private fun showCoinListPop(){
        if(coinListPopupWindow==null){
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_transfer_coin_list, null, false)
            coinListPopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true)
            rcvFilter=contentView.rcvFilter
            contentView.tvCancel.setOnClickListener{ coinListPopupWindow?.dismiss() }
            rcvFilter.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            initCoinListView()
            coinListPopupWindow?.setBackgroundDrawable(ColorDrawable(0x7f000000))
            val location = IntArray(2)
            rltSelectCoin.getLocationOnScreen(location)
            coinListPopupWindow?.showAtLocation(rltSelectCoin, Gravity.BOTTOM, 0, 0)
        }else{
            initCoinListView()
            coinListPopupWindow?.showAtLocation(rltSelectCoin, Gravity.BOTTOM, 0, 0)
        }
    }
    private fun showOtcSuccess(){
        EventBus.getDefault().post(RefreshMarketInfo(false))
        DialogUtils.getInstance().showTwoButtonDialog(this, String.format(getString(R.string.transter_des),
                if (!isSwitch) getString(R.string.cx19) else getString(R.string.cx18),
                if (!isSwitch) getString(R.string.ee37) else getString(R.string.bb_deal)), getString(R.string.cc21),
                if (!isSwitch) getString(R.string.ee37) else getString(R.string.bb_deal))
        DialogUtils.getInstance().setOnclickListener(object : DialogUtils.onBnClickListener() {
            override fun onLiftClick(dialog: AlertDialog, view: View) {
                dialog?.dismiss()
            }

            override fun onRightClick(dialog: AlertDialog, view: View) {
                dialog?.dismiss()
                val intent = Intent(instance, MainActivity::class.java)
                var type=0
                if(!isSwitch){
                    type=1
                }
                intent.putExtra("type", type)
                intent.putExtra("from", "OtcTransferActivity")
                startActivity(intent)
                finish()
            }
        })
    }
    private fun showContractSuccess(bean:ContractTransferEntity){
        when(bean.tipsType){
            2-> MToast.show(this, getString(R.string.no_get_nm), 1)
            3->{
                val strings = arrayOf<String>(String.format(getString(R.string.transfer_nm),
                        bean.stillNeedTransferQuota, bean.availableMudQuota))
                getInstance()?.contractSuccessful1(this, strings,
                        url?:"") {
                    try {
                        if (toContractTrade()) return@contractSuccessful1
                    } catch (t: Throwable) {
                        Logger.getInstance().error(t)
                    }
                    getInstance()?.dismiss()
                }
            }
            4->{
                val strings = arrayOf<String>(String.format(getString(R.string.get_nm), bean.netTransferQuota),bean.mudGetQuota)
                getInstance()?.contractSuccessful1(this, strings,
                        url?:"") {
                    try {
                        if (toContractTrade()) return@contractSuccessful1
                    } catch (t: Throwable) {
                        Logger.getInstance().error(t)
                    }
                    getInstance()?.dismiss()
                }
            }
        }
    }
    private fun initAccountListView(){
        llAccount?.removeAllViews()
        var account=""
        account = if(isClickAccount1){//点击的账户1
            if(isSwitch){
                srcAccount
            }else{
                targetAccount
            }
        }else{
            if(isSwitch){
                targetAccount
            }else{
                srcAccount
            }
        }
        val accountList=getAccountShowList(account)
        for (i in accountList.indices) {
            val textView = TextView(this)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dp2px(this, 43f))
            textView.gravity = Gravity.CENTER
            textView.text=accountList[i]
            textView.setTextColor(ContextCompat.getColor(this,R.color.ff333333))
            if((textView.text.toString().contains(resources.getString(R.string.contract_des))&&!AgreementUtils.isHyOpen())
                    ||(textView.text.toString().contains(resources.getString(R.string.bdb_account))&&!AgreementUtils.isBdbOpen())){
                textView.isEnabled=false
                textView.setTextColor(ContextCompat.getColor(this,R.color.color_c8c8c8))
            }
            textView.setOnClickListener {
                accountListPopupWindow?.dismiss()
                when {
                    textView.text.toString().contains(resources.getString(R.string.wealth_account)) -> {
                        if(isWealth()) return@setOnClickListener
                        switchAccount(TransferAccount.WEALTH.value,textView.text.toString())
                    }
                    textView.text.toString().contains(resources.getString(R.string.qa9)) -> {
                        if(isOtc()) return@setOnClickListener
                        switchAccount(TransferAccount.OTC.value,textView.text.toString())
                    }
                    textView.text.toString().contains(resources.getString(R.string.a27)) -> {
                        if(isSpot()) return@setOnClickListener
                        switchAccount(TransferAccount.SPOT.value,textView.text.toString())
                    }
                    textView.text.toString().contains(resources.getString(R.string.contract_des)) -> {
                        if(isContract()) return@setOnClickListener
                        switchAccount(TransferAccount.CONTRACT.value,textView.text.toString())
                    }
                    textView.text.toString().contains(resources.getString(R.string.earn_account)) -> {
                        if(isEarn()) return@setOnClickListener
                        switchAccount(TransferAccount.EARN.value,textView.text.toString())
                    }
                    textView.text.toString().contains(resources.getString(R.string.bdb_account)) -> {
                        if(isBdb()) return@setOnClickListener
                        switchAccount(TransferAccount.BDB.value,textView.text.toString())
                    }
                }
            }
            val view = View(this)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
            view.setBackgroundColor(ContextCompat.getColor(this,R.color.ffd7dcea))
            llAccount?.addView(view)
            llAccount?.addView(textView)
        }
    }
    private fun initCoinListView(){
        if(rcvFilter==null) return
        val adapter=initCoinListAdapter()
        adapter.setList(coinList)
        Logger.getInstance().debug(TAG,"initCoinListView coinList:$coinList")
        adapter.setOnItemClickListener{adapter,view,position->
            coinListPopupWindow?.dismiss()
            if(coinList==null||coinList.size<=position) return@setOnItemClickListener
            if(isSameCoin(coinList[position].coinId)) return@setOnItemClickListener
            switchCoin(position)
        }
        rcvFilter.adapter=adapter
    }
    private fun initCoinListAdapter(): BaseQuickAdapter<CoinAddressBean,BaseViewHolder> =
            object : BaseQuickAdapter<CoinAddressBean, BaseViewHolder>(R.layout.item_account_transfer_record_pop){
                override fun convert(helper: BaseViewHolder, item: CoinAddressBean) {
                    helper.itemView.tvName.text=item.coinName
                }
            }
    //切账户
    private fun switchAccount(account:String,accountName: String){
        clearData()
        tvGrant.gone()
        tvNimaBalance.gone()
        if (isSwitch) {
            if(isClickAccount1){
                targetAccount=account
                tvAccountName1.text=accountName
            }else{
                srcAccount=account
                tvAccountName2.text=accountName
            }
        } else {
            if(isClickAccount1){
                srcAccount=account
                tvAccountName1.text=accountName
            }else{
                targetAccount=account
                tvAccountName2.text=accountName
            }
        }

        getAllData()
    }
    //切币种
    private fun switchCoin(position:Int){
        if(coinList==null||coinList.size<=position) return
        clearData()
        tvGrant.gone()
        tvNimaBalance.gone()
        etAmount.setText("")
        refreshCoin(coinList[position].coinId,coinList[position].coinName)
        getFinance()
    }
    //刷新跟协议相关的ui
    private fun refreshUiAgreement(){
        tvTipBottom.gone()
        tvTipTop.gone()
        when{
            isContract()->{
                if(!isContractAgreementOpen) {
                    tvTipTop.visible()
                    tvTipTop.text=getString(R.string.no_open)
                    tvAccountName2.text=getString(R.string.no_open_des)
                }
            }
            isBdb()->{
                if(!isBdbAgreementOpen) {
                    tvTipTop.visible()
                    tvTipTop.text=getString(R.string.bdb_transfer_no_open)
                    tvAccountName2.text=getString(R.string.bdb_account_no_open)
                }
            }
        }
    }
    private fun refreshCoin(coinId:Int,coinName:String){
        Logger.getInstance().debug(TAG,"refreshCoin coinId:$coinId coinName:$coinName")
        this.coinId=coinId
        this.coinName=coinName
        tvCoinName1.text=coinName
        tvCoinName2.text=coinName
    }
    private fun revertAccount(){
        val temp=srcAccount
        srcAccount=targetAccount
        targetAccount=temp
    }
    //判断接口返回的划转方向和本地方向是否一致，不一致需要忽略
    private fun isSameAccount(srcAccount: String,targetAccount: String):Boolean{
        return TextUtils.equals(srcAccount,this.srcAccount)&&TextUtils.equals(targetAccount,this.targetAccount)
    }
    //判断接口返回的coinId和本地是否一致，不一致需要忽略
    private fun isSameCoin(coinId: Int):Boolean{
        return coinId==this.coinId
    }

    private fun aliVerify(){
        DialogUtils.getInstance().showRiskTipButtonDialog(this, object : DialogUtils.onBnClickListener() {
            override fun onLiftClick(dialog: AlertDialog, view: View) {
                AppHelper.dismissDialog(dialog)
            }
            override fun onRightClick(dialog: AlertDialog, view: View) {
                AppHelper.dismissDialog(dialog)
                //去验证（阿里face验证）
                FaceVerifyHelper.getInstance().verify(instance, object : DialogUtils.onBnClickListener() {
                    override fun onLiftClick(dialog: AlertDialog, view: View) {
                        AppHelper.dismissDialog(dialog)
                    }
                    override fun onRightClick(dialog: AlertDialog, view: View) {
                        AppHelper.dismissDialog(dialog)
                    }
                }, null)
            }
        })
    }
    private fun toContractTrade(): Boolean {
        if (TextUtils.isEmpty(coinName)) {
            return true
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE)
        intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE)
        intent.putExtra("type", 1) //代表为合约交易
        intent.putExtra("coinName", coinName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        BaseApp.getSelf().startActivity(intent)
        return false
    }
    //切换账户返回可切换的列表,account:未被点击的账户
    private fun getAccountShowList(account:String):ArrayList<String>{
        Logger.getInstance().debug(TAG,"getAccountShowList account:$account")
        var list= arrayListOf<String>()
        when(account){
            TransferAccount.WEALTH.value->{
                if(DataUtils.isOpenHeader()) {
                    list.add(getString(R.string.qa9))
                }
                list.add(getString(R.string.a27))
                list.add(getString(R.string.contract_des))
                list.add(getString(R.string.earn_account))
                if(!Constant.IS_BDB_CLOSE){
                    list.add(getString(R.string.bdb_account))
                }
            }
            TransferAccount.SPOT.value->{
                list.add(getString(R.string.wealth_account))
                if(DataUtils.isOpenHeader()) {
                    list.add(getString(R.string.qa9))
                }
            }
            TransferAccount.OTC.value->{
                list.add(getString(R.string.wealth_account))
                list.add(getString(R.string.a27))
            }
            else->list.add(getString(R.string.wealth_account))
        }
        return list
    }
    private fun getAccountName(account: String):String{
        var accountName=""
        when(account){
            TransferAccount.WEALTH.value-> accountName=getString(R.string.wealth_account)
            TransferAccount.OTC.value-> accountName=getString(R.string.qa9)
            TransferAccount.SPOT.value-> accountName=getString(R.string.a27)
            TransferAccount.CONTRACT.value-> accountName=getString(R.string.contract_des)
            TransferAccount.BDB.value-> accountName=getString(R.string.bdb_account)
            TransferAccount.EARN.value-> accountName=getString(R.string.earn_account)
        }
        return accountName
    }
    private fun clearData(){
        getTvAccountFinanceSrc().text="0 $coinName"
        getTvAccountFinanceTarget().text="0 $coinName"
        tvCanTransfer.text=" ${getString(R.string.qa13)}0 $coinName"
        etAmount.setText("")
        transferBean=null
    }
}