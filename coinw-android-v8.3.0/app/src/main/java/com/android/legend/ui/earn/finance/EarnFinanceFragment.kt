package com.android.legend.ui.earn.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.coinw.biz.event.BizEvent.ChangeExchangeRate
import com.android.legend.model.earn.wrap.EarnAccountCoinWrap
import com.android.legend.model.earn.wrap.EarnProfitWrap
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.ui.earn.EarnViewModel
import com.android.legend.ui.transfer.AccountTransferActivity
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import huolongluo.byw.BR
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentEarnFinanceBinding
import huolongluo.byw.databinding.ItemEarnFinanceBinding
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.DialogUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EarnFinanceFragment :BaseFragment<EarnViewModel>(){

    private lateinit var mBinding : FragmentEarnFinanceBinding

    private lateinit var mAdapter : DataBindingRecyclerViewAdapter<EarnAccountCoinWrap>

    // 列表原始数据 用于筛选
    private var mOriginData :List<EarnAccountCoinWrap> = mutableListOf()

    // 搜索币种
    private val mSearchCoin = ObservableField<String>("")

    // 隐藏币种数字
    private val mHideAmount = ObservableBoolean(false)

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnFinanceBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initLiveData()
    }

    private fun initView() {
        mBinding.moneyUnit = PricingMethodUtil.getPricingSelectType()
        mBinding.searchCoin = mSearchCoin
        mBinding.hideAmount = mHideAmount

        mBinding.rvCoins.adapter = DataBindingRecyclerViewAdapter<EarnAccountCoinWrap>(context, R.layout.item_earn_finance, BR.earnAccount, mutableListOf()).apply {
            mAdapter = this
            setOnBindingViewHolderListener { holder, position ->

                val binding : ItemEarnFinanceBinding = holder.getBinding()

                binding.earnAccount?.mHindAmount = mHideAmount.get()

                binding.moneyUnit = PricingMethodUtil.getPricingSelectType()

                binding.btnManageAccount.setOnClickListener {
                    binding.earnAccount?.let {
                        MyEarnProductActivity.launch(requireContext(), it.earnAccountCoin.coinId)
                    }

                }

                binding.btnTransfer.setOnClickListener {
                    binding.earnAccount?.let {
                        AccountTransferActivity.launch(requireContext(),TransferAccount.EARN.value,TransferAccount.WEALTH.value,
                                it.earnAccountCoin.coinId,null,false,"")
                    }
                }
            }
        }

        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.fetchAccountTotalInfo()
            mViewModel.fetchEarnAccount()
            mBinding.refreshLayout.postDelayed({
                mBinding.refreshLayout.isRefreshing = false
            },5000)
        }

        mBinding.tvFinanceEye.setOnClickListener {
            mHideAmount.set(!mHideAmount.get())
            if (mHideAmount.get()){
                val drawable = resources.getDrawable(R.mipmap.ic_eye_white_close)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                mBinding.tvFinanceEye.setCompoundDrawables(null, null, drawable, null)
            } else {
                val drawable = resources.getDrawable(R.mipmap.ic_eye_white_open)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                mBinding.tvFinanceEye.setCompoundDrawables(null, null, drawable, null)
            }
            mAdapter.notifyDataSetChanged()
        }

        mBinding.tvHideCoin.setOnClickListener{
            DialogUtils.getInstance().showOneButtonDialog(activity, getString(R.string.question_des), getString(R.string.iknow1))
        }

        mBinding.ivHideCoin.setOnClickListener {
            it.isSelected = !it.isSelected
            filterList()
        }

        mSearchCoin.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                filterList()
            }

        })
    }

    private fun filterList() {
        if (mOriginData.isEmpty()){
            return
        }

        val filterHideLittleCoin = mBinding.ivHideCoin.isSelected
        val filterSearchCoin = mSearchCoin.get()!!.isNotEmpty()

        val newList = mOriginData.filter {
            if (filterHideLittleCoin && it.earnAccountCoin.availableBalance * 7 < 1){
                return@filter false
            }

            if (filterSearchCoin && it.earnAccountCoin.coinFullName.indexOf(mSearchCoin.get()!!, ignoreCase = true) < 0){
                return@filter false
            }

            return@filter true
        }

        mAdapter.refreshData(newList)
    }

    private fun initData() {

        mViewModel.fetchAccountTotalInfo()
        mViewModel.fetchEarnAccount()
    }

    private fun initLiveData() {
        mViewModel.mAccountTotalInfoLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.earnProfit = EarnProfitWrap(it)
            mBinding.refreshLayout.isRefreshing = false
        })

        mViewModel.mCoinsEarnAccountLiveData.observe(viewLifecycleOwner, Observer {
            mOriginData = it.map { EarnAccountCoinWrap(it) }
            filterList()
            mBinding.refreshLayout.isRefreshing = false
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPricingMethod(event: ChangeExchangeRate?) {
        mBinding.moneyUnit = PricingMethodUtil.getPricingSelectType()
        mBinding.invalidateAll()
        mAdapter.notifyDataSetChanged()
    }


}