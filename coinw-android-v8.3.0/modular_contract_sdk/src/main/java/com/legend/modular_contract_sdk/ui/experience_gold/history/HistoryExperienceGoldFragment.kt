package com.legend.modular_contract_sdk.ui.experience_gold.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithRecyclerView
import com.legend.modular_contract_sdk.databinding.McSdkFragmentHistoryExperienceGoldBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemExperienceGoldBinding
import com.legend.modular_contract_sdk.repository.model.wrap.ExperienceGoldWrap
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.legend.modular_contract_sdk.ui.contract.ExperienceGoldType
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.ViewUtil
import com.legend.modular_contract_sdk.widget.decoration.SpaceItemDecoration

class HistoryExperienceGoldFragment : BaseFragment<HistoryExperienceGoldViewModel>() {
    companion object {
        fun getInstance(state: ExperienceGoldType): HistoryExperienceGoldFragment = HistoryExperienceGoldFragment().apply {
            arguments = Bundle().apply {
                putInt("state", state.type)
            }
        }
    }

    private lateinit var mBinding: McSdkFragmentHistoryExperienceGoldBinding

    private lateinit var mAdapter: DataBindingRecyclerViewAdapter<ExperienceGoldWrap>

    private var mState = ExperienceGoldType.USED.type

    override fun createViewModel() = ViewModelProvider(this).get(HistoryExperienceGoldViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = McSdkFragmentHistoryExperienceGoldBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         arguments?.apply {
            mState = getInt("state")
        }

        initView()

        observerLiveData()

        initData()
    }

    private fun initData() {
        when(mState){
            ExperienceGoldType.USED.type -> {
                getViewModel().fetchUsedExperienceGold()
            }

            ExperienceGoldType.EXPIRED.type -> {
                getViewModel().fetchExpiredExperienceGold()
            }
        }

        setupWithRecyclerView(this, mBinding.rvList, refreshAction@{
            val responseData = ExperienceGoldService.instance().fetchHistoryExperienceGoldList(mState, 1, McConstants.COMMON.PER_PAGE_SIZE)
            return@refreshAction responseData
        }, loadMoreAction@{ pageIndex ->
            val data = ExperienceGoldService.instance().fetchHistoryExperienceGoldList(mState, pageIndex, McConstants.COMMON.PER_PAGE_SIZE)
            return@loadMoreAction data
        }, useWrap = true) {
            ExperienceGoldWrap(it)
        }

    }

    private fun observerLiveData() {

        when(mState){
            ExperienceGoldType.USED.type -> {
                getViewModel().mUsedExperienceGoldLiveData.observe(this, Observer {
                    mAdapter.addData(it.map { ExperienceGoldWrap(it) })
                })
            }

            ExperienceGoldType.EXPIRED.type -> {
                getViewModel().mExpiredExperienceGoldLiveData.observe(this, Observer {
                    mAdapter.addData(it.map { ExperienceGoldWrap(it) })
                })
            }

        }


    }

    private fun initView() {

        mAdapter = DataBindingRecyclerViewAdapter<ExperienceGoldWrap>(context,
                R.layout.mc_sdk_item_experience_gold,
                BR.expGold,
                mutableListOf()).apply {
            mBinding.rvList.adapter = this

            setOnBindingViewHolderListener { holder, _ ->
                val binding = holder.getBinding<McSdkItemExperienceGoldBinding>()
                binding.ivBgLeft.setImageResource(R.drawable.mc_sdk_bg_item_experience_gold_not_enable)

                when(mState){
                    ExperienceGoldType.USED.type -> {
                        binding.ivState.visibility = View.VISIBLE
                        binding.ivState.setImageResource(R.drawable.mc_sdk_ic_used)
                    }
                    ExperienceGoldType.EXPIRED.type -> {
                        binding.ivState.visibility = View.VISIBLE
                        binding.ivState.setImageResource(R.drawable.mc_sdk_ic_expired)
                    }
                    else -> {
                        binding.ivState.visibility = View.VISIBLE
                    }
                }

            }

        }

        val space = ViewUtil.dip2px(context, 10.0f)

        mBinding.rvList.addItemDecoration(SpaceItemDecoration(space, space, space, space, null, false, true))
    }

}