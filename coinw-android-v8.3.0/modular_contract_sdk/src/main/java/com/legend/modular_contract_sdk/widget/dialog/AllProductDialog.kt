package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.databinding.McSdkDialogDrawerAllProductBinding
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.ProductTicker
import com.lxj.xpopup.core.DrawerPopupView

class AllProductDialog(
    private val products: List<Product>,
    context: Context,
    private val dismissCallBack: (Product) -> Unit
) : DrawerPopupView(context) {

    private lateinit var mBinding: McSdkDialogDrawerAllProductBinding

    private var mMarketListenerList = mutableListOf<MarketListener>()

    override fun onCreate() {
        super.onCreate()
        // DrawerPopupView 层次结构跟其他的PopupView不一样 所以需要在获取一下子View才能使用DataBinding
        // 可以联系一下作者 修改下
        var rootView = ((popupContentView as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(0)
        mBinding = McSdkDialogDrawerAllProductBinding.bind(rootView)
        val productTickers = products.map {
            ProductTicker(it)
        }
        mBinding.rvList.adapter = DataBindingRecyclerViewAdapter(
            context,
            R.layout.mc_sdk_item_product,
            BR.ticker,
            productTickers
        ).apply {
            setOnItemClickListener { _, position ->
                dismissWith {
                    dismissCallBack(products[position])
                }
            }
        }



        for (product in products) {
            mMarketListenerList.add(
                MarketListenerManager.subscribe(
                    MarketSubscribeType.TickerSwap(product.mBase, "usd"),
                    MutableLiveData()
                )
            )
        }

        mMarketListenerList.forEachIndexed { index, marketListener ->
            marketListener.liveData.observe(context as LifecycleOwner, Observer {
                val ticker = it as Ticker
                productTickers[index].innerTicker = ticker
            })
        }


    }

    override fun getImplLayoutId(): Int = R.layout.mc_sdk_dialog_drawer_all_product

    override fun getPopupWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    override fun onDismiss() {
        mMarketListenerList.forEach {
            MarketListenerManager.unsubscribe(it)
        }
        super.onDismiss()
    }

}