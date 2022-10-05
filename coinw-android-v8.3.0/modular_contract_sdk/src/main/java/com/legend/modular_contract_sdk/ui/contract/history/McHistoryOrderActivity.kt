package com.legend.modular_contract_sdk.ui.contract.history

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.event.GetSharePicEvent
import com.legend.modular_contract_sdk.common.showImageShareDialog
import com.legend.modular_contract_sdk.common.showLoading
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithBaseQuickAdapter
import com.legend.modular_contract_sdk.component.adapter.setupWithRecyclerView
import com.legend.modular_contract_sdk.databinding.McSdkActivityHistoryOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemHistoryOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkViewSharePositionNewBinding
import com.legend.modular_contract_sdk.onekeyshare.CreateQRImage
import com.legend.modular_contract_sdk.repository.model.BaseResp
import com.legend.modular_contract_sdk.repository.model.McBasePage
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.enum.McContractModeEnum
import com.legend.modular_contract_sdk.repository.model.wrap.OrderWrap
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.mc_sdk_view_tool_bar.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 历史仓位页面
 */
class McHistoryOrderActivity : BaseActivity<McHistoryOrderViewModel>() {

    lateinit var mBinding: McSdkActivityHistoryOrderBinding

    lateinit var mAdapter: DataBindingRecyclerViewAdapter<PositionWrap>

    private lateinit var mCurrentTradeUnit : QuantityUnit

    companion object{
        fun launch(context:Context){
            val intent=Intent(context,McHistoryOrderActivity::class.java)
//            intent.putExtra("quantity_unit", quantityUnit.unit)
            context.startActivity(intent)
        }
    }

    var mIsExperienceGold:Boolean = false

    override fun createViewModel(): McHistoryOrderViewModel {
        return ViewModelProvider(this).get(McHistoryOrderViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.mc_sdk_activity_history_order)

        initTradeUnit()

        mBinding.recycerView.layoutManager = LinearLayoutManager(this)

        mAdapter = DataBindingRecyclerViewAdapter(this, R.layout.mc_sdk_item_history_order, BR.position, mutableListOf())
        mAdapter.setOnBindingViewHolderListener { holder, position ->
            val itemBinding = holder.getBinding<McSdkItemHistoryOrderBinding>()
            itemBinding.tradeUnit = mCurrentTradeUnit
            itemBinding.tvId.setOnClickListener {
                McClipboardUtils.copyingToClipboard(this, itemBinding.position?.position?.mOpenId?.toString())
                ToastUtils.showShortToast(R.string.mc_sdk_copy_success)
            }

            itemBinding.ivShare.setOnClickListener {
                itemBinding.position?.let {
                    showShare(it)
                }
            }

        }

        setEmptyView()

        mBinding.recycerView.adapter = mAdapter
        mBinding.toolBar.tvTitle.text=resources.getString(R.string.mc_sdk_contract_history_order)
        mBinding.toolBar.tvTitle.textSize=18f
        mBinding.toolBar.ivLeftIcon.setOnClickListener { finish() }//不知道toolbar框架会如何，先写好功能

        mBinding.rgPositionType.setOnCheckedChangeListener{group, checkedId ->
            when (checkedId) {
                R.id.rb_swap_contract-> {
                    mIsExperienceGold = false
                }
                R.id.rb_experience_gold-> {
                    mIsExperienceGold = true
                }
            }
            mAdapter.cleanData()
            getData()
        }

        initObserver()
        getData()
        setupWithRecyclerView<PositionAndOrder, PositionWrap>(this,
                mBinding.recycerView,
                { getOrderListMore() },
                {page-> getOrderListMore(page) },
                useWrap = true,
                wrapTranslationAction = {
                    PositionWrap(it)
                }
        )
    }

    private fun initObserver(){
        getViewModel().mDataLiveData.observe(this, androidx.lifecycle.Observer {
            mAdapter.refreshData(it.map {
                PositionWrap(it)
            })
        })
    }

    private fun getData(){
        if (mAdapter==null) return
        getViewModel().fetchOrderList(1, mIsExperienceGold)
    }

    private suspend fun getOrderListMore(page:Int=1): BaseResp<McBasePage<PositionAndOrder>>{
        return getViewModel().getOrderListMore(page, mIsExperienceGold)
    }

    private fun showShare(positionWrap: PositionWrap) {
        lifecycleScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            Logger.e("share errror")
        }) {
            val dialog = showLoading(this@McHistoryOrderActivity)

            val shareView = layoutInflater.inflate(R.layout.mc_sdk_view_share_position_new, mBinding.recycerView, false)

            val binding = McSdkViewSharePositionNewBinding.bind(shareView)

            binding.isHistoryOrder = true
            binding.position = positionWrap
            binding.executePendingBindings()

            val bitmap = withContext(Dispatchers.IO){

                val sharePicResult = async {
                    suspendCoroutine<String> {
                        EventBus.getDefault().post(GetSharePicEvent{ isSuccess, result ->
                            it.resume(result)
                        })
                    }
                }.await()

                Logger.e("shareInfo: $sharePicResult")

                val qrcodeSize = ViewUtil.dip2px(this@McHistoryOrderActivity, 52f)

                val qrLogo = BitmapFactory.decodeResource(this@McHistoryOrderActivity.resources, R.drawable.mc_sdk_qr_logo)

                binding.ivQrcode.setImageBitmap(CreateQRImage.createQRCodeWithLogo( sharePicResult, qrcodeSize, qrLogo))

                shareView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                shareView.layout(0, 0,
                        shareView.measuredWidth,
                        shareView.measuredHeight)

                val shareBitmap = Bitmap.createBitmap(shareView.measuredWidth, shareView.measuredHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(shareBitmap)
                shareView.draw(canvas)
                shareBitmap
            }

            dialog.dismiss()
            showImageShareDialog(this@McHistoryOrderActivity, bitmap)

        }
    }

    private fun setEmptyView(){
        val view=LayoutInflater.from(this).inflate(R.layout.mc_sdk_view_empty, mBinding.recycerView, false)
        val tvNoData=view.findViewById<TextView>(R.id.tvNoData)
        val tvNoLogin=view.findViewById<TextView>(R.id.tvNoLogin)
        if(::mAdapter.isInitialized){
            tvNoData.visible()
            tvNoLogin.gone()
            mAdapter.setEmptyView(view)
        }
    }

    private fun initTradeUnit(){
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mCurrentTradeUnit = QuantityUnit.SIZE
            }
            QuantityUnit.USDT.unit -> {
                mCurrentTradeUnit = QuantityUnit.USDT
            }
            QuantityUnit.COIN.unit -> {
                mCurrentTradeUnit = QuantityUnit.COIN
            }
        }
    }
}