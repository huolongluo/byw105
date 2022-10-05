package com.legend.modular_contract_sdk.widget

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton

fun <T : CheckBox> List<T>.setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
    for (t in this) {
        t.setOnCheckedChangeListener(listener)
    }
}
fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}