package huolongluo.byw.byw.ui.fragment.maintab03.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WithdrawFeeBean(
        val chain:String,
        val fee:Double,
        val fixedFee:Double
):Parcelable
