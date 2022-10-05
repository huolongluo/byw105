package com.android.legend.model.enumerate.transfer

import android.content.Context
import huolongluo.byw.R

enum class TransferRecordStatus(val type:Int) {
    SUCCESS(1),PROCESSING(0),FAIL(2);

    fun getText(context: Context,type:Int?):String{
        if(type==null) return ""
        var text=""
        when(type){
            SUCCESS.type->text=context.getString(R.string.already)
            PROCESSING.type->text=context.getString(R.string.processing)
            FAIL.type->text=context.getString(R.string.str_settle_failed)
        }
        return text
    }
}