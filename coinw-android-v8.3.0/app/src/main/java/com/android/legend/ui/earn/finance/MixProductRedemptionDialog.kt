package com.android.legend.ui.earn.finance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.android.legend.model.CurrencyInfo
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.bumptech.glide.Glide
import com.legend.modular_contract_sdk.utils.getNum
import com.lxj.xpopup.core.BottomPopupView
import huolongluo.byw.R
import huolongluo.byw.databinding.DialogMixRedemptionBinding
import huolongluo.byw.databinding.LayoutEarnIncomeInfoBinding

class MixProductRedemptionDialog(context: Context,val currencyList:List<CurrencyInfo>, val earnProduct: EarnProductWrap, val onConfirm: (EarnProductWrap) -> Unit) : BottomPopupView(context) {

    lateinit var mBinding: DialogMixRedemptionBinding

    override fun getImplLayoutId(): Int = R.layout.dialog_mix_redemption

    override fun onCreate() {
        super.onCreate()

        mBinding = DialogMixRedemptionBinding.bind(popupImplView)

        mBinding.earnProduct = earnProduct

        earnProduct.earnProduct.productInvestList.forEach {invest ->
            val investAmountBinding = LayoutEarnIncomeInfoBinding.inflate(LayoutInflater.from(context), mBinding.llBuyAmount, false)
            investAmountBinding.tvCurrencyName.text = invest.currencyName
            investAmountBinding.tvInfo.text = "${(invest.investTotalAmount).toString().getNum(8)} ${invest.currencyName}"
            mBinding.llBuyAmount.addView(investAmountBinding.root)
        }

        earnProduct.earnProduct.productIncomeList.forEachIndexed {index, income ->
            val incomeProfitLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(LayoutInflater.from(context), mBinding.llProfitRate, false)
            incomeProfitLayoutBinding.tvCurrencyName.text = income.currencyName
            incomeProfitLayoutBinding.tvInfo.text = (income.actualRate * 100).toString().getNum(2) + "%"
            mBinding.llProfitRate.addView(incomeProfitLayoutBinding.root)

            val incomeInterestLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(LayoutInflater.from(context), mBinding.llInterest, false)
            incomeInterestLayoutBinding.tvCurrencyName.text = income.currencyName
            if (earnProduct.isMixRegularProduct()){
                incomeInterestLayoutBinding.tvInfo.text = earnProduct.getMixExpectedProfit(index)
            } else {
                incomeInterestLayoutBinding.tvInfo.text = "${(income.incomeTotalAmount).toString().getNum(8)} ${income.currencyName}"
            }

            mBinding.llInterest.addView(incomeInterestLayoutBinding.root)

        }

        if(currencyList.isNotEmpty()){
            val coinIconViews = mutableListOf<ImageView>()
            coinIconViews.add(mBinding.ivCurrencyIcon1)
            coinIconViews.add(mBinding.ivCurrencyIcon2)
            coinIconViews.add(mBinding.ivCurrencyIcon3)
            coinIconViews.add(mBinding.ivCurrencyIcon4)
            coinIconViews.add(mBinding.ivCurrencyIcon5)

            earnProduct.earnProduct.productInvestList.forEachIndexed { index, productInvest ->

                if (index > coinIconViews.size){
                    return@forEachIndexed
                }

                coinIconViews[index].visibility = View.GONE
                currencyList.forEach {
                    if (it.id == earnProduct.earnProduct.productInvestList[index].currencyId){
                        coinIconViews[index].visibility = View.VISIBLE
                        Glide.with(context)
                                .load(it.logo)
                                .into(coinIconViews[index])
                        return@forEach
                    }
                }
            }
        }


        mBinding.btnRedemption.setOnClickListener {
            onConfirm(earnProduct)
            dismiss()
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

    }
}