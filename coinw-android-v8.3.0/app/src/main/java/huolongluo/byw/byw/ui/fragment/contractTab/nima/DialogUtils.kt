package huolongluo.byw.byw.ui.fragment.contractTab.nima

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import huolongluo.byw.R
import huolongluo.byw.byw.ui.fragment.contractTab.NMInfoEntity
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.DoubleUtils
import java.text.DecimalFormat

class DialogUtils {
    private var dialog: AlertDialog? = null
    private val df = DecimalFormat("0.0000");

    companion object {
        private var self: DialogUtils? = DialogUtils()
        fun getInstance(): DialogUtils? {
            if (self == null) {
                self = DialogUtils()
            }
            return self
        }


    }

    fun dismiss() {
        AppHelper.dismissDialog(dialog)
        dialog = null
    }

    /**
     * 合约划转成功，弹窗
     */
    fun contractSuccessful(context: Activity, data: Array<String>, url: String, listener: () -> Unit) {
        if (dialogShowStatues()) return
        val view = View.inflate(context, R.layout.dialog_contract_trade_successfule, null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<Button>(R.id.to_trade).setOnClickListener {
            listener()
        }
        view.findViewById<TextView>(R.id.tv_grant).setOnClickListener {
            toGrantWebView(context, url)
        }
        view.findViewById<TextView>(R.id.title_des1).text = data[0]
        view.findViewById<TextView>(R.id.tv_money).text = data[1]
        initDialogView(context, view)
    }

    fun contractSuccessful1(context: Activity, data: Array<String>, url: String, listener: () -> Unit) {
        if (dialogShowStatues()) return
        val view = View.inflate(context, R.layout.dialog_trade_successful, null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<Button>(R.id.to_trade).setOnClickListener {
            dismiss()
            //            listener()
        }
        view.findViewById<TextView>(R.id.tv_grant).setOnClickListener {
            toGrantWebView(context, url)
        }
        view.findViewById<TextView>(R.id.des_content).text = data[0]
        initDialogView(context, view)
    }

    /**
     * 域名变更 提示框
     */
    fun hostChange(context: Activity, url: String) {
        if (dialogShowStatues()) return
        val view = View.inflate(context, R.layout.dialog_change_host, null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        val img = view.findViewById<ImageView>(R.id.bg_view)
        img.setOnClickListener {
            dismiss()
        }
        Glide.with(context).load(url).into(img)
        initDialogView(context, view)
    }

//    fun whatNm(context: Activity, data: Array<String>, listener: () -> Unit) {
//        var HTML_TEXT = "累计净划转:<font  color=\"red\">50USDT</font>获得<font size=\"3\" color=\"red\">50USDT</font>泥码"
//        var HTML_TEXT2 = "累计净划转:<font  color=\"red\">50USDT</font>获得<font size=\"3\" color=\"red\">50USDT</font>泥码"
//        if (dialogShowStatues()) return
//        val view = View.inflate(context, R.layout.dialog_what_nm, null)
//        view.findViewById<ImageView>(R.id.close).setOnClickListener {
//            dismiss()
//        }
//        view.findViewById<Button>(R.id.to_trade).setOnClickListener {
//            listener()
//        }
//        view.findViewById<TextView>(R.id.tv_grant).setOnClickListener {
//            toGrantWebView(context)
//        }
////        view.findViewById<TextView>(R.id.des_content).text = data[0]
//        view.findViewById<TextView>(R.id.des_content1).text = Html.fromHtml(HTML_TEXT)
//        view.findViewById<TextView>(R.id.des_content2).text = Html.fromHtml(HTML_TEXT2)
//        initDialogView(context, view)
//    }

    private fun toGrantWebView(context: Activity, url: String) {
        val intent = Intent(context, NewsWebviewActivity::class.java)
        //JIRA:COIN-3248
//        if (LogicLanguage.isZhEnv(context)) {
//            intent.putExtra("url", UrlConstants.GRANT_URL_ZH)
//        } else {
//            intent.putExtra("url", UrlConstants.GRANT_URL_EN)
//        }
        //
        Logger.getInstance().debug("DialogUtils", "url: " + url, Exception());
        intent.putExtra("url", url)
        intent.putExtra("token", UserInfoManager.getToken())
        intent.putExtra("useH5Title", true)
        context.startActivity(intent)
    }

    private fun dialogShowStatues(): Boolean {
        if (dialog != null && dialog!!.isShowing) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            return true
        }
        return false
    }

    private fun initDialogView(context: Activity, view: View?) {
        if (view == null) {
            return
        }
        dialog = AlertDialog.Builder(context).create()
        dialog!!.setCancelable(false)
        dialog!!.setView(view)
        val windows = dialog!!.window
        windows?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.show()
    }

    fun whatNm(context: Activity, nmInfoEntity: NMInfoEntity, listener: () -> Unit) {
        var HTML_TEXT = context.getString(R.string.cumulative_transfer) + "<font  color=\"red\">" + nmInfoEntity.data.oneLevelTransfer + "USDT</font>" + context.getString(R.string.get_des) + "<font size=\"3\" color=\"red\">" + nmInfoEntity.data.oneLevelMud + "USDT</font>" + context.getString(R.string.nm)
        var HTML_TEXT2 = context.getString(R.string.cumulative_transfer) + "<font  color=\"red\">" + nmInfoEntity.data.twoLevelTransfer + "USDT</font>" + context.getString(R.string.get_des) + "<font size=\"3\" color=\"red\">" + nmInfoEntity.data.twoLevelMud + "USDT</font>" + context.getString(R.string.nm)
        if (dialogShowStatues()) return
        val view = View.inflate(context, R.layout.dialog_what_nm, null)
        if (nmInfoEntity.data.status == 2) {
            view.findViewById<TextView>(R.id.activity_status).visibility = View.VISIBLE
        }
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<Button>(R.id.to_trade).setOnClickListener {
            dismiss()
            listener()
        }
        view.findViewById<TextView>(R.id.tv_grant).setOnClickListener {
            dismiss()
            //
            toGrantWebView(context, nmInfoEntity.data.url)
        }
//        view.findViewById<TextView>(R.id.des_content).text = data[0]
        view.findViewById<TextView>(R.id.des_content1).text = Html.fromHtml(HTML_TEXT)
        view.findViewById<TextView>(R.id.des_content2).text = Html.fromHtml(HTML_TEXT2)
        initDialogView(context, view)
    }


    fun projectIncentiveDialog(context: Activity, coinName: String, lockCoin: AssetCoinsBean.LockCoin?, listener: () -> Unit) {
        if (dialogShowStatues()) return
        val view = View.inflate(context, R.layout.project_incentive, null)
        val spannableString = context.getString(R.string.single_release) + "<font color='#FF0000'>" + df.format(DoubleUtils.parseDouble(lockCoin?.releaseNum
                ?: "0")) + coinName + "</font>";
        view.findViewById<TextView>(R.id.vol1).text = Html.fromHtml(spannableString);
        val spannableString1 = context.getString(R.string.release_limit) + "<font color='#FF0000'>" + df.format(DoubleUtils.parseDouble(lockCoin?.releaseLimit
                ?: "0")) + coinName + "</font>";
        view.findViewById<TextView>(R.id.vol2).text = Html.fromHtml(spannableString1);
        if (lockCoin != null) {
            if (TextUtils.isEmpty(lockCoin.releaseNum) && TextUtils.isEmpty(lockCoin.releaseLimit)) {
                view.findViewById<LinearLayout>(R.id.ll_interest).visibility = View.GONE
            } else {
                view.findViewById<LinearLayout>(R.id.ll_interest).visibility = View.VISIBLE
            }

            val desRelease = view.findViewById<TextView>(R.id.des_release);
            if (TextUtils.isEmpty(lockCoin.orderLimit)) {
                desRelease.text = context.getString(R.string.j4);
            } else {
                desRelease.text = String.format(context.getString(R.string.interest_des), lockCoin.orderLimit)
            }
        } else {
            view.findViewById<LinearLayout>(R.id.ll_interest).visibility = View.GONE
        }
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_click).setOnClickListener {
            dismiss()
        }
        initDialogView(context, view)
    }

}