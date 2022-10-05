package com.android.legend.ui.earn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.coinw.biz.event.BizEvent
import com.android.legend.api.apiService
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.android.legend.ui.earn.buy.EarnBuyActivity
import com.android.legend.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithRecyclerView
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import huolongluo.byw.BR
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentEarnListBinding
import huolongluo.byw.databinding.ItemEarnProductBinding
import huolongluo.byw.user.UserInfoManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EarnListFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance(type: EarnType, timeLimitType: EarnTimeLimitType) = EarnListFragment().apply {
            arguments = Bundle().apply {
                putInt("type", type.type)
                putInt("time_limit_type", timeLimitType.type)
            }
        }
    }

    lateinit var mBinding: FragmentEarnListBinding

    private var mTimeLimitType: EarnTimeLimitType = EarnTimeLimitType.CURRENT
    private var mEarnType: EarnType = EarnType.NORMAL

    private lateinit var mAdapter: DataBindingRecyclerViewAdapter<EarnProductWrap>

    // 是否只显示进行中
    private var mIsOnlyShowProgress = false

    // 币种筛选
    private var mFilterCurrency = ObservableField<String>("")

    private var mOriginData: List<EarnProductWrap> = mutableListOf()

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            mTimeLimitType = it.getInt("time_limit_type", EarnTimeLimitType.CURRENT.type).let {
                when (it) {
                    EarnTimeLimitType.CURRENT.type -> {
                        EarnTimeLimitType.CURRENT
                    }
                    EarnTimeLimitType.REGULAR.type -> {
                        EarnTimeLimitType.REGULAR
                    }
                    EarnTimeLimitType.MIX.type -> {
                        EarnTimeLimitType.MIX
                    }
                    else -> {
                        EarnTimeLimitType.CURRENT
                    }
                }
            }

            mEarnType = it.getInt("type", EarnTimeLimitType.CURRENT.type).let {
                when (it) {
                    EarnType.NORMAL.type -> {
                        EarnType.NORMAL
                    }
                    EarnType.HOT.type -> {
                        EarnType.HOT
                    }
                    else -> {
                        EarnType.NORMAL
                    }
                }
            }

        }

        initView()

        initData()

        initLiveData()

    }

    private fun initView() {

        mBinding.filterCurrency = mFilterCurrency

        mBinding.rvList.adapter = DataBindingRecyclerViewAdapter<EarnProductWrap>(requireContext(), R.layout.item_earn_product, BR.earnProduct, mutableListOf())
                .apply {

                    mAdapter = this

                    setOnBindingViewHolderListener { holder, position ->

                        val binding: ItemEarnProductBinding = holder.getBinding()
                        val product = binding.earnProduct

                        product?.let {
                            Glide.with(requireContext())
                                    .load(it.getCover())
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                                    .into(binding.ivProductCover)

                            if (product.isRegularProduct()) {
                                binding.rgTimeLimit.addEarnDeadlineButton(product.earnProduct.deadline.split(",").toTypedArray())

                                binding.rgTimeLimit.setOnCheckedChangeListener { group, checkedId ->
                                    it.index = checkedId - 0x1000
                                    Glide.with(requireContext())
                                            .load(it.getCover())
                                            .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                                            .into(binding.ivProductCover)
                                }
                            }

                            binding.btnBuyEarn.setOnClickListener { view ->
                                view.isClickable = false
                                view.postDelayed({ view.isClickable = true }, 500)

                                if (!UserInfoManager.isLogin()) {
                                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                                } else {
                                    EarnBuyActivity.launch(requireContext(), it.earnProduct, it.index)
                                }

                            }

                        }
                    }
                }

        setupWithRecyclerView(viewLifecycleOwner, mBinding.rvList, {
            apiService.fetchEarnProductList(mTimeLimitType.timeLimitName, mEarnType.type.toString(), page = 1, pageSize = McConstants.COMMON.PER_PAGE_SIZE)
                    .apply {
                        if (data.rows == null) {
                            return@apply
                        }
                        mOriginData = data.rows!!.map { EarnProductWrap(it) }

                        val filterData = mutableListOf<EarnProductWrap>()
                        filterData.addAll(mOriginData)
                        mOriginData.forEach {

                            if ((mIsOnlyShowProgress && !it.isInProgress())) {
                                filterData.remove(it)
                            }

                            if (mFilterCurrency.get()!!.isNotEmpty() && !it.getProductCoinName().contains(mFilterCurrency.get()!!, ignoreCase = true)) {
                                filterData.remove(it)
                            }
                        }
                        data.rows = filterData.map { it.earnProduct }
                    }
        }, { pageindex ->
            apiService.fetchEarnProductList(mTimeLimitType.timeLimitName, mEarnType.type.toString(), page = pageindex, pageSize = McConstants.COMMON.PER_PAGE_SIZE)
        }, useWrap = true, enableLoadMore = false) {
            EarnProductWrap(it)
        }

        setEmptyView()

        mBinding.cbOnlyShowProgress.setOnCheckedChangeListener { buttonView, isChecked ->
            mIsOnlyShowProgress = isChecked
            filterList()
        }

        mFilterCurrency.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                filterList()
            }

        })
    }

    private fun filterList() {
        val filterData = mutableListOf<EarnProductWrap>()
        filterData.addAll(mOriginData)
        mOriginData.forEach {

            if ((mIsOnlyShowProgress && !it.isInProgress())) {
                filterData.remove(it)
            }

            if (mFilterCurrency.get()!!.isNotEmpty() && !it.getProductCoinName().contains(mFilterCurrency.get()!!, ignoreCase = true)) {
                filterData.remove(it)
            }
        }
        mAdapter.refreshData(filterData)
    }

    private fun initData() {
        mViewModel.fetchEarnProductList(mTimeLimitType, mEarnType)
    }

    private fun initLiveData() {

        mViewModel.mEarnProductListLiveData.observe(viewLifecycleOwner, Observer {
            mOriginData = it
            mAdapter.refreshData(it)
        })
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEarnBuySuccess(event: BizEvent.Earn.EarnBuySuccess){
        mViewModel.fetchEarnProductList(mTimeLimitType, mEarnType)
    }

}