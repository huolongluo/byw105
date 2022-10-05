package com.android.legend.ui.earn.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.legend.ui.earn.EarnTimeLimitType
import com.android.legend.ui.earn.EarnTypeFragment
import com.android.legend.ui.earn.EarnViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.legend.modular_contract_sdk.base.BaseFragment
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentMyEarnProductBinding

class MyEarnProductFragment :BaseFragment<EarnViewModel>(){

    companion object{
        fun getInstance(currencyId:Int) =
            MyEarnProductFragment().apply {
                arguments = Bundle().apply {
                    putInt("currency_id", currencyId)
                }
            }

    }

    private lateinit var mBinding : FragmentMyEarnProductBinding

    private var mCurrencyId:Int = -1

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentMyEarnProductBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrencyId = arguments?.let {
            it.getInt("currency_id", -1)
        }!!
        initView()

    }

    private fun initView() {
        mBinding.vpMyEarnProduct.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        MyEarnProductListFragment.getInstance(mCurrencyId, EarnTimeLimitType.CURRENT)
                    }
                    1 -> {
                        MyEarnProductListFragment.getInstance(mCurrencyId, EarnTimeLimitType.REGULAR)
                    }
                    2 -> {
                        MyEarnProductListFragment.getInstance(mCurrencyId, EarnTimeLimitType.MIX)
                    }
                    else -> {
                        MyEarnProductListFragment.getInstance(mCurrencyId, EarnTimeLimitType.CURRENT)
                    }
                }

            }

        }

        val titles = mutableListOf<String>(getString(R.string.earn_current), getString(R.string.earn_regular), getString(R.string.earn_mix))

        TabLayoutMediator(mBinding.tab, mBinding.vpMyEarnProduct) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}