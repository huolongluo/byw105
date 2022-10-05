package huolongluo.byw.byw.bean

data class WithdrawBean(
        val result:Boolean,
        val fee_list:List<FeeListBean>,
        val chains:ArrayList<String>,//链名列表
        val list:List<WithdrawChainBean>
)
