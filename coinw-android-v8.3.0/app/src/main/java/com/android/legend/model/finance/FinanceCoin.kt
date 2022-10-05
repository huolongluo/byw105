package com.android.legend.model.finance

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class FinanceCoin(
        val coinCode:String,
        val coinFullName:String,//币种全称
        val coinId:Int,//币种id
        val coinLog:String,//币种Log url
        val coinName:String,
        val etf:Boolean
):Parcelable