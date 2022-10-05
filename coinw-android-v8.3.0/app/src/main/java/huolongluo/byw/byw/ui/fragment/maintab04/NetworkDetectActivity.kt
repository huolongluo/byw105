package huolongluo.byw.byw.ui.fragment.maintab04

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.core.net.toUri
import com.android.legend.api.ApiRepository
import com.android.legend.model.enumerate.order.OrderType
import com.android.legend.model.enumerate.transfer.TransferAccount
import huolongluo.byw.BuildConfig
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.net.DomainHelper
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.net.okhttp.HttpUtils
import huolongluo.byw.helper.SSLHelper
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.base.BaseActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.*
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.AppUtils
import kotlinx.android.synthetic.main.activity_network_detect.*
import kotlinx.android.synthetic.main.base_title.*
import kotlinx.coroutines.*
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.*


class NetworkDetectActivity : BaseActivity(), CoroutineScope by MainScope() {


    companion object {
        private const val JS_METHOD_NAME = "getDiagnoseInfo"
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, NetworkDetectActivity::class.java))
        }
    }

    private val url = UrlConstants.getNetworkDetect()

    private var detectJob: Job? = null

    private val header by lazy {
        val loginToken = RSACipher().encrypt(loginToken, AppConstants.KEY.PUBLIC_KEY)
                .replace("\n", "");

        mutableMapOf("lang" to AppUtils.getLanguage(),
                "system" to "android",
                "loginToken" to loginToken,
                "deviceId" to FingerprintUtil.getFingerprint(BaseApp.getSelf()),
                SSLHelper.getHeadKey() to SSLHelper.getHeadValue(11, url))
    }

    private val loginToken = UserInfoManager.getToken()

    /**
     * 检测id
     * 生成策略：如果登录，用户id拼接时间戳；否则采用uuid
     */
    private val detectionId by lazy {
        if (UserInfoManager.getUserInfo() != null && UserInfoManager.getUserInfo().fid != 0) {
            UserInfoManager.getUserInfo().fid.toString() + "_" + System.currentTimeMillis()
        } else {
            UUID.randomUUID().toString()
        }
    }

    private val loadingDrawable by lazy {
        //        iv_loading.setImageResource(R.drawable.loading)
        iv_loading.drawable as AnimationDrawable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw() // webview全屏截图，需要在setContentView之前调用
        }
        setContentView(R.layout.activity_network_detect)
        back_iv.setOnClickListener { finish() }
        title_tv.setText(R.string.network_test)
        loadingDrawable.start()
        setWebviewAttr()
        btn_img.setOnClickListener {
            val outputPath = "${Environment.getExternalStorageDirectory().absolutePath}/${Environment.DIRECTORY_DCIM}/Camera" +
                    "/coinw_network_detect_$detectionId.jpg"
            ScreenshotUtil.screenshotWebView(this, webView, outputPath, {
                //保存成功
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, it.toUri()));
                launch { SnackBarUtils.ShowBlue(this@NetworkDetectActivity, getString(R.string.generate_img_success_to_gallery)) }
                val bytes = File(outputPath).readBytes()
                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                val params = mutableMapOf(
                        "file" to base64,
                        "detectionId" to detectionId,
                        "ext1" to "png",
                        "loginToken" to loginToken)
                OkhttpManager.getInstance().removeRequest(UrlConstants.UPLOAD_NETWORK_DETECT_PIC)
                OkhttpManager.postAsync(UrlConstants.UPLOAD_NETWORK_DETECT_PIC, params, object : OkhttpManager.DataCallBack {
                    override fun requestSuccess(result: String?) {
                        Logger.getInstance().debug("网络检测->图片上传成功: $result")
                    }

                    override fun requestFailure(request: Request?, e: java.lang.Exception?, errorMsg: String?) {
                        Logger.getInstance().debug("网络检测->图片上传失败: $errorMsg")
                    }
                })
            }, { /*保存失败*/launch { SnackBarUtils.ShowRed(this@NetworkDetectActivity, getString(R.string.d6)) } })
        }
    }

    private fun callJs(json: String) = webView.loadUrl("javascript:window.getDiagnoseInfoResult('$json');")

    /**
     * 进行网络诊断，获取机型信息和测速，通过调用js方法把数据传给web展示
     */
    private fun doDetect() {
        // 1.App_a_o：otc的广告列表api速度
        // 2.App_a_oo：当前委单api速度
        // 3.App_a_ao：全部委单api速度
        // 4.App_a_ho：历史委单api速度
        // 5.App_a_t：交易页api速度
        // 6.App_a_b币币账户api速度
        // 7. App_a_f：买币账户api速度
        // 8.App_a_m：行情交易分区api速度
        detectJob?.cancel()
        detectJob = launch {
            // 获取操作系统
            Logger.getInstance().debug("网络检测->操作系统: Android " + Build.VERSION.RELEASE + "\n")
            // 手机信息
            Logger.getInstance().debug("网络检测->手机型号: " + Build.MODEL + "\n")
            Logger.getInstance().debug("网络检测->APP版本号: " + BuildConfig.VERSION_CODE + "\n")
            callJs("""{"AppVersion":"${BuildConfig.VERSION_NAME}","Device":"${Build.MODEL};Android ${Build.VERSION.RELEASE};${MD5Util.encrypt(DomainHelper.getDomain().host)};${MD5Util.encrypt(DomainHelper.getDomain().ws)};"}""")
            callJs("""{"detectionId":"$detectionId"}""")

            val downloadSpeed = NetworkSpeedUtil.getSpeedText(UrlConstants.NETWORK_SPEED_TEST)
            Logger.getInstance().debug("网络检测->下载速度 \t$downloadSpeed")
            callJs("""{"downloadSpeed":"$downloadSpeed"}""")

            // otc的广告列表api速度
            val paramsOTC = OkhttpManager.encrypt(mapOf(
                    "coinId" to "2",
                    "type" to "2",
                    "pageNo" to "1",
                    "pageSize" to "10",
                    "fillExternal" to "true"))
            val pingOTC = NetworkSpeedUtil.getPing(UrlConstants.advertisements, paramsOTC)
            Logger.getInstance().debug("网络检测->otc的广告列表api速度 \t $pingOTC")
            callJs("""{"App_a_o":"$pingOTC"}""")

            val pingEntrust = NetworkSpeedUtil.getPing{
                ApiRepository.instance.getOrders(true, 0, null, null, "0",
                        OrderType.LIMIT.type, "0",null)
            }
            Logger.getInstance().debug("网络检测->当前委单api速度  \t $pingEntrust")
            callJs("""{"App_a_oo":"$pingEntrust"}""")

            val pingHisEntrust = NetworkSpeedUtil.getPing{
                ApiRepository.instance.getOrders(false, 0, null, null, "0",
                        OrderType.LIMIT.type, "0",null)
            }
            Logger.getInstance().debug("网络检测->历史委单api速度  \t $pingHisEntrust")
            callJs("""{"App_a_ho":"$pingHisEntrust"}""")

            val pingEntrustAll = NetworkSpeedUtil.getPing{
                ApiRepository.instance.getOrders(false, 0, null, null, "0",
                        OrderType.LIMIT.type, "1",null)
            }
            Logger.getInstance().debug("网络检测->全部委单api速度  \t $pingEntrustAll")
            callJs("""{"App_a_ao":"$pingEntrustAll"}""")

            val pingExchangeDepth = NetworkSpeedUtil.getPing{
                ApiRepository.instance.getDepthData("0")
            }
            Logger.getInstance().debug("网络检测->交易页api速度  \t$pingExchangeDepth")
            callJs("""{"App_a_t":"$pingExchangeDepth"}""")

            val pingUserWallet = NetworkSpeedUtil.getPing { ApiRepository.instance.getBbFinanceData(TransferAccount.SPOT.value) }
            Logger.getInstance().debug("网络检测->币币账户api速度  \t$pingUserWallet")
            callJs("""{"App_a_b":"$pingUserWallet"}""")

            val pingOtcWallet = NetworkSpeedUtil.getPing { ApiRepository.instance.getBbFinanceData(TransferAccount.OTC.value) }
            Logger.getInstance().debug("网络检测->买币账户api速度  \t$pingOtcWallet")
            callJs("""{"App_a_f":"$pingOtcWallet"}""")

            val pingTradeArea = NetworkSpeedUtil.getPing(UrlConstants.DOMAIN + UrlConstants.TRADING_AREA_MAIN, OkhttpManager.encrypt(mapOf("type" to "2")))
            Logger.getInstance().debug("网络检测->行情交易分区api速度  \t$pingTradeArea")
            callJs("""{"App_a_m":"$pingTradeArea"}""")

            //检测完成
            webView.loadUrl("javascript:window.sendMenu();")
            // 检测完成，隐藏进度条，修改文字
            loadingDrawable.stop()
            iv_loading.visibility = View.GONE
            btn_img.setText(R.string.network_detect_gen_img)
            btn_img.isEnabled = true
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebviewAttr() {
        AppHelper.setSafeBrowsingEnabled(webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
        webView.settings.setBuiltInZoomControls(false) //支持缩放，默认为true。是下面那个的前提。
        webView.settings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webView.settings.displayZoomControls = true //隐藏原生的缩放控件
        //        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        webView.settings.loadsImagesAutomatically = true //支持自动加载图片
        webView.settings.defaultTextEncodingName = "utf-8" //设置编码格式
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        //DEBUG模式打开WebView的调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
//        webView.addJavascriptInterface(JSCallJavaInterface(), "JSCallJava")
        webView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun callJava(msg: String) {
                Logger.getInstance().debug("网络检测->$msg")
                val json = JSONObject(msg)
                val callName = json.getString("callName")
                if (callName == JS_METHOD_NAME) {
                    doDetect()
                }
            }
        }, "JSCallJava")
        webView.settings.blockNetworkImage = true
        //点击超链接的时候重新在原来的进程上加载URL
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", " errorCode: $errorCode description: $description failingUrl: $failingUrl")
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) { // 这个方法在 android 6.0才出现
                    view.loadUrl("about:blank") // 避免出现默认的错误界面
                    //                    view.loadUrl(mErrorUrl);// 加载自定义错误页面
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url))
                view.loadUrl(url, header)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                webView.settings.blockNetworkImage = false
                //判断webview是否加载了，图片资源
                if (!webView.settings.loadsImagesAutomatically) { //设置wenView加载图片资源
                    webView.settings.loadsImagesAutomatically = true
                }
                super.onPageFinished(view, url)
                progressbar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressbar.visibility = View.VISIBLE
            }

            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) { // 这个方法在 android 6.0才出现
                    view.loadUrl("about:blank") // 避免出现默认的错误界面
                    //                    view.loadUrl(mErrorUrl);// 加载自定义错误页面
                }
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) { // android 5.0以上默认不支持Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                }
                handler.proceed()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId())
                return true
            }

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", "url: $url message: $message", Exception())
                return super.onJsAlert(view, url, message, result)
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressbar.visibility = View.GONE //加载完网页进度条消失
                } else {
                    progressbar.visibility = View.VISIBLE //开始加载网页时显示进度条
                    progressbar.progress = newProgress //设置进度值
                }
            }
        }
        webView.loadUrl(url, header)
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
        loadingDrawable.stop()
        cancel()
    }
}