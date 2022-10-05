package com.android.legend.ui.earn.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.legend.api.apiService
import com.android.legend.model.earn.EarnBill
import com.android.legend.model.earn.EarnCurrency
import com.android.legend.model.earn.wrap.EarnBillWrap
import com.android.legend.ui.earn.EarnBillActionType
import com.android.legend.ui.earn.EarnTimeLimitType
import com.android.legend.ui.earn.EarnViewModel
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.common.showSelectItemDialog
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithRecyclerView
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.widget.decoration.GridSpacingItemDecoration
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import huolongluo.byw.R
import huolongluo.byw.BR
import huolongluo.byw.databinding.FragmentEarnBillBinding


class EarnBillFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance(): EarnBillFragment {
            return EarnBillFragment()
        }
    }

    lateinit var mBinding: FragmentEarnBillBinding

    var mEarnTimeLimitType: EarnTimeLimitType? = null
    var mEarnActionType: EarnBillActionType? = null
    var mCurrencyId: Int? = null

    var mPageIndex = 1

    var mEarnCurrencyList : List<EarnCurrency> = mutableListOf()

    lateinit var mAdapter: DataBindingRecyclerViewAdapter<EarnBillWrap>

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnBillBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initLiveData()
    }

    private fun initView() {

        applyToolBar(getString(R.string.earn_bill))

        mBinding.rvBill.addItemDecoration(GridSpacingItemDecoration(1, 1, false, true, ThemeUtil.getThemeColor(requireContext(), com.legend.modular_contract_sdk.R.attr.divider_line)))

        mBinding.rvBill.adapter = DataBindingRecyclerViewAdapter<EarnBillWrap>(context, R.layout.item_earn_bill, BR.earnBill, mutableListOf()).apply {
            mAdapter = this
        }

        setupWithRecyclerView<EarnBill, EarnBillWrap>(viewLifecycleOwner, mBinding.rvBill,
                {
                    apiService.fetchEarnBill(mEarnActionType?.actionName, mEarnTimeLimitType?.type, mCurrencyId, 1, McConstants.COMMON.PER_PAGE_SIZE)
                },
                { pageIndex: Int ->
                    apiService.fetchEarnBill(mEarnActionType?.actionName, mEarnTimeLimitType?.type, mCurrencyId, pageIndex, McConstants.COMMON.PER_PAGE_SIZE)
                },useWrap = true
        ){
            EarnBillWrap(it)
        }

        setEmptyView()

        mBinding.tvFilter1.setOnClickListener {
            val items = arrayOf<String>(getString(R.string.earn_bill_filter_type1), getString(R.string.earn_buy), getString(R.string.earn_redemption), getString(R.string.earn_profit))

            showSelectItemDialog(requireContext(), items) { index, text ->
                mBinding.tvFilter1.text = text
                when (index) {
                    0 -> {
                        mEarnActionType = null
                        initData()
                        return@showSelectItemDialog
                    }
                }

                mEarnActionType = EarnBillActionType.values()[index - 1]
                refreshBill()
            }
        }

        mBinding.tvFilter2.setOnClickListener {
            val items = arrayOf<String>(getString(R.string.earn_bill_filter_type2), getString(R.string.earn_current), getString(R.string.earn_regular), getString(R.string.earn_mix))

            showSelectItemDialog(requireContext(), items) { index, text ->
                mBinding.tvFilter2.text = text
                when (index) {
                    0 -> {
                        mEarnTimeLimitType = null
                        initData()
                        return@showSelectItemDialog
                    }
                }

                mEarnTimeLimitType = EarnTimeLimitType.values()[index - 1]
                refreshBill()
            }
        }

        mBinding.tvFilter3.setOnClickListener {

            val items = arrayOf<String>(getString(R.string.earn_bill_filter_type3)).let {
                it + mEarnCurrencyList.map {
                    it.currencyName
                }.toTypedArray()
            }

            showSelectItemDialog(requireContext(), items) { index, text ->
                mBinding.tvFilter3.text = text
                when (index) {
                    0 -> {
                        mCurrencyId = null
                        initData()
                        return@showSelectItemDialog
                    }
                }
                mCurrencyId = mEarnCurrencyList[index - 1].currencyId
                refreshBill()
            }
        }
    }

    private fun initData() {
        refreshBill()

        mViewModel.fetchEarnCurrencyList()

    }

    private fun refreshBill() {
        mViewModel.fetchEarnBill(mEarnTimeLimitType?.type, mEarnActionType?.actionName, mCurrencyId, mPageIndex, McConstants.COMMON.PER_PAGE_SIZE)
    }

    private fun initLiveData() {
        mViewModel.mEarnBillListLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.refreshData(it)
        })

        mViewModel.mEarnCurrencyLiveData.observe(viewLifecycleOwner, Observer {
            mEarnCurrencyList = it
        })
    }

    private fun setEmptyView(){
        val view=LayoutInflater.from(context).inflate(com.legend.modular_contract_sdk.R.layout.mc_sdk_view_empty, mBinding.rvBill, false)
        val tvNoData=view.findViewById<TextView>(com.legend.modular_contract_sdk.R.id.tvNoData)
        val tvNoLogin=view.findViewById<TextView>(com.legend.modular_contract_sdk.R.id.tvNoLogin)
        if(::mAdapter.isInitialized){
            tvNoData.visible()
            tvNoLogin.gone()
            mAdapter.setEmptyView(view)
        }
    }

}