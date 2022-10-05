package huolongluo.byw.model

data class FinanceRecordBean (
        val coinName:String,
        val number:String,//列表显示的数量
        val status:String,//列表显示的文案
        val createTime:String,
        val ftype:String,//1充值，2提现
        val rechargeAddress:String,//充提需要的充值地址
        val withdrawAddress:String,//提现地址
        val txid:String,//提现id
        val logo:String,//充提详情显示的logo地址
        val blockUrl:String,//提现详情的提现查询需要的地址，和提现id拼成完整地址
        val queryType:Int,  //区分类型，和参数type相同
        val fremark:String//空投列表显示的数据
)