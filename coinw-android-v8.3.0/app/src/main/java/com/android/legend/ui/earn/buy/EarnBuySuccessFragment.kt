package com.android.legend.ui.earn.buy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.legend.base.BaseActivity
import com.android.legend.model.earn.EarnProduct
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.getNum
import huolongluo.byw.R
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment
import huolongluo.byw.databinding.FragmentEarnBuySuccessBinding
import huolongluo.byw.databinding.LayoutEarnIncomeInfoBinding
import kotlinx.android.synthetic.main.item_earn_product.view.*
import java.util.*

class EarnBuySuccessFragment : BaseFragment<BaseViewModel>() {

    companion object {
        fun getInstance(earnProduct: EarnProduct, amount: ArrayList<String> ,days:Int): EarnBuySuccessFragment {
            return EarnBuySuccessFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("earn_product", earnProduct)
                    putStringArrayList("amount", amount)
                    putInt("days", days)
                }
            }
        }
    }

    lateinit var mBinding: FragmentEarnBuySuccessBinding

    private lateinit var mEarnProduct: EarnProductWrap
    private lateinit var mAmount: List<String>
    private var mDays: Int = -1

    private val now = System.currentTimeMillis()// 当前事件
    private val day:Long = (1000L * 60L * 60L * 24L)// 一天的毫秒数

    override fun createViewModel(): BaseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnBuySuccessBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.apply {
            mEarnProduct = EarnProductWrap(getSerializable("earn_product") as EarnProduct)
            mAmount = getStringArrayList("amount") ?: mutableListOf()
            mDays = getInt("days")
        }

        initView()
    }

    private fun initView() {


        applyToolBar(getString(R.string.earn_title_buy, mEarnProduct.getProductCoinName()))

        if (mAmount.size == 1){
            mBinding.tvAmount.text = "${mAmount[0]} ${mEarnProduct.getProductCoinName()}"
            mBinding.tvBuyAmount.visibility = View.GONE
            mBinding.llBuyAmount2.visibility = View.GONE
        } else {
            mBinding.llBuyAmount1.visibility = View.GONE
            mAmount.forEachIndexed { index, s ->
                // 根据回报币种 动态生成参考年华
                val incomeProfitLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, mBinding.llBuyAmount2, false)
                val currencyName = mEarnProduct.earnProduct.productInvestList[index].currencyName.toUpperCase()
                incomeProfitLayoutBinding.tvCurrencyName.text = currencyName
                incomeProfitLayoutBinding.tvInfo.text = "$s $currencyName"
                mBinding.llBuyAmount2.addView(incomeProfitLayoutBinding.root)

            }
        }

        if (mEarnProduct.isMixProduct()){
            mBinding.llTimeLimit.visibility = View.GONE
        }

        mBinding.tvTimeLimit.text = if (mDays <= 0) getString(R.string.earn_current) else getString(R.string.earn_days, mDays.toString())

        mBinding.layoutEarnBuyRule.tvBuyDate.text = TimeUtils.date2String(Date(now), "yyyy-MM-dd")
        mBinding.layoutEarnBuyRule.tvStartInterestBearing.text = TimeUtils.date2String(Date(now + day), "yyyy-MM-dd")

        if (mDays >= 1) {
            mBinding.layoutEarnBuyRule.tvProfitArrivalDate.text = getString(R.string.earn_profit_arrival_date)
            mBinding.layoutEarnBuyRule.tvProfitArrivalDateValue.text = TimeUtils.date2String(Date(now + day * (mDays + 1)), "yyyy-MM-dd")
        } else {
            mBinding.layoutEarnBuyRule.tvProfitArrivalDate.text = getString(R.string.earn_profit_yesterday)
            mBinding.layoutEarnBuyRule.tvProfitArrivalDateValue.text = TimeUtils.date2String(Date(now + day * 2), "yyyy-MM-dd")
        }

        mBinding.btnGotoEarn.setOnClickListener {
            activity?.finish()
        }

        mBinding.btnGotoWallet.setOnClickListener {
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_EARN)
            startActivity(Intent(context, MainActivity::class.java))
        }
    }
}