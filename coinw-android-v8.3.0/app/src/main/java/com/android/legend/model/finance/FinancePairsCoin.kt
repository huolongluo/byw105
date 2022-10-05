package com.android.legend.model.finance

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
//交易对最小信息
@SuppressLint("ParcelCreator")
@Parcelize
data class FinancePairsCoin(
        val baseId:Int,//交易对基础币种id
        val baseName:String,//交易对基础币种名称
        val pairId:Int,//交易对id
        val quoteId:Int,//交易对计价币种id
        val quoteName:String//交易对计价币种名称
):Parcelable