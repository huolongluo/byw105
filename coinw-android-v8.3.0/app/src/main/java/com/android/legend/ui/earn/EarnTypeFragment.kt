package com.android.legend.ui.earn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.base.BaseViewModel
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentEarnTypeBinding
import huolongluo.byw.log.Logger

/**
 * 常规产品喝活动产品
 */
class EarnTypeFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance(type: EarnTimeLimitType): EarnTypeFragment = EarnTypeFragment().apply {
            arguments = Bundle().apply {
                putInt("time_limit_type", type.type)
            }
        }
    }

    private lateinit var mBinding: FragmentEarnTypeBinding

    private var mTimeLimitType: EarnTimeLimitType = EarnTimeLimitType.CURRENT

    private var mIgnore = false

    override fun createViewModel() = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnTypeBinding.inflate(inflater, container, false)
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
        }

        initView()

    }

    private fun initView() {

        mBinding.vpContainer.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        EarnListFragment.getInstance(EarnType.NORMAL, mTimeLimitType)
                    }
                    1 -> {
                        EarnListFragment.getInstance(EarnType.HOT, mTimeLimitType)
                    }
                    else -> {
                        EarnListFragment.getInstance(EarnType.NORMAL, mTimeLimitType)
                    }
                }
            }

        }

        mBinding.rgType.setOnCheckedChangeListener { group, checkedId ->
            if (mIgnore){
                return@setOnCheckedChangeListener
            }
            if (checkedId == R.id.rb_normal_product){
                mBinding.cbHotProduct2.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.col_text_content))
                mBinding.vpContainer.setCurrentItem(0,false)
            } else {
                mBinding.cbHotProduct2.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.col_text_title))
                mBinding.vpContainer.setCurrentItem(1,false)
            }
        }

        mBinding.vpContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mIgnore = true
                (mBinding.rgType.getChildAt(position) as RadioButton).isChecked = true
                mIgnore = false
            }
        })

        mBinding.vpContainer.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        EarnListFragment.getInstance(EarnType.NORMAL, mTimeLimitType);
                    }
                    1 -> {
                        EarnListFragment.getInstance(EarnType.HOT, mTimeLimitType)
                    }
                    else -> {
                        EarnListFragment.getInstance(EarnType.NORMAL, mTimeLimitType)
                    }
                }
            }

        }


    }
}