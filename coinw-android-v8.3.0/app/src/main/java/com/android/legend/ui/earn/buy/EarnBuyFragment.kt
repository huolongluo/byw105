package com.android.legend.ui.earn.buy

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.coinw.biz.event.BizEvent
import com.android.legend.model.earn.EarnAccountCoin
import com.android.legend.model.earn.EarnProduct
import com.android.legend.model.earn.ProductInvest
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.android.legend.ui.earn.EarnViewModel
import com.android.legend.ui.earn.addEarnDeadlineButton
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getInt
import com.legend.modular_contract_sdk.utils.getNum
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import huolongluo.byw.R
import huolongluo.byw.databinding.FragmentEarnBuyBinding
import huolongluo.byw.databinding.LayoutEarnIncomeInfoBinding
import huolongluo.byw.databinding.LayoutEarnInvestInputBinding
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.tip.MToast
import org.greenrobot.eventbus.EventBus
import java.util.*

class EarnBuyFragment : BaseFragment<EarnViewModel>() {

    companion object {
        fun getInstance(earnProduct: EarnProduct, selectDeadlineIndex:Int): EarnBuyFragment {
            return EarnBuyFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("earn_product", earnProduct)
                    putInt("select_deadline", selectDeadlineIndex)
                }
            }
        }
    }

    private lateinit var mBinding: FragmentEarnBuyBinding

    private lateinit var mEarnProduct: EarnProductWrap

    private var mSelectDeadlineIndex = 0

    private var mAmount = ObservableField<String>("")

    private var mCoinAccountInfo: EarnAccountCoin? = null
    private var mAllCoinAccount: List<EarnAccountCoin> = mutableListOf()

    private var mTimeLimit = -1
    private var mTimeLimitList = mutableListOf<Int>()

    private var mInvestProduct : ProductInvest? = null

    // 混合产品 输入项集合
    private var mInvestInputLayoutBindingList = mutableListOf<LayoutEarnInvestInputBinding>()
    private var mIncomeProfitRateLayoutBindingList = mutableListOf<LayoutEarnIncomeInfoBinding>()
    private var mIncomeInterestLayoutBindingList = mutableListOf<LayoutEarnIncomeInfoBinding>()

    private val now = System.currentTimeMillis()// 当前事件
    private val day:Long = (1000L * 60L * 60L * 24L)// 一天的毫秒数

    override fun createViewModel(): EarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentEarnBuyBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEarnProduct = arguments?.let {
            mSelectDeadlineIndex = it.getInt("select_deadline")
            EarnProductWrap((it.getSerializable("earn_product") as EarnProduct))
        }!!

        initView()
        initData()
        initLiveData()
    }

    private fun initView() {

        mBinding.earnProduct = mEarnProduct

        mBinding.amount = mAmount

        mAmount.addOnPropertyChangedCallback(object :Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                // 定期产品才会计算预计收益
                if (mEarnProduct.isMixRegularProduct()) {
                    val amount = mAmount.get().getDouble()
                    mBinding.tvExpectedInterest.text = mEarnProduct.getExpectedProfit(amount) + mEarnProduct.getIncomeCurrencyName()
                }
                checkInput()
            }
        })

        val title = if(mEarnProduct.isMixProduct()){
            getString(R.string.earn_title_buy, mEarnProduct.getProductCoinName())
        } else if (mEarnProduct.isRegularProduct()) {
            getString(R.string.earn_title_regular, mEarnProduct.getProductCoinName())
        } else {
            getString(R.string.earn_title_current, mEarnProduct.getProductCoinName())
        }

        applyToolBar(title)

        if (mEarnProduct.isMixRegularProduct()) {

            mTimeLimitList = mEarnProduct.earnProduct.deadline.split(",").map {
                it.getInt()
            }.toMutableList()

            mBinding.rgTimeLimit.addEarnDeadlineButton(mEarnProduct.earnProduct.deadline.split(",").toTypedArray())

            (mBinding.rgTimeLimit.getChildAt(mSelectDeadlineIndex) as RadioButton).isChecked = true

            mBinding.rgTimeLimit.setOnCheckedChangeListener { group, checkedId ->
                applyProduct(checkedId - 0x1000)
            }
        } else {

            // 活期的活动产品也有期限限制
            if (mEarnProduct.isHasDeadline()) {
                mTimeLimitList.add(mEarnProduct.earnProduct.deadline.getInt())
                mBinding.tvTime.text = mEarnProduct.getBuyDeadlineDays(requireContext())
            } else {
                mTimeLimitList.add(-1)
            }

            mBinding.tvTime.visibility = View.VISIBLE
        }

        mBinding.etAmount1.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(18,8))
        mBinding.tvProblems.text = mEarnProduct.earnProduct.problem
        mBinding.layoutEarnBuyRule.tvBuyDate.text = TimeUtils.date2String(Date(now), "yyyy-MM-dd")
        mBinding.layoutEarnBuyRule.tvStartInterestBearing.text = TimeUtils.date2String(Date(now + day), "yyyy-MM-dd")

        applyProduct(mSelectDeadlineIndex)

        mBinding.tvAll1.setOnClickListener {
            mCoinAccountInfo?.let {
                mBinding.etAmount1.setText(it.availableBalance.toString().getNum())
            }
        }

        mBinding.btnBuy.setOnClickListener {

            it.isClickable = false
            it.postDelayed({ it.isClickable = true }, 500)

            if (UserInfoManager.isLogin()) {

                if (mEarnProduct.isMixProduct()){
                    // 申购混合产品
                    buyMixProduct()
                } else {
                    // 申购非混合产品
                    buyProduct()
                }

            }
        }

        // 处理混合产品 多个输入项
        if (mEarnProduct.isMixProduct()){
            mEarnProduct.earnProduct.productInvestList.forEach { invest ->
                // 动态根据投资币种数量生成多个输入布局

                val itemBinding = LayoutEarnInvestInputBinding.inflate(layoutInflater, mBinding.llInvestList, false)

                itemBinding.investCoin = invest
                itemBinding.amount = ObservableField("").apply {
                    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            checkInput()
                            if (mEarnProduct.isHasDeadline()){
                                calcMixProfit()
                            }
                        }

                    })
                }
                itemBinding.etAmount1.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(18,8))
                itemBinding.tvCoin1.text = invest.currencyName
                itemBinding.tvAll1.setOnClickListener {
                    mAllCoinAccount?.forEach namedForEach@{
                        if (it.coinId == invest.currencyId) {
                            itemBinding.etAmount1.setText(it.availableBalance.toString().getNum())
                            return@namedForEach
                        }
                    }
                }

                mBinding.llInvestList.addView(itemBinding.root)
                mInvestInputLayoutBindingList.add(itemBinding)

            }

            mEarnProduct.earnProduct.productIncomeList.forEach {income ->
                // 根据回报币种 动态生成参考年华
                val incomeProfitLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, mBinding.llMixProductIncomeCoinsProfitRate, false)

                incomeProfitLayoutBinding.tvCurrencyName.text = income.currencyName
                incomeProfitLayoutBinding.tvInfo.text = (income.actualRate * 100).toString().getNum(2) + "%"
                mBinding.llMixProductIncomeCoinsProfitRate.addView(incomeProfitLayoutBinding.root)
                mIncomeProfitRateLayoutBindingList.add(incomeProfitLayoutBinding)

                if (mEarnProduct.isHasDeadline()){
                    // 预期利息
                    val incomeInterestLayoutBinding = LayoutEarnIncomeInfoBinding.inflate(layoutInflater, mBinding.llMixProductIncomeCoinsInterest, false)
                    incomeInterestLayoutBinding.tvCurrencyName.text = income.currencyName
                    mBinding.llMixProductIncomeCoinsInterest.addView(incomeInterestLayoutBinding.root)
                    mIncomeInterestLayoutBindingList.add(incomeInterestLayoutBinding)
                }

            }

        }

    }

    private fun checkInput() {
        if (!mEarnProduct.isMixProduct()){
            mBinding.btnBuy.isEnabled = mAmount.get()!!.isNotEmpty()
        } else {
            mInvestInputLayoutBindingList.forEach {
                val inputAmount = it.amount!!.get()
                if (inputAmount?.isEmpty() == true) {
                    mBinding.btnBuy.isEnabled = false
                    return@checkInput
                }
            }
            mBinding.btnBuy.isEnabled = true
        }
    }

    // 计算混合输入预期收益
    private fun calcMixProfit() {

        mInvestInputLayoutBindingList.forEach {
            val inputAmount = it.amount!!.get()
            val amount = inputAmount!!.getDouble()
            it.investCoin!!.investTotalAmount = amount
        }

        mIncomeInterestLayoutBindingList.forEachIndexed { index, binding ->

            binding.tvInfo.text = mEarnProduct.getMixExpectedProfit(index)

        }

    }

    private fun buyMixProduct() {
        var productId = 0
        val inputAmountArray = mutableListOf<Pair<Int,String>>()
        mInvestInputLayoutBindingList.forEach {binding ->
            binding.investCoin?.let {

                if (binding.etAmount1.text.toString().getDouble() > 0){
                    productId = it.productId
                    inputAmountArray.add(it.currencyId to binding.etAmount1.text.toString())
                }

            }
        }
        mViewModel.buyEarn(productId, *inputAmountArray.toTypedArray())
    }

    private fun buyProduct() {
        val amount = mBinding.etAmount1.text.toString()

        mInvestProduct?.let {
            if (amount.getDouble() > it.maxAmount) {
                MToast.show(context, getString(R.string.earn_buy_hint_min, it.currencyName, it.maxAmount))
                return
            } else if (amount.getDouble() < it.minAmount) {
                MToast.show(context, getString(R.string.earn_buy_hint_max, it.currencyName, it.minAmount))
                return
            }
            mViewModel.buyEarn(it.productId, Pair(it.currencyId, amount))
        }
    }

    private fun initData() {
        mViewModel.fetchEarnAccount()
    }

    private fun initLiveData() {
        mViewModel.mCoinsEarnAccountLiveData.observe(viewLifecycleOwner, Observer {
            mAllCoinAccount = it
            updateAvailable(it)
        })

        mViewModel.mBuySuccessLiveData.observe(viewLifecycleOwner, Observer {amount ->
            activity?.finish()
            EarnBuySuccessActivity.launch(requireContext(), mEarnProduct.earnProduct, amount, mTimeLimit)
            EventBus.getDefault().post(BizEvent.Earn.EarnBuySuccess())
        })
    }

    private fun updateAvailable(accounts: List<EarnAccountCoin>) {
        accounts.forEach {
            if (it.coinId == mEarnProduct.getInvestCoinId()) {
                mCoinAccountInfo = it
                return@forEach
            }
        }

        if (mEarnProduct.isMixProduct()) {
            // 混合产品 给每个input layout的可用余额赋值
            mInvestInputLayoutBindingList.forEach { binding ->
                binding.tvAvailableValue1.text = accounts.let {
                    var earnAccountCoin: EarnAccountCoin? = null

                    it.forEach namedForEach@{
                        if (it.coinId == binding.investCoin!!.currencyId) {
                            earnAccountCoin = it
                            return@namedForEach
                        }
                    }

                    if (earnAccountCoin == null){
                        "0 ${binding.investCoin!!.currencyName.toUpperCase()}"
                    } else {
                        "${earnAccountCoin?.availableBalance.toString().getNum(8)} ${binding.investCoin!!.currencyName.toUpperCase()}"
                    }
                }
            }
        } else {
            mBinding.tvAvailableValue1.text = "${mCoinAccountInfo?.availableBalance.toString().getNum(8)} ${mEarnProduct.getInvestCurrencyName()}"
        }

    }

    /**
     * index：选中了第几个投资期限 不同的投资期限对应的productId不一样
     */
    private fun applyProduct(index :Int){
        if (index >= mTimeLimitList.size || index >= mEarnProduct.earnProduct.productIncomeList.size){
            return
        }

        mEarnProduct.index = index

        mAmount.set("")

        updateAvailable(mAllCoinAccount)

        mTimeLimit = mTimeLimitList[index]
        mInvestProduct = mEarnProduct.earnProduct.productInvestList[index]

        if (mEarnProduct.isMixRegularProduct()) {
            mBinding.layoutEarnBuyRule.tvProfitArrivalDate.text = getString(R.string.earn_profit_arrival_date)
            mBinding.layoutEarnBuyRule.tvProfitArrivalDateValue.text = TimeUtils.date2String(Date(now + day * (mTimeLimitList[index] + 1)), "yyyy-MM-dd")
        } else {
            mBinding.layoutEarnBuyRule.tvProfitArrivalDate.text = getString(R.string.earn_profit_yesterday)
            mBinding.layoutEarnBuyRule.tvProfitArrivalDateValue.text = TimeUtils.date2String(Date(now + day * 2), "yyyy-MM-dd")
        }

    }


}