package com.legend.modular_contract_sdk.ui.experience_gold

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.common.event.JumpToContractTransfer
import com.legend.modular_contract_sdk.common.event.SelectExperienceGoldEvent
import com.legend.modular_contract_sdk.common.event.ShowExperienceGildExplainEvent
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.databinding.McSdkActivityExperienceGoldBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemExperienceGoldBinding
import com.legend.modular_contract_sdk.repository.model.wrap.ExperienceGoldWrap
import com.legend.modular_contract_sdk.ui.experience_gold.history.HistoryExperienceGoldActivity
import com.legend.modular_contract_sdk.utils.ViewUtil
import com.legend.modular_contract_sdk.widget.decoration.SpaceItemDecoration
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import org.greenrobot.eventbus.EventBus

class ExperienceGoldActivity : BaseActivity<ExperienceGoldViewModel>() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, ExperienceGoldActivity::class.java))
        }
    }


    private lateinit var mBinding: McSdkActivityExperienceGoldBinding

    private lateinit var mAdapter: DataBindingRecyclerViewAdapter<ExperienceGoldWrap>

    private val mShowTypeFilter = ObservableField<Boolean>(false)

    private val mShowSortFilter = ObservableField<Boolean>(false)

    override fun createViewModel() = ViewModelProvider(this).get(ExperienceGoldViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.mc_sdk_activity_experience_gold)

        mBinding.showTypeFilter = mShowTypeFilter
        mBinding.showSortFilter = mShowSortFilter

        initView()

        observerLiveData()

        initData()

    }


    private fun initView() {
        applyToolBar(getString(R.string.mc_sdk_contract_experience_gold_title), rightBtnImgResId = R.drawable.ic_trade_question) {
            EventBus.getDefault().post(ShowExperienceGildExplainEvent())
        }

        mBinding.tvFilterType.setOnClickListener {
            mShowTypeFilter.set(!mShowTypeFilter.get()!!)
            mShowSortFilter.set(false)
            Logger.e("click tvFilterType")
        }

        mBinding.tvFilterSort.setOnClickListener {
            mShowSortFilter.set(!mShowSortFilter.get()!!)
            mShowTypeFilter.set(false)
            Logger.e("click tvFilterSort")
        }

        mBinding.viewPlaceholder.setOnClickListener {
            mShowSortFilter.set(false)
            mShowTypeFilter.set(false)
        }

        mBinding.ivHistory.setOnClickListener {
            HistoryExperienceGoldActivity.launch(this)
        }

        mBinding.tvAllType.setOnCheckedChangeListener(typeCheckedChangeListener)
        mBinding.tvWaitUse.setOnCheckedChangeListener(typeCheckedChangeListener)
        mBinding.tvNotActivate.setOnCheckedChangeListener(typeCheckedChangeListener)

        mBinding.tvDefaultSort.setOnCheckedChangeListener(filterCheckedChangeListener)
        mBinding.tvBigToSmall.setOnCheckedChangeListener(filterCheckedChangeListener)
        mBinding.tvBoutToExpire.setOnCheckedChangeListener(filterCheckedChangeListener)


        mAdapter = DataBindingRecyclerViewAdapter<ExperienceGoldWrap>(this,
                R.layout.mc_sdk_item_experience_gold,
                BR.expGold,
                mutableListOf()).apply {

            mBinding.rvList.adapter = this

            setOnBindingViewHolderListener { holder, position ->
                var binding = holder.getBinding<McSdkItemExperienceGoldBinding>()
                binding.btnAction.setOnClickListener {
                    binding.expGold?.let {
                        if (it.isEnable()) {
                            toUseExperienceGold(it)
                        }
                    }

                }

            }

            this.setOnItemClickListener { view, position ->
                toUseExperienceGold(allData[position])
            }

        }

        val space = ViewUtil.dip2px(this, 10.0f)

        mBinding.rvList.addItemDecoration(SpaceItemDecoration(space, space, space, space, null, false, true))

        mBinding.refLayout.setEnableLoadMore(false)

        mBinding.refLayout.setRefreshHeader(ClassicsHeader(this))

        mBinding.refLayout.setOnRefreshListener {
            getViewModel().getInitData(false)
        }

    }


    private fun initData() {
        getViewModel().getInitData(true)

    }

    private fun observerLiveData() {

        getViewModel().mExperienceGoldLiveData.observe(this, Observer {
            mBinding.refLayout.finishRefresh(true)
            mAdapter.refreshData(it.map {
                ExperienceGoldWrap(it)
            })
        })

    }

    private fun toUseExperienceGold(gold: ExperienceGoldWrap) {
        if (gold.isActive()){
            EventBus.getDefault().post(SelectExperienceGoldEvent(gold.experienceGold))
            finish()
        } else {
            EventBus.getDefault().post(JumpToContractTransfer(gold.experienceGold.mConditionCurrencyId, gold.experienceGold.mConditionCurrencyName))
        }

    }

    val typeCheckedChangeListener = { buttonView: CompoundButton, isChecked:Boolean ->
        if(isChecked){
            mShowTypeFilter.set(false)
            getViewModel().getInitData(true)
            when (buttonView.id) {
                R.id.tv_all_type -> {
                    mBinding.tvFilterType.setText(R.string.mc_sdk_all_type)
                    mViewModel.mStatus.value = null
                    mBinding.tvWaitUse.isChecked = false
                    mBinding.tvNotActivate.isChecked = false
                }
                R.id.tv_wait_use -> {
                    mBinding.tvFilterType.setText(R.string.mc_sdk_wait_use)
                    mViewModel.mStatus.value = 1
                    mBinding.tvAllType.isChecked = false
                    mBinding.tvNotActivate.isChecked = false
                }
                R.id.tv_not_activate -> {
                    mBinding.tvFilterType.setText(R.string.mc_sdk_not_activate)
                    mViewModel.mStatus.value = 0
                    mBinding.tvWaitUse.isChecked = false
                    mBinding.tvAllType.isChecked = false
                }
            } 
        }
    }
    
    val filterCheckedChangeListener = { buttonView: CompoundButton, isChecked:Boolean ->
        if (isChecked){
            mShowSortFilter.set(false)
            getViewModel().getInitData(true)
            when (buttonView.id) {
                R.id.tv_default_sort -> {
                    mBinding.tvFilterSort.setText(R.string.mc_sdk_default_sort)
                    mViewModel.mSortType.value = 1
                    mBinding.tvBigToSmall.isChecked = false
                    mBinding.tvBoutToExpire.isChecked = false
                }
                R.id.tv_big_to_small -> {
                    mBinding.tvFilterSort.setText(R.string.mc_sdk_big_to_small)
                    mViewModel.mSortType.value = 2
                    mBinding.tvDefaultSort.isChecked = false
                    mBinding.tvBoutToExpire.isChecked = false
                }
                R.id.tv_bout_to_expire -> {
                    mBinding.tvFilterSort.setText(R.string.mc_sdk_about_to_expire)
                    mViewModel.mSortType.value = 3
                    mBinding.tvDefaultSort.isChecked = false
                    mBinding.tvBigToSmall.isChecked = false
                }
            }
        }
        
    }
    
}