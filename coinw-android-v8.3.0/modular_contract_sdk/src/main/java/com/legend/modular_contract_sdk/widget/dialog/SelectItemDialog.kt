package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.widget.decoration.GridSpacingItemDecoration
import com.lxj.xpopup.core.BottomPopupView

class SelectItemDialog(context: Context, val items: Array<String>, val onConfirm: (index: Int, text:String) -> Unit): BottomPopupView(context){
    override fun getImplLayoutId(): Int = R.layout.mc_sdk_dialog_select_item

    override fun onCreate() {
        super.onCreate()
        val rvItem = findViewById<RecyclerView>(R.id.rv_item)
        val tvCancel = findViewById<TextView>(R.id.tv_cancel)

        rvItem.addItemDecoration(GridSpacingItemDecoration(1, 1, false, true, ThemeUtil.getThemeColor(context, R.attr.divider_line)))

        rvItem.adapter = DataBindingRecyclerViewAdapter<String>(context, R.layout.mc_sdk_item_text, BR.text, items.toList()).apply {
            setOnItemClickListener { view, position ->
                onConfirm(position, items[position])
                dismiss()
            }
        }

        tvCancel.setOnClickListener {
            dismiss()
        }
    }
}