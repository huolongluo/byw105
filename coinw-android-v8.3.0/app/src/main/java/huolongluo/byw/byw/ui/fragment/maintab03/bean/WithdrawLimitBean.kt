package huolongluo.byw.byw.ui.fragment.maintab03.bean

import java.io.Serializable

data class WithdrawLimitBean (
        val id:Long,
        val minWithdraw:String,
        val maxWithdraw:String
):Serializable