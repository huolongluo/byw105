package com.legend.modular_contract_sdk.ui.experience_gold.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.databinding.McSdkActivityHistoryExperienceGoldBinding
import com.legend.modular_contract_sdk.ui.contract.ExperienceGoldType

class HistoryExperienceGoldActivity : BaseActivity<HistoryExperienceGoldViewModel>() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, HistoryExperienceGoldActivity::class.java))
        }
    }

    private lateinit var mBinding: McSdkActivityHistoryExperienceGoldBinding

    private val mFragments by lazy {
        mutableListOf<Fragment>(HistoryExperienceGoldFragment.getInstance(ExperienceGoldType.USED),
                HistoryExperienceGoldFragment.getInstance(ExperienceGoldType.EXPIRED))
    }

    override fun createViewModel() = ViewModelProvider(this).get(HistoryExperienceGoldViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.mc_sdk_activity_history_experience_gold)

        applyToolBar(getString(R.string.mc_sdk_experience_gold_history))

        initView()
    }

    private fun initView() {

        mBinding.vp.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

            override fun getItem(position: Int) = mFragments[position]

            override fun getCount() = 2

            override fun getPageTitle(position: Int): CharSequence? {
                return when(position){
                    0 -> getString(R.string.mc_sdk_used)
                    1 -> getString(R.string.mc_sdk_expired)
                    else -> ""
                }
            }

        }

        mBinding.tab.setupWithViewPager(mBinding.vp)
    }
}