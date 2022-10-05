package com.android.legend.api

import android.content.Intent
import com.android.legend.base.BaseRetrofitClient
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.bean.ServiceStopBean
import huolongluo.byw.byw.injection.model.SSLSocketClient
import huolongluo.byw.byw.ui.activity.stop_service.StopServiceActivity
import huolongluo.byw.manager.AppManager
import huolongluo.byw.util.GsonUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.io.File
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

object RetrofitClient : BaseRetrofitClient() {

    private val mainScope = MainScope()

    private val cookieJar by lazy {
        PersistentCookieJar(
                SetCookieCache(), SharedPrefsCookiePersistor(
                BaseApp.appContext
        )
        )
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
        val httpCacheDirectory = File(BaseApp.appContext.cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L
        val cache = Cache(httpCacheDirectory, cacheSize)
        builder.cache(cache)
                .cookieJar(cookieJar)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), x509m)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
    }

    fun maintain(url: HttpUrl, string: String) {
        //内容拷贝来源于HttpLogginginterceptor
        if (url.toString().contains("app/exchangeDepthV2.html") || url.toString().contains("app/getSuccessDetails.html") || url.toString().contains("/ifcontract/tickers")) {
        } else if (!CoinwHyUtils.isServiceStop && AppManager.get().isMainActivityExist) {
            mainScope.launch {
                CoinwHyUtils.isServiceStop = true
                val serviceStopEntity = GsonUtil.json2Obj(string, ServiceStopBean::class.java)
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setClass(BaseApp.getSelf(), StopServiceActivity::class.java)
                intent.putExtra("data", serviceStopEntity.data)
                BaseApp.getSelf().startActivity(intent)
            }
        }
    }

    var x509m: X509TrustManager = object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
        ) {
        }
    }

    // todo 换成native
    private fun sign(text: String): String {
        //val pk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbbjwvFE0sZYEzItlZ9R2nvqGSJkwZQgXOixaYzL7l/4MSeXVKWLVRiRJFraGfj9Uclguaj+lEXmydJ+dOn34Nx4CpWmIxO1VTH4AL4sq7hJZvprqgEHtwgcJCKJFsnh41RXWhU1Yo4VayajQCqQf0LxwbICAusgZuDeT5nqTbpQIDAQAB"
        //Logger.getInstance().debug("sign origin -> $text//$pk")
        // val sign = MD5Tool.getMD5Value("$text//$pk")
        //Logger.getInstance().debug("sign -> $sign")
        //ndk
//        val signNdk = RSACipher.md5(text)
//        Logger.getInstance().debug("signNdk -> $signNdk")
//        return signNdk
        return ""
    }
}