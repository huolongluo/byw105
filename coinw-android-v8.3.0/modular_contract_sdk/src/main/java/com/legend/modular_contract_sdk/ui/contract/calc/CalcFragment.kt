package com.legend.modular_contract_sdk.ui.contract.calc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.showSelectItemDialog
import com.legend.modular_contract_sdk.databinding.McSdkFragmentCalcBinding
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.SPUtils
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum
import com.legend.modular_contract_sdk.utils.getPrecision
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.legend.modular_contract_sdk.utils.inputfilter.MaxInputValidator

class CalcFragment : BaseFragment<BaseViewModel>() {

    companion object {
        fun getInstance(calcType: CalcType, productList: List<Product>) = CalcFragment().apply {
            arguments = Bundle().apply {
                putSerializable("calc_type", calcType)
                putSerializable("product_list", ArrayList(productList))
            }
        }
    }

    private lateinit var mBinding: McSdkFragmentCalcBinding

    private lateinit var mProductList: List<Product>

    private lateinit var mCurrentProduct: Product

    private val mLeverage = ObservableField<String>("10")
    private val mOpen = ObservableField<String>("")
    private val mClose = ObservableField<String>("")
    private val mAmount = ObservableField<String>("")
    private val mProfitAndMargin = ObservableField<String>("")

    var mCalcType = CalcType.CALC_PROFIT

    //方向： 多：1 空 -1
    var mDirection = 1

    var mPositionMode = PositionMode.FULL

    override fun createViewModel() = ViewModelProvider(this).get(BaseViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = McSdkFragmentCalcBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mCalcType = it.getSerializable("calc_type") as CalcType
            mProductList = it.getSerializable("product_list") as ArrayList<Product>
        }

        mBinding.calcType = mCalcType
        mBinding.leverage = mLeverage
        mBinding.open = mOpen
        mBinding.close = mClose
        mBinding.amount = mAmount
        mBinding.profitAndMargin = mProfitAndMargin

        applyProduct(mProductList[0])
        initView()
    }


    private fun initView() {

        mBinding.tvProductName.setOnClickListener { view->

            view.isClickable = false
            view.postDelayed({ view.isClickable = true }, 500)

            val titles = mProductList.map { it.getProductName() }.toTypedArray()
            showSelectItemDialog(requireContext(), titles) { index, text ->
                applyProduct(mProductList[index])
                applyInputFilters()
            }
        }

        mBinding.btnCalc.setOnClickListener {
            calc()
        }

        mBinding.rgDirection.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_direction_long -> {
                    mDirection = 1
                }
                R.id.rb_direction_short -> {
                    mDirection = -1
                }
            }
        }

        mBinding.tvPositionMode.setOnClickListener {
            mBinding.tvPositionMode.text = if (mPositionMode == PositionMode.FULL) {
                mPositionMode = PositionMode.PART
                mBinding.llMargin.visibility = View.GONE
                getString(R.string.mc_sdk_contract_order_type_part)
            } else {
                mPositionMode = PositionMode.FULL
                mBinding.llMargin.visibility = View.VISIBLE
                getString(R.string.mc_sdk_contract_order_type_full)
            }
            checkInput()
        }

        val onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                checkInput()
            }
        }

        applyInputFilters()

        mLeverage.addOnPropertyChangedCallback(onPropertyChangedCallback)
        mOpen.addOnPropertyChangedCallback(onPropertyChangedCallback)
        mClose.addOnPropertyChangedCallback(onPropertyChangedCallback)
        mAmount.addOnPropertyChangedCallback(onPropertyChangedCallback)
        mProfitAndMargin.addOnPropertyChangedCallback(onPropertyChangedCallback)

    }

    private fun applyInputFilters() {
        mBinding.etLeverage.filters = arrayOf(MaxInputValidator(mCurrentProduct.mMaxLeverage))
        mBinding.etOpen.filters = arrayOf(DecimalDigitsInputFilter(10, mCurrentProduct.mPricePrecision))
        mBinding.etClose.filters = arrayOf(DecimalDigitsInputFilter(10, mCurrentProduct.mPricePrecision))
        mBinding.etProfitAndMargin.filters = arrayOf(DecimalDigitsInputFilter(10, 4))
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mBinding.etAmount.filters = arrayOf(DecimalDigitsInputFilter(10, 0))
            }
            QuantityUnit.USDT.unit -> {
                mBinding.etAmount.filters = arrayOf(DecimalDigitsInputFilter(10, 4))
            }
            QuantityUnit.COIN.unit -> {
                mBinding.etAmount.filters = arrayOf(DecimalDigitsInputFilter(10, mCurrentProduct.mOneLotSize.getPrecision()))
            }
        }
    }

    private fun applyProduct(product: Product) {
        mCurrentProduct = product
        mBinding.tvProductName.text = product.getProductName() + getString(R.string.mc_sdk_contract_permanent)
        mLeverage.set("")
        mOpen.set("")
        mClose.set("")
        mAmount.set("")
        mProfitAndMargin.set("")

        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mBinding.tvAmountUnit.text = getString(R.string.mc_sdk_contract_unit)
            }
            QuantityUnit.USDT.unit -> {
                mBinding.tvAmountUnit.text = getString(R.string.mc_sdk_usdt)
            }
            QuantityUnit.COIN.unit -> {
                mBinding.tvAmountUnit.text = mCurrentProduct.mBase.toUpperCase()
            }
        }

        mBinding.tvResultMargin.text = "--"
        mBinding.tvResultProfit.text = "--"
        mBinding.tvResultProfitRate.text = "--"
        mBinding.tvResultMakerFee.text = "--"
        mBinding.tvResultTakerFee.text = "--"
        mBinding.tvResultClose.text = "--"
    }

    private fun calc() {
        when (mCalcType) {
            CalcType.CALC_PROFIT -> {
                calcProfit()
            }
            CalcType.CALC_CLOSE_PRICE -> {
                calcClosePrice()
            }
            CalcType.CALC_LIQUIDATION -> {
                calcLiquidation()
            }
        }
    }

    /**
    仓位价值 = 张数 * oneLotSize * 开仓价
    方向： 多：1 空 -1

    1. 收益：

    1.1  保证金 = 仓位价值 / 杠杆倍数
    1.2  收益 = (平仓价 - 开仓价) * 方向 * 张数 * oneLotSize
    1.3  收益率 = 收益 / 保证金
    1.4  Maker 手续费 = 仓位价值 * MakerFee
    1.5  Taker 手续费 = 仓位价值 * TakerFee

     */
    private fun calcProfit() {
        val open = mOpen.get().getDouble()
        val close = mClose.get().getDouble()
        var amount = mAmount.get().getDouble()
        val leverage = mLeverage.get().getDouble()

        amount = getAmount(open)


        val positionValue = amount * mCurrentProduct.mOneLotSize * open
        val margin = positionValue / leverage
        val profit = ((close - open) * mDirection * amount * mCurrentProduct.mOneLotSize)

        mBinding.tvResultMargin.text = margin.toString().getNum(4) + " " + getString(R.string.mc_sdk_usdt)
        mBinding.tvResultProfit.text = profit.toString().getNum(4) + " " + getString(R.string.mc_sdk_usdt)
        mBinding.tvResultProfitRate.text = (profit / margin * 100).toString().getNum(2) + " " + "%"
        mBinding.tvResultMakerFee.text = (positionValue * mCurrentProduct.mMakerFee).toString().getNum(4) + " " + getString(R.string.mc_sdk_usdt)
        mBinding.tvResultTakerFee.text = (positionValue * mCurrentProduct.mTakerFee).toString().getNum(4) + " " + getString(R.string.mc_sdk_usdt)
    }

    // 其他单位换算为张 抹掉小数部分
    private fun getAmount(open: Double): Double {
        var amount = mAmount.get().getDouble()
        return when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                amount
            }
            QuantityUnit.USDT.unit -> {
                (amount / open / mCurrentProduct.mOneLotSize).toInt().toDouble()
            }
            QuantityUnit.COIN.unit -> {
                (amount / mCurrentProduct.mOneLotSize).toInt().toDouble()
            }
            else -> {
                amount
            }
        }
    }

    private fun calcClosePrice() {
//        2. 平仓价：
//        收益 / (张数 * oneLotSize) * 方向 + 开仓价
        val open = mOpen.get().getDouble()
        val amount = getAmount(open)
        val profit = mProfitAndMargin.get().getDouble()
        mBinding.tvResultClose.text = (profit / (amount * mCurrentProduct.mOneLotSize) * mDirection + open).toString().getNum(mCurrentProduct.mPricePrecision) + " " + getString(R.string.mc_sdk_usdt)
    }

    private fun calcLiquidation() {

//        3. 强平价：
//        保证金： 全仓为输入，逐仓为 仓位价值 / 杠杆倍数
//        最大亏损 = 维持保证金率 * (张数 * oneLotSize) * 开仓价 / 杠杆倍数 - 保证金
//        强平价 = 最大亏损 / (张数 * oneLotSize) * 方向 + 开仓价
        val leverage = mLeverage.get().getDouble()
        val open = mOpen.get().getDouble()
        val amount = getAmount(open)
        var margin = if (mPositionMode == PositionMode.FULL) {
            mProfitAndMargin.get().getDouble()
        } else {
            amount * mCurrentProduct.mOneLotSize * open / leverage
        }
        // 最大亏损
        val maxLoss = mCurrentProduct.mStopSurplusRate * (amount * mCurrentProduct.mOneLotSize) * open / leverage - margin
        mBinding.tvResultClose.text = (maxLoss / (amount * mCurrentProduct.mOneLotSize) * mDirection + open).toString().getNum(mCurrentProduct.mPricePrecision) + " " + getString(R.string.mc_sdk_usdt)
    }

    private fun checkInput() {
        val enable: Boolean = when (mCalcType) {
            CalcType.CALC_PROFIT -> {
                mOpen.get()!!.isNotEmpty() && mClose.get()!!.isNotEmpty() && mAmount.get()!!.isNotEmpty() && mLeverage.get()!!.isNotEmpty()
            }
            CalcType.CALC_CLOSE_PRICE -> {
                mOpen.get()!!.isNotEmpty() && mAmount.get()!!.isNotEmpty() && mProfitAndMargin.get()!!.isNotEmpty()
            }
            CalcType.CALC_LIQUIDATION -> {
                mOpen.get()!!.isNotEmpty() && (mPositionMode == PositionMode.PART || mProfitAndMargin.get()!!.isNotEmpty()) && mAmount.get()!!.isNotEmpty() && mLeverage.get()!!.isNotEmpty()
            }
        }
        mBinding.btnCalc.isEnabled = enable
    }

}