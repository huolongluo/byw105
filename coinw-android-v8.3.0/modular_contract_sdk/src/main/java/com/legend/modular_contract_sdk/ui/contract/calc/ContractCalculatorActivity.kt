package com.legend.modular_contract_sdk.ui.contract.calc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.databinding.McSdkActivityContractCalculatorBinding
import com.legend.modular_contract_sdk.repository.model.Product

class ContractCalculatorActivity :BaseActivity<BaseViewModel>() {

    companion object{
        fun launch(context: Context,productList: List<Product>){
            Intent(context, ContractCalculatorActivity::class.java).apply {
                putExtra("product_list", ArrayList(productList))
                context.startActivity(this)
            }
        }
    }

    private lateinit var mBinding:McSdkActivityContractCalculatorBinding

    private lateinit var mProductList:List<Product>

    override fun createViewModel(): BaseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProductList = intent.getSerializableExtra("product_list") as ArrayList<Product>

        mBinding = DataBindingUtil.setContentView(this, R.layout.mc_sdk_activity_contract_calculator)

        applyToolBar(getString(R.string.mc_sdk_calculator))

        mBinding.vpCalculator.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> CalcFragment.getInstance(CalcType.CALC_PROFIT, mProductList)
                    1 -> CalcFragment.getInstance(CalcType.CALC_CLOSE_PRICE, mProductList)
                    2 -> CalcFragment.getInstance(CalcType.CALC_LIQUIDATION, mProductList)
                    else -> CalcFragment.getInstance(CalcType.CALC_PROFIT, mProductList)
                }
            }

        }

        val titles = mutableListOf<String>(getString(R.string.mc_sdk_calculator_profit), getString(R.string.mc_sdk_share_close_price), getString(R.string.mc_sdk_calculator_liquidation_price))

        TabLayoutMediator(mBinding.tabTitle, mBinding.vpCalculator) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}