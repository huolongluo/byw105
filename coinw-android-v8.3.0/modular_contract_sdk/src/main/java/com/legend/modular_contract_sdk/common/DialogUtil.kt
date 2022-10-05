package com.legend.modular_contract_sdk.common

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.common.event.SelectExperienceGoldEvent
import com.legend.modular_contract_sdk.databinding.McSdkDialogUseExperienceGoldBinding
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import com.legend.modular_contract_sdk.repository.model.ExperienceGold
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.ExperienceGoldWrap
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.*
import com.legend.modular_contract_sdk.ui.contract.calc.ContractCalculatorActivity
import com.legend.modular_contract_sdk.ui.contract.settings.PreferencesSettingActivity
import com.legend.modular_contract_sdk.ui.experience_gold.ExperienceGoldActivity
import com.legend.modular_contract_sdk.widget.dialog.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.enums.PopupPosition
import org.greenrobot.eventbus.EventBus
import java.util.*

fun showLoading(context: Context): BasePopupView {
    return XPopup.Builder(context)
            .asLoading()
            .show()
}

fun showSelectProductDialog(
        context: Context,
        products: List<Product>,
        onSelectCallback: (Product) -> Unit
) {
    XPopup.Builder(context)
            .popupPosition(PopupPosition.Left)//右边
            .hasStatusBarShadow(true) //启用状态栏阴影
            .maxWidth(400)
            .asCustom(AllProductDialog(products, context) {
                onSelectCallback(it)
            })
            .show()
}

/**
 * 通用dialog
 */
fun showMessageDialog(
        context: Context, title: String? = null, subtitle: String? = null, content: String? = null,
        cancel: Boolean = true, confirm: Boolean = true,
        cancelText: String? = null, confirmText: String? = null,
        onConfirm: () -> Unit
) {
    XPopup.Builder(context)
            .asCustom(object : CenterPopupView(context) {
                override fun getImplLayoutId() = R.layout.mc_sdk_dialog_message_with_confirm_cancel

                override fun onCreate() {
                    super.onCreate()

                    findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                        onConfirm()
                        dismiss()
                    }
                    findViewById<Button>(R.id.btn_cancel).setOnClickListener { dismiss() }
                    findViewById<TextView>(R.id.tv_title).text = title
                    findViewById<TextView>(R.id.tv_title).visibility =
                            if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
                    findViewById<TextView>(R.id.tv_sub_title).text = subtitle
                    findViewById<TextView>(R.id.tv_sub_title).visibility =
                            if (subtitle.isNullOrEmpty()) View.GONE else View.VISIBLE
                    findViewById<TextView>(R.id.tv_content).text = content
                    findViewById<TextView>(R.id.tv_content).visibility =
                            if (content.isNullOrEmpty()) View.GONE else View.VISIBLE
                    findViewById<Button>(R.id.btn_cancel).text = cancelText
                            ?: context.getString(R.string.mc_sdk_cancel)
                    findViewById<Button>(R.id.btn_cancel).visibility = if (cancel) View.VISIBLE else View.GONE
                    findViewById<Button>(R.id.btn_confirm).text = confirmText
                            ?: context.getString(R.string.mc_sdk_confirm)
                    findViewById<Button>(R.id.btn_confirm).visibility = if (confirm) View.VISIBLE else View.GONE

                }
            }).show()
}

fun showClosePartPositionDialog(
        context: Context,
        tradeUnit: QuantityUnit,
        position: PositionWrap,
        onConfirm: (PositionType, price:String, count:Double, inputCount:String) -> Unit
) {
    XPopup.Builder(context)
            .autoOpenSoftInput(true)
            .asCustom(ClosePartPositionDialog(context, tradeUnit, position, onConfirm))
            .show()
}


fun showClosePositionConfirmDialog(context: Context, price:String, count: String,  onConfirm: (notShow: Boolean) -> Unit){
    XPopup.Builder(context)
            .autoOpenSoftInput(false)
            .asCustom(ClosePositionConfirmDialog(context, price, count, onConfirm))
            .show()
}

fun showAddPositionDialog(context: Context, onConfirm: (Double, AddPositionType) -> Unit) {
    XPopup.Builder(context)
            .autoOpenSoftInput(true)
            .asCustom(AddPositionDialog(context, onConfirm))
            .show()
}

fun showModifyLeverageDialog(
        context: Context,
        currentPositionMode: PositionMode,
        currentPositionMergeMode: PositionMergeMode,
        currentLeverage: Int,
        maxLeverage: Int,
        currentLeverageMaxOpen: (Int) -> String,
        onConfirm: (PositionMode, PositionMergeMode, Int) -> Unit
) {
    XPopup.Builder(context)
            .autoOpenSoftInput(true)
            .asCustom(ModifyLeverageDialog(context, currentPositionMode, currentPositionMergeMode, currentLeverage, maxLeverage, currentLeverageMaxOpen, onConfirm))
            .show()
}

fun showModifyStopProfitAndLoss(
        context: Context,
        fragment: Fragment,
        position: PositionAndOrder,
        product: Product,
        showMoveTP_SL:Boolean = false
) {
    XPopup.Builder(context)
            .enableDrag(false)
            .autoOpenSoftInput(true)
            .asCustom(ModifyStopProfitAndLossDialog(context, fragment, position, product, showMoveTP_SL))
            .show()
}

fun showSelectExperienceGoldDialog(context: Context, experienceGoldList: List<ExperienceGold>, currentSelectedGold: ExperienceGold?, onConfirm: (isUse: Boolean, ExperienceGoldWrap?) -> Unit) {
    XPopup.Builder(context)
            .enableDrag(false)
            .autoOpenSoftInput(true)
            .asCustom(SelectExperienceGoldDialog(context, experienceGoldList, currentSelectedGold, onConfirm))
            .show()
}

fun showUseExperienceGoldDialog(context: Context, experienceGold: ExperienceGold) {
    XPopup.Builder(context)
            .enableDrag(false)
            .hasShadowBg(true)
            .borderRadius(20f)
            .asCustom(object : CenterPopupView(context) {
                override fun getImplLayoutId() = R.layout.mc_sdk_dialog_use_experience_gold

                override fun onCreate() {
                    super.onCreate()
                    val binding = McSdkDialogUseExperienceGoldBinding.bind(popupImplView)
                    binding.expGold = ExperienceGoldWrap(experienceGold)

                    binding.btnCancel.setOnClickListener {
                        dismiss()
                    }

                    binding.btnConfirm.setOnClickListener {
                        ExperienceGoldActivity.launch(context)
                        dismiss()
                    }
                }
            })
            .show()
}

fun showImageShareDialog(context: Context, shareBitmap: Bitmap) {
    XPopup.Builder(context)
            .enableDrag(false)
            .hasShadowBg(true)
            .asCustom(ShareDialog(context, shareBitmap))
            .show()
}

fun showContractMenuDialog(context: Context, attachView: View, products: List<Product>) {
    XPopup.Builder(context)
            .atView(attachView)
            .hasShadowBg(true)
            .asCustom(object : AttachPopupView(context) {
                override fun getImplLayoutId(): Int {
                    return R.layout.mc_sdk_view_contract_menu
                }

                override fun onCreate() {
                    super.onCreate()
                    findViewById<TextView>(R.id.tv_preferences_setting).setOnClickListener {
                        PreferencesSettingActivity.launch(context)
                        dismiss()
                    }
                    findViewById<TextView>(R.id.tv_calculator).setOnClickListener {
                        ContractCalculatorActivity.launch(context, products)
                        dismiss()
                    }
                }
            })
            .show()

}

fun showSwitchTradeUnitDialog(context:Context, attachView: View, onConfirm: (unit:QuantityUnit) -> Unit){
    XPopup.Builder(context)
            .atView(attachView)
            .hasShadowBg(true)
            .asCustom(object : AttachPopupView(context) {
                override fun getImplLayoutId(): Int {
                    return R.layout.mc_sdk_dialog_switch_trade_unit
                }

            })
            .show()
}

fun showOrderConfirmDialog(context: Context, direction: String, triggerPrice: String = "", price: String, count: String, onConfirm: (notShow: Boolean) -> Unit) {
    XPopup.Builder(context)
            .enableDrag(false)
            .autoOpenSoftInput(false)
            .asCustom(OrderConfirmDialog(context, direction, triggerPrice, price, count, onConfirm))
            .show()
}

fun showModifyPositionMarginDialog(context: Context, position: PositionWrap, usableBalances: ContractAssetInfo, onConfirm: (amount:String, type:Int) -> Unit){
    XPopup.Builder(context)
            .enableDrag(false)
            .autoOpenSoftInput(true)
            .asCustom(ModifyPositionMarginDialog(context, usableBalances, position, onConfirm))
            .show()
}

fun showSelectItemDialog(context: Context, tiems: Array<String>, onConfirm: (index:Int,text:String) -> Unit){
    XPopup.Builder(context)
            .enableDrag(false)
            .autoOpenSoftInput(false)
            .asCustom(SelectItemDialog(context, tiems, onConfirm)).apply {
                if (!isShow){
                    show()
                }
                isShown
            }
}