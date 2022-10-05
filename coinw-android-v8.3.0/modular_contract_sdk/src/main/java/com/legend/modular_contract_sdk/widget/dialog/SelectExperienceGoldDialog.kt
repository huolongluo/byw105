package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.view.View
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.common.event.ShowExperienceGildExplainEvent
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.databinding.McSdkDialogSelectExperienceGoldBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemExperienceGoldBinding
import com.legend.modular_contract_sdk.repository.model.ExperienceGold
import com.legend.modular_contract_sdk.repository.model.wrap.ExperienceGoldWrap
import com.legend.modular_contract_sdk.utils.ViewUtil
import com.legend.modular_contract_sdk.widget.decoration.SpaceItemDecoration
import com.lxj.xpopup.core.BottomPopupView
import org.greenrobot.eventbus.EventBus

class SelectExperienceGoldDialog(context: Context, val experienceGoldList: List<ExperienceGold>,val currentSelectedGold: ExperienceGold?, val onConfirm: (isUse: Boolean, ExperienceGoldWrap?) -> Unit) : BottomPopupView(context) {

    lateinit var mBinding: McSdkDialogSelectExperienceGoldBinding

    lateinit var mAdapter: DataBindingRecyclerViewAdapter<ExperienceGoldWrap>

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_select_experience_gold

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogSelectExperienceGoldBinding.bind(popupImplView)

        mBinding.tvNotUse.setOnClickListener {
            onConfirm.invoke(false, null)
            dismiss()
        }

        mBinding.ivQuestion.setOnClickListener {
            EventBus.getDefault().post(ShowExperienceGildExplainEvent())
        }

        mAdapter = DataBindingRecyclerViewAdapter<ExperienceGoldWrap>(context,
                R.layout.mc_sdk_item_experience_gold,
                BR.expGold,
                experienceGoldList.map { ExperienceGoldWrap(it) }).apply {
            mBinding.rvList.adapter = this

            this.setOnItemClickListener{view, position ->
                onConfirm.invoke(true, allData[position])
                dismiss()
            }

            setOnBindingViewHolderListener { holder, position ->
                val binding = holder.getBinding<McSdkItemExperienceGoldBinding>()
                if (currentSelectedGold != null && currentSelectedGold.mId == binding.expGold!!.experienceGold.mId) {
                    binding.ivSelected.visibility = View.VISIBLE
                } else {
                    binding.ivSelected.visibility = View.GONE
                }
                binding.btnAction.setOnClickListener {
                    onConfirm.invoke(true, allData[position])
                    dismiss()
                }
            }


        }


        val space = ViewUtil.dip2px(context, 10.0f)

        mBinding.rvList.addItemDecoration(SpaceItemDecoration(space, space, space, space, null, false, true))


    }
}