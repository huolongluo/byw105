package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.common.showMessageDialog
import com.legend.modular_contract_sdk.component.market_listener.*
import com.legend.modular_contract_sdk.databinding.McSdkDialogModifyTpSlBinding
import com.legend.modular_contract_sdk.databinding.McSdkFragmentModifyPositionTpSlBinding
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.modify_tp_sl.ModifyMoveTP_SLFragment
import com.legend.modular_contract_sdk.ui.contract.modify_tp_sl.ModifyPositionTP_SLFragment
import com.legend.modular_contract_sdk.utils.getDoubleValue
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.lxj.xpopup.core.BottomPopupView


class ModifyStopProfitAndLossDialog(
        context: Context,
        val fragment:Fragment,
        val position: PositionAndOrder,
        val product: Product,
        val showMoveTP_SL:Boolean = false
) : BottomPopupView(context) {

    lateinit var mBinding: McSdkDialogModifyTpSlBinding


    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_modify_tp_sl


    override fun onCreate() {
        super.onCreate()

        mBinding = McSdkDialogModifyTpSlBinding.bind(popupImplView)

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        if (showMoveTP_SL){
            mBinding.ivQuestion.visibility = View.VISIBLE
        } else {
            mBinding.ivQuestion.visibility = View.GONE
        }

        mBinding.ivQuestion.setOnClickListener {
            showMessageDialog(context,
                    title = context.getString(R.string.mc_sdk_modify_move_tp_sl),
                    content = context.getString(R.string.mc_sdk_modify_move_tp_sl_desc),
                    cancel = false,
                    confirmText = context.getString(R.string.mc_sdk_confirm)
            ){

            }
        }

        val fragment1 = ModifyPositionTP_SLFragment.getInstance(position, product, this)
        val fragment2 = ModifyMoveTP_SLFragment.getInstance(position, product, this)

        mBinding.vp.adapter = object : FragmentStateAdapter(fragment){
            override fun getItemCount() = if (showMoveTP_SL){
                2
            } else {
                1
            }

            override fun createFragment(position: Int): Fragment {
                return if (position == 0){
                    fragment1
                } else {
                    fragment2
                }
            }

        }

        val titles = mutableListOf<String>(context.getString(R.string.mc_sdk_modify_position_tp_sl), context.getString(R.string.mc_sdk_modify_move_tp_sl))

        TabLayoutMediator(mBinding.tab, mBinding.vp) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }


}