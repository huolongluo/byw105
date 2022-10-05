package com.android.legend.ui.earn.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.android.legend.api.apiService
import com.android.legend.model.CurrencyInfo
import com.android.legend.model.earn.EarnProduct
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.android.legend.model.earn.wrap.EarnProfitWrap
import com.android.legend.ui.earn.EarnTimeLimitType
import com.android.legend.ui.earn.EarnViewModel
import com.bumptech.glide.Glide
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithRecyclerView
import com.legend.modular_contract_sdk.utils.getNum
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import com.lxj.xpopup.XPopup
import huolongluo.byw.BR
import huolongluo.byw.R
import huolongluo.byw.databinding.*
import huolongluo.byw.log.Logger

class MyEarnProductListFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance(currencyId: Int, earnTimeLimitType: EarnTimeLimitType) =
                MyEarnProductListFragment().apply {
                    arguments = Bundle().apply {
                        putInt("currency_id", currencyId)
                        putSerializable("time_limit_type", earnTimeLimitType)
                    }
                }
    }


    lateinit var mBinding: FragmentMyEarnProductListBinding
    lateinit var mAdapter: DataBindingRecyclerViewAdapter<EarnProductWrap>

    private var mCurrencyId: Int = -1
    private var mEarnTimeLimitType: EarnTimeLimitType = EarnTimeLimitType.MIX
    private var mEarnFinancialType: String = EarnTimeLimitType.CURRENT.type.toString()

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        mBinding = FragmentMyEarnProductListBinding.inflate(inflater)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.fetchCurrencyInfoList()

        arguments?.let {
            mCurrencyId = it.getInt("currency_id", -1)

            mEarnTimeLimitType = it.getSerializable("time_limit_type") as EarnTimeLimitType

            if (mEarnTimeLimitType == EarnTimeLimitType.MIX) {
                mEarnFinancialType = EarnTimeLimitType.CURRENT.type.toString()
            } else {
                mEarnFinancialType = mEarnTimeLimitType.type.toString()
            }

        }

        if (mCurrencyId == -1) {
            return
        }

        if (mEarnTimeLimitType == EarnTimeLimitType.MIX) {
            mBinding.clEarnType.visibility = View.VISIBLE
            mBinding.rgType.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == R.id.rb_current_product) {
                    mEarnFinancialType = EarnTimeLimitType.CURRENT.type.toString()
                } else {
                    mEarnFinancialType = EarnTimeLimitType.REGULAR.type.toString()
                }
                refreshData()
            }
        }

        mBinding.rvList.adapter = DataBindingRecyclerViewAdapter<EarnProductWrap>(context, R.layout.item_my_earn, BR.earnProduct, mutableListOf())
                .apply {
                    mAdapter = this

                    val viewTypeMix = 1001

                    setOnBindingViewHolderListener { holder, position ->

                        val binding = holder.getBinding<ViewDataBinding>()

                        mViewModel.mCurrencyInfoLiveData.value?.let {
                            val earnProduct = allData[position]

                            if (getItemViewType(position) == viewTypeMix) {

                                val coinIconViews = mutableListOf<ImageView>()
                                coinIconViews.add(holder.itemView.findViewById(R.id.iv_currency_icon1))
                                coinIconViews.add(holder.itemView.findViewById(R.id.iv_currency_icon2))
                                coinIconViews.add(holder.itemView.findViewById(R.id.iv_currency_icon3))
                                coinIconViews.add(holder.itemView.findViewById(R.id.iv_currency_icon4))
                                coinIconViews.add(holder.itemView.findViewById(R.id.iv_currency_icon5))

                                coinIconViews.forEach {
                                    it.visibility = View.GONE
                                }

                                earnProduct!!.earnProduct.productInvestList.forEachIndexed { index, productInvest ->

                                    if (index > coinIconViews.size) {
                                        return@forEachIndexed
                                    }

                                    it.forEach {
                                        if (it.id == earnProduct.earnProduct.productInvestList[index].currencyId) {
                                            coinIconViews[index].visibility = View.VISIBLE
                                            Glide.with(requireContext())
                                                    .load(it.logo)
                                                    .into(coinIconViews[index])
                                            return@forEach
                                        }
                                    }
                                }

                            } else {
                                it.forEach {
                                    if (it.id == earnProduct!!.earnProduct.productInvestList[0].currencyId) {
                                        val ivCurrencyIcon = holder.itemView.findViewById<ImageView>(R.id.iv_currency_icon)
                                        Glide.with(requireContext())
                                                .load(it.logo)
                                                .into(ivCurrencyIcon)
                                        return@forEach
                                    }
                                }
                            }

                        }

                        if (binding is ItemMyMixEarnBinding) {

                            binding.llInterest.removeAllViews()
                            binding.llBuyAmount.removeAllViews()
                            binding.llProfitRate.removeAllViews()

                            binding.earnProduct!!.earnProduct.productInvestList.forEach { invest ->
                                val investAmountBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, binding.llBuyAmount, false)
                                investAmountBinding.tvCurrencyName.text = invest.currencyName
                                investAmountBinding.tvInfo.text = "${(invest.investTotalAmount).toString().getNum(8)} ${invest.currencyName}"
                                binding.llBuyAmount.addView(investAmountBinding.root)
                            }

                            binding.earnProduct!!.earnProduct.productIncomeList.forEachIndexed { index, income ->
                                val incomeProfitLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, binding.llProfitRate, false)
                                incomeProfitLayoutBinding.tvCurrencyName.text = income.currencyName
                                incomeProfitLayoutBinding.tvInfo.text = (income.actualRate * 100).toString().getNum(2) + "%"
                                incomeProfitLayoutBinding.tvInfo.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.up_color))
                                binding.llProfitRate.addView(incomeProfitLayoutBinding.root)

                                val incomeInterestLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, binding.llInterest, false)
                                incomeInterestLayoutBinding.tvCurrencyName.text = income.currencyName
                                if (binding.earnProduct!!.isMixRegularProduct()) {
                                    incomeInterestLayoutBinding.tvInfo.text = binding.earnProduct!!.getMixExpectedProfit(index)
                                } else {
                                    incomeInterestLayoutBinding.tvInfo.text = "${(income.incomeTotalAmount).toString().getNum(8)} ${income.currencyName}"
                                }
                                binding.llInterest.addView(incomeInterestLayoutBinding.root)

                            }

                        }

                    }

                    setOnItemClickListener { view, position ->
                        if (getItemViewType(position) == viewTypeMix) {
                            XPopup.Builder(context)
                                    .autoOpenSoftInput(false)
                                    .asCustom(MixProductRedemptionDialog(requireContext(), mViewModel.mCurrencyInfoLiveData.value
                                            ?: mutableListOf(), mAdapter.allData[position]) {
                                        mViewModel.redemption(it.earnProduct.productIncomeList[0].productId)
                                    })
                                    .show()
                        } else {
                            XPopup.Builder(context)
                                    .autoOpenSoftInput(false)
                                    .asCustom(RedemptionDialog(requireContext(), mAdapter.allData[position]) {
                                        mViewModel.redemption(it.earnProduct.productIncomeList[0].productId)
                                    })
                                    .show()
                        }

                    }

                    val viewTypeMap = mutableMapOf<Int, Int>()
                    viewTypeMap[viewTypeMix] = R.layout.item_my_mix_earn

                    setViewTypeProvider(viewTypeMap) viewType@{ position ->
                        if (allData[position].isMixProduct()) {
                            return@viewType viewTypeMix
                        } else {
                            return@viewType DataBindingRecyclerViewAdapter.TYPE_NORMAL
                        }
                    }

                }

        refreshData()

        mViewModel.mMyEarnListLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.refreshData(it)
        })

        mViewModel.mRedemptionSuccessLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                refreshData()
            } else {

            }

        })

        mViewModel.mCurrencyInfoLiveData.observe(viewLifecycleOwner, Observer {
            if (mViewModel.mMyEarnListLiveData.value != null) {
                mAdapter.notifyDataSetChanged()
            }
        })

        setEmptyView()

    }

    fun refreshData() {
        mViewModel.fetchMyEarnList(mCurrencyId, mEarnTimeLimitType.timeLimitName, mEarnFinancialType)
    }

    private fun setEmptyView() {
        val view = LayoutInflater.from(context).inflate(com.legend.modular_contract_sdk.R.layout.mc_sdk_view_empty, mBinding.rvList, false)
        val tvNoData = view.findViewById<TextView>(com.legend.modular_contract_sdk.R.id.tvNoData)
        val tvNoLogin = view.findViewById<TextView>(com.legend.modular_contract_sdk.R.id.tvNoLogin)
        if (::mAdapter.isInitialized) {
            tvNoData.visible()
            tvNoLogin.gone()
            mAdapter.setEmptyView(view)
        }
    }

}