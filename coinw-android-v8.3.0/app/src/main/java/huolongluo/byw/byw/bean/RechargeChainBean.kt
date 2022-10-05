package huolongluo.byw.byw.bean

import huolongluo.byw.util.RSACipher

//充值链列表
data class RechargeChainBean(
    val accesskey:String,
    val addresses:List<ChainBean>
){
    fun getDecryptAccessKey():String{
        return try {
            RSACipher().decryptfd(accesskey)
        }catch (t:Throwable){
            t.printStackTrace()
            ""
        }
    }
}
