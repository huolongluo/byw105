package huolongluo.byw.byw.bean

import huolongluo.byw.util.RSACipher

data class ChainBean(
        val tokenName:String,
        val address:String
){
    //address需要解密显示
    fun getDecryptAddress():String{
        return try {
            RSACipher().decryptfd(address)
        }catch (t:Throwable){
            t.printStackTrace()
            ""
        }
    }
}
