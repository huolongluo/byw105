package huolongluo.byw.util.domain

import android.content.Context
import android.text.TextUtils
import huolongluo.byw.byw.net.DomainHelper
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.SPUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * 域名工具类
 */
object DomainUtil {
    private const val TAG = "DomainUtil"
    /**
     * 切换域名
     */
    fun switchDomain(context: Context, result: String, force: Boolean) {
        try {
            val jsonObject= JSONObject(result)
            val urlApp = jsonObject["urlApp"] as String
            val urlWeb = jsonObject["urlWeb"] as String
            val urlSwap = jsonObject["urlSwap"] as String
            val urlDownload = jsonObject["qrcodeUrl"] as String

            // 保存App下载地址
            if (!TextUtils.isEmpty(urlDownload) && !TextUtils.equals(urlDownload, SPUtils.getString(context,AppConstants.LOCAL.KEY_LOCAL_HOST_DOWNLOAD,""))){
                SPUtils.saveString(context, AppConstants.LOCAL.KEY_LOCAL_HOST_DOWNLOAD, urlDownload)
            }

            Logger.getInstance().debug(TAG, "urlApp:$urlApp urlWeb:$urlWeb urlSwap:$urlSwap")
            if (!TextUtils.isEmpty(urlApp) && !TextUtils.equals(getHostWithSlashSuffix(urlApp),SPUtils.getString(context,AppConstants.LOCAL.KEY_LOCAL_HOST,""))) { //需要替换app域名
                SPUtils.saveString(context, AppConstants.LOCAL.KEY_LOCAL_HOST, getHostWithSlashSuffix(urlApp))
                //是否强制更新内存
                if (force) {
                    DomainHelper.setDomain(null)
                    val start = System.currentTimeMillis()
                    UrlConstants.setDomain(getHostWithSlashSuffix(urlApp))
                    Logger.getInstance().debug(TAG, "替换域名时间 ${System.currentTimeMillis() - start}")
                }
                Logger.getInstance().debug(TAG, "app域名不一致需要替换")
            }
            if (!TextUtils.isEmpty(urlWeb) && !TextUtils.equals(getHostWithSlashSuffix(urlWeb),SPUtils.getString(context,AppConstants.LOCAL.KEY_LOCAL_HOST_WEB,""))) { //需要替换app域名
                SPUtils.saveString(context, AppConstants.LOCAL.KEY_LOCAL_HOST_WEB, getHostWithSlashSuffix(urlWeb))
            }
            if (!TextUtils.isEmpty(urlSwap) && !TextUtils.equals(getHostWithSlashSuffix(urlSwap),SPUtils.getString(context,AppConstants.LOCAL.KEY_LOCAL_HOST_SWAP,""))) { //需要替换app域名
                SPUtils.saveString(context, AppConstants.LOCAL.KEY_LOCAL_HOST_SWAP, getHostWithSlashSuffix(urlSwap))
            }
            if (!TextUtils.isEmpty(urlWeb) && !TextUtils.equals(getHostWithSlashSuffix(urlWeb), getHostWithSlashSuffix(UrlConstants.WEB_DOMAIN))) { //需要替换web域名
                UrlConstants.setWebDomain(getHostWithSlashSuffix(urlWeb))
                Logger.getInstance().debug(TAG, "web域名不一致需要替换")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * 域名后面带上/的后缀
     */
    fun getHostWithSlashSuffix(host:String):String{
        return if (host.endsWith("/")) {
            host
        } else {
            "$host/"
        }
    }

    /**
     * 获取web域名的后缀
     */
    fun getWebUrlSuffix():String{
        var urlWeb=UrlConstants.WEB_DOMAIN
        if(TextUtils.isEmpty(urlWeb)){
            return ""
        }
        if(urlWeb.startsWith("https://")){
            return urlWeb.substring(8, urlWeb.length)
        }
        if(urlWeb.startsWith("http://")){
            return urlWeb.substring(7, urlWeb.length)
        }
        return urlWeb
    }
}