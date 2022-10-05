package com.android.legend.view

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText

//价格输入控制不能输入00 01这类
open class InputPriceTextWatcher : TextWatcher {
    private lateinit var input: EditText

    constructor(input: EditText) {
        this.input = input
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val str = s.toString()
        try {
            if(str.length==1&&TextUtils.equals(".",str)){
                input.setText("")
            }else if (str.length >= 2 && str.substring(0, 1) == "0") {
                val num = str.substring(0, 2).toInt()
                if (num >= 0) {
                    input.setText(str.substring(1))
                    input.setSelection(input.text.length)
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun afterTextChanged(s: Editable?) {}
}