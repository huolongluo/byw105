package com.android.legend.ui.earn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.legend.ui.earn.bill.EarnBillActivity
import com.android.legend.ui.login.LoginActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.legend.modular_contract_sdk.base.BaseFragment
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentEarmFrameBinding
import huolongluo.byw.user.UserInfoManager

/**
 * 最外层Fragment 三个tab：活期 定期 混合
 */
class EarnFrameFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance() = EarnFrameFragment()
    }

    private lateinit var mBinding: FragmentEarmFrameBinding

    override fun createViewModel() = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarmFrameBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        mBinding.ivBill.setOnClickListener {
            if (!UserInfoManager.isLogin()) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            } else {
                EarnBillActivity.launch(requireContext())
            }
        }

        mBinding.vpContainer.isUserInputEnabled = false

        mBinding.vpContainer.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        EarnTypeFragment.getInstance(EarnTimeLimitType.CURRENT)
                    }
                    1 -> {
                        EarnTypeFragment.getInstance(EarnTimeLimitType.REGULAR)
                    }
                    2 -> {
                        EarnTypeFragment.getInstance(EarnTimeLimitType.MIX)
                    }
                    else -> {
                        EarnTypeFragment.getInstance(EarnTimeLimitType.CURRENT)
                    }
                }

            }

        }

        val titles = mutableListOf<String>(getString(R.string.earn_current), getString(R.string.earn_regular), getString(R.string.earn_mix))
//        val titles = mutableListOf<String>(getString(R.string.earn_current), getString(R.string.earn_regular))

        TabLayoutMediator(mBinding.tab, mBinding.vpContainer) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}