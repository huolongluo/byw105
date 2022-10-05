package com.android.coinw.test

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.security.cloud.CloudRealIdentityTrigger
import com.alibaba.security.realidentity.ALRealIdentityCallback
import com.android.coinw.test.log.HttpLogActivity
import com.android.coinw.test.log.WsLogActivity
import com.android.coinw.utils.Utilities
import huolongluo.byw.R
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.heyue.ui.HYCoinDetailActivity
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.GsonUtil
import huolongluo.byw.util.jpush.PushEntity
import huolongluo.bywx.utils.DataUtils

class TestActivity : AppCompatActivity(), View.OnClickListener {

    private fun gotoWebView(url: String, title: String) {
        try {
            val intent = Intent(this, NewsWebviewActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("token", UserInfoManager.getToken())
            intent.putExtra("title", "")
            startActivity(intent)
        } catch (t: Throwable) {
            Logger.getInstance().error(t)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_test)
        findViewById<View>(R.id.tv_http_log).setOnClickListener(this)
        findViewById<View>(R.id.tv_ws_log).setOnClickListener(this)
        findViewById<View>(R.id.tv_test).setOnClickListener(this)
        findViewById<View>(R.id.tv_change_ip).setOnClickListener(this)
        findViewById<View>(R.id.tv_svr63).setOnClickListener(this)
        findViewById<View>(R.id.tv_svr_online).setOnClickListener(this)
        findViewById<View>(R.id.tv_svr_178_5).setOnClickListener(this)
        findViewById<View>(R.id.tv_svr_online_web).setOnClickListener(this)
        findViewById<View>(R.id.tv_contract).setOnClickListener(this)
        findViewById<View>(R.id.tv_face).setOnClickListener(this)
        findViewById<View>(R.id.tv_safe).setOnClickListener(this)
        findViewById<View>(R.id.tv_hbt).setOnClickListener(this)
        findViewById<View>(R.id.tv_welfare_center).setOnClickListener(this)
        findViewById<View>(R.id.tv_depth_v2).setOnClickListener(this)
        findViewById<View>(R.id.tv_share).setOnClickListener(this)
        findViewById<View>(R.id.tv_websocket_gsoms).setOnClickListener(this)
        findViewById<View>(R.id.tv_websocket_gsoms_next).setOnClickListener(this)
        findViewById<View>(R.id.tv_websocket_gsoms_end).setOnClickListener(this)
        findViewById<View>(R.id.tv_websocket_xkline).setOnClickListener(this)
        //
    }

    override fun onClick(v: View) {
        val vId = v.id
        if (vId == R.id.tv_test) {
//            CoinwTest.upload()
//            Utilities.stageQueue.postRunnable {
//                DataUtils.loadHeaderConfig()
//            }
            testJPush()
        } else if (vId == R.id.tv_change_ip) {
            return
        } else if (vId == R.id.tv_contract) {
            val intent = Intent()
            intent.setClass(this, HYCoinDetailActivity::class.java)
            startActivity(intent)
        } else if (vId == R.id.tv_face) { //阿里face验证
            CloudRealIdentityTrigger.start(
                this,
                "5de8768d723945aa9f6ddd5b5641524e",
                aLRealIdentityCallback
            )
            return
        } else if (vId == R.id.tv_hbt) { //            /app/viewHbt.html
            gotoWebView(UrlConstants.ACTION_HBT, "")
        } else if (vId == R.id.tv_safe) { //安全验证
            return
        } else if (vId == R.id.tv_welfare_center) { //福利中心
//            Intent intent = new Intent(this, WelfareCenterShareActivity.class);
//            startActivity(intent);
            val intent = Intent(this, NewsWebviewActivity::class.java)
            intent.putExtra("url", UrlConstants.ACTION_VIEWWELFARE)
            intent.putExtra("token", UserInfoManager.getToken())
            intent.putExtra("title", "福利中心")
            startActivity(intent)
            return
        } else if (vId == R.id.tv_depth_v2) { //合并深度2.0
            CoinwTest.testDepthV2()
        } else if (vId == R.id.tv_websocket_gsoms) {
            return
        } else if (vId == R.id.tv_websocket_gsoms_next) {
            return
        } else if (vId == R.id.tv_websocket_gsoms_end) { //            endWebsocket();
            return
        } else if (vId == R.id.tv_websocket_xkline) { //            XCoinwTest.getInstance().apiKLine();//OK

            return
        } else if (vId == R.id.tv_http_log) { //            XCoinwTest.getInstance().apiKLine();//OK
            HttpLogActivity.launch(this)
        } else if (vId == R.id.tv_ws_log) { //            XCoinwTest.getInstance().apiKLine();//OK
            WsLogActivity.launch(this)
        }
        CoinwTest.testNetWork(vId)
    }
    private fun testJPush(){
        //            // 在这里可以自己写代码去定义用户点击后的行为
        try {
            val json = "{\"h5\":\"https://coinw.zendesk.com/hc/zh-cn/articles/4402495059481\"}"
//            val json = "{\"h5\":\"https://coinw.zendesk.com/hc/zh-cn/articles/4402495059481\",\"ios\":{\"sound\":\"default\",\"badge\":\"1\",\"mutable-content\":1}}"
            var pushEntity: PushEntity? =
                GsonUtil.json2Obj<PushEntity>(json, PushEntity::class.java) ?: return
            if (!TextUtils.isEmpty(pushEntity!!.h5)) {
                val intents = Intent(this@TestActivity, NewsWebviewActivity::class.java)
                intents.putExtra("url", pushEntity!!.h5)
                intents.putExtra("token", UserInfoManager.getToken())
                intents.putExtra("useH5Title", true)
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intents)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 基础回调的方式 TODO
     * @return
     */
    private val aLRealIdentityCallback: ALRealIdentityCallback
        private get() = ALRealIdentityCallback { alRealIdentityResult, s ->
            //DO your things
//                Log.d("RPSDK", "ALRealIdentityResult:" + alRealIdentityResult.audit);
            Logger.getInstance()
                .debug("TestActivity", "aliface-audit: " + alRealIdentityResult.audit + " s: " + s)
        }


    companion object {
        private const val TAG = "TestActivity"
    }
}