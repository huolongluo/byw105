package com.android.legend.ui.mine

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import com.android.coinw.biz.trade.helper.ETFHepler
import com.android.coinw.test.TestActivity
import com.android.legend.base.BaseFragment
import com.android.legend.ui.earn.EarnActivity
import com.android.legend.ui.login.LoginActivity
import com.android.legend.ui.mine.settings.SettingsActivity
import com.android.legend.util.ClipboardUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.legend.common.component.theme.ThemeManager
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.utils.ToastUtils
import huolongluo.byw.BuildConfig
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.bean.UserInfoBean
import huolongluo.byw.byw.bean.wrap.UserInfoWrap
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.share.Event
import huolongluo.byw.byw.share.Event.exitApp
import huolongluo.byw.byw.share.Event.refreshInfo
import huolongluo.byw.byw.ui.activity.address.AdressManageActivity
import huolongluo.byw.byw.ui.activity.feedback.FeedBackActivity
import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity
import huolongluo.byw.databinding.FragmentMine30Binding
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.reform.home.activity.ShareActivity
import huolongluo.byw.reform.mine.activity.*
import huolongluo.byw.reform.mine.bean.BindHpyBean
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.ApkUtils
import huolongluo.byw.util.DeviceUtils
import huolongluo.byw.util.OkhttpManager
import huolongluo.byw.util.OkhttpManager.DataCallBack
import huolongluo.byw.util.SPUtils
import huolongluo.byw.util.cache.CacheManager
import huolongluo.byw.util.domain.DomainUtil
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.MToast
import huolongluo.bywx.utils.AppUtils
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*

class MineFragment : BaseFragment() {

    companion object {
        fun getInstance(): MineFragment = MineFragment()
    }

    lateinit var mBinding: FragmentMine30Binding
    var mUserIsLogin = ObservableField<Boolean>(false)
    var mIsEncrypt = ObservableField<Boolean>(true) // 手机号是否加敏
    var userInfoBean: UserInfoBean? = null
    var bindHpyBean: BindHpyBean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentMine30Binding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun isRegisterEventBus() = false

    override fun getContentViewId() = 0

    override fun initData() {

    }

    override fun initView(view: View) {

        mUserIsLogin.set(UserInfoManager.isLogin())
        mBinding.userIsLogin = mUserIsLogin
        mBinding.isEncrypt = mIsEncrypt

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        updateUserState()

        mBinding.llUserInfo.setOnClickListener {
            if (!mUserIsLogin.get()!!){
                LoginActivity.launch(requireContext())
            }
        }

        mBinding.ivEncrypt.setOnClickListener {
            mIsEncrypt.set(!mIsEncrypt.get()!!)
        }

        mBinding.ivCopyUid.setOnClickListener {
            if (userInfoBean != null){
                ClipboardUtils.copyingToClipboard(requireContext(), userInfoBean!!.fid.toString())
                ToastUtils.showShortToast(R.string.copy_suc)
            }
        }

        mBinding.ivBack.setOnClickListener {
            activity?.finish()
        }

        mBinding.ivVip.setOnClickListener {
            launchOtherIfLogin(GradeActivity::class.java)
        }

        mBinding.llSecurity.setOnClickListener {
            launchOtherIfLogin(SafeCentreActivity::class.java)
        }

        mBinding.llSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }


        mBinding.llRedPacket.setOnClickListener {
            if (UserInfoManager.isLogin()) {
                val intent = Intent(activity, NewsWebviewActivity::class.java)
                intent.putExtra("url", UrlConstants.MY_REDENVELOPE_URL)
                intent.putExtra("token", UserInfoManager.getToken())
                intent.putExtra("title", resources.getString(R.string.red_envelope))
                requireActivity().startActivity(intent)
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }

        }

        mBinding.llHpySwap.setOnClickListener {
            if (bindHpyBean != null) {
                if (bindHpyBean!!.isHasHyperpayBind) {
                    val intent = Intent(activity, BindHPaySuccessActivity::class.java)
                    intent.putExtra("appId", bindHpyBean!!.appId)
                    startActivity(intent)
                } else {
                    val intent = Intent(activity, BindHPayActivity::class.java)
                    intent.putExtra("uniqueId", bindHpyBean!!.uniqueId)
                    intent.putExtra("appId", bindHpyBean!!.appId)
                    startActivity(intent)
                }
            } else {
                if (UserInfoManager.isLogin()) {
                    bindHyperpay()
                    MToast.show(activity, getString(R.string.err), 1)
                } else {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
            }
        }

        mBinding.llHpyEarn.setOnClickListener {
            startActivity(Intent(requireContext(), MoneyManagerActivity::class.java))
        }

        mBinding.llHelp.setOnClickListener {
            val intent = Intent(activity, NewsWebviewActivity::class.java)
            intent.putExtra("url", UrlConstants.getHelpUrl(context))
            intent.putExtra("token", UserInfoManager.getToken())
            intent.putExtra("title", resources.getString(R.string.help_center))
            requireActivity().startActivity(intent)
        }

        mBinding.llCwEarn.setOnClickListener {
            //https://www.coinw.am/app/viewRegular
            try {
                val url = if (BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)){
                    SPUtils.getString(context, AppConstants.LOCAL.KEY_LOCAL_HOST_WEB, BuildConfig.HOST_WEB) + "/app/viewRegular"
                } else {
                    BuildConfig.HOST + "/app/viewRegular"
                }
                val intent = Intent(context, NewsWebviewActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("token", UserInfoManager.getToken())
                intent.putExtra("title", getString(R.string.cw_earn))
                intent.putExtra("hideTitle", false)
                intent.putExtra("isBdb", false)
                startActivity(intent)
            } catch (t: Throwable) {
                Logger.getInstance().error(t)
            }
        }

        mBinding.llShare.setOnClickListener {
            startActivity(Intent(requireContext(), ShareActivity::class.java))
        }

        mBinding.llFeedback.setOnClickListener {
            launchOtherIfLogin(FeedBackActivity::class.java)
        }

        mBinding.tvEntrust.setOnClickListener {
            launchOtherIfLogin(TradeOrderListActivity::class.java)
        }

        mBinding.tvAddressManage.setOnClickListener {
            launchOtherIfLogin(AdressManageActivity::class.java)
        }

        mBinding.tvInvite.setOnClickListener {
            val intent = Intent(activity, PyramidSaleWebViewActivity::class.java)
            intent.putExtra("url", UrlConstants.INVITE)
            intent.putExtra("token", UserInfoManager.getToken())
            intent.putExtra("title", getString(R.string.str_invitation_reward))
            startActivity(intent)
        }

        mBinding.tvService.setOnClickListener {
            startActivity(Intent(requireContext(), WebViewActivity::class.java))
        }

        mBinding.tvKycLevel.setOnClickListener {
            if (!UserInfoManager.isLogin()) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            } else {
                if (userInfoBean == null) {
                    MToast.show(activity, getString(R.string.relogin), 1)
                    return@setOnClickListener
                }
                val intentx = Intent()
                intentx.component = ComponentName(activity, AuthActivity::class.java)
                requireActivity().startActivity(intentx)
            }
        }

        mBinding.switchNewbie.isChecked = !SPUtils.isProHome(requireContext())
        mBinding.switchNewbie.setOnCheckedChangeListener { buttonView, isChecked ->
            EventBus.getDefault().post(Event.SwitchHomeUi(isChecked))
        }

        Glide.with(this)
                .asGif()
                .load(R.drawable.point_to)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mBinding.ivPointTo)

        initDebugFeature()

    }

    override fun onResume() {
        getUserInfo()
        super.onResume()
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    private fun initDebugFeature() {

        if (ApkUtils.isApkInDebug(requireActivity().applicationContext) || BuildConfig.ENV_DEV){
            mBinding.tvChangeUpAndDrop.visibility = View.VISIBLE
            mBinding.changeIp.visibility = View.VISIBLE
            mBinding.changeContractIp.visibility = View.VISIBLE
            mBinding.tvTest.visibility = View.VISIBLE
            mBinding.tvFeature.visibility = View.VISIBLE
        } else {
            mBinding.tvChangeUpAndDrop.visibility = View.GONE
            mBinding.changeIp.visibility = View.GONE
            mBinding.changeContractIp.visibility = View.GONE
            mBinding.tvTest.visibility = View.GONE
            mBinding.tvFeature.visibility = View.GONE
        }

        mBinding.tvChangeUpAndDrop.setOnClickListener {
            ThemeManager.changeTickerColorMode()
        }

        mBinding.changeIp.setOnClickListener {
            DialogUtils.getInstance().showChangeIp(getActivity())
        }

        mBinding.changeContractIp.setOnClickListener {
            DialogUtils.getInstance().showChangeContractIp(activity)
        }

        mBinding.tvTest.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity, TestActivity::class.java)
            startActivity(intent)
        }

        mBinding.tvFeature.setOnClickListener {
            EarnActivity.launch(requireContext())
        }
    }

    private fun updateUserState() {
        mUserIsLogin.set(UserInfoManager.isLogin())
        if (UserInfoManager.isLogin()) {

            bindHyperpay()

            userInfoBean = UserInfoManager.getUserInfo()
            mBinding.userInfo = UserInfoWrap(userInfoBean)

            mBinding.tvKycLevel.apply {
                if (userInfoBean!!.isHasC3Validate){
                    mBinding.tvKycLevel.setText(R.string.mine_kyc_adv)
                    mBinding.tvKycLevel.setTextColor(requireContext().resources.getColor(R.color.kyc_adv))
                    mBinding.tvKycLevel.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_kyc_adv), null, null, null)
                    mBinding.tvKycLevel.setBackgroundResource(R.drawable.bg_kyc_adv)
                } else if (userInfoBean!!.isHasC2Validate){
                    mBinding.tvKycLevel.setText(R.string.mine_kyc_normal)
                    mBinding.tvKycLevel.setTextColor(requireContext().resources.getColor(R.color.kyc_normal))
                    mBinding.tvKycLevel.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_kyc_normal), null, null, null)
                    mBinding.tvKycLevel.setBackgroundResource(R.drawable.bg_kyc_normal)
                } else {
                    mBinding.tvKycLevel.setText(R.string.mine_kyc_none)
                    mBinding.tvKycLevel.setTextColor(requireContext().resources.getColor(R.color.kyc_none))
                    mBinding.tvKycLevel.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_kyc_none), null, null, null)
                    mBinding.tvKycLevel.setBackgroundResource(R.drawable.bg_kyc_none)
                }
            }

        } else {
            mBinding.userInfo = UserInfoWrap(null)
        }
        //根据服务器红包大开关进行控制
        if (!CoinwHyUtils.isServiceStop) {
            mBinding.llRedPacket.visibility = if (AppUtils.isRedEnvelopeClose()) View.GONE else View.VISIBLE
        }
    }

    private fun getUserInfo() {
        if (!AppUtils.isNetworkConnected()) {
            return
        }
        var params: MutableMap<String?, String?> = HashMap()
        params["type"] = "1"
        params = OkhttpManager.encrypt(params)

        params["loginToken"] = UserInfoManager.getToken()
        val postToken = UserInfoManager.getToken()
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: Exception, errorMsg: String) {}
            override fun requestSuccess(result: String) {
                //   showLoginDialog(result);
                Log.d("个人信息", result)
                try {
                    val jsonObject = JSONObject(result)
                    val code = jsonObject.getInt("code")
                    Log.e("校验token", "code= : $result")
                    if (code == 0) {
                        val `object` = jsonObject.getJSONObject("userInfo")
                        UserInfoManager.setToken(SPUtils.getLoginToken())
                        userInfoBean = Gson().fromJson(`object`.toString(), UserInfoBean::class.java)

                        if (userInfoBean != null) {
                            UserInfoManager.setUserInfoBean(userInfoBean)
                            //登录IM
                            BaseApp.getSelf().loginIM()
                        }
                        updateUserState()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        //ETF标识
        ETFHepler.getETFDisclaimer()
    }


    fun bindHyperpay() {
        var imei = "012345678"
        try {
            imei = DeviceUtils.getImei(requireActivity().applicationContext)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val params: MutableMap<String, String> = HashMap()
        params["loginToken"] = UserInfoManager.getToken()
        params["imei"] = imei
        OkhttpManager.postAsync(UrlConstants.bindHyperpay, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: java.lang.Exception, errorMsg: String) {
                e.printStackTrace()
            }

            override fun requestSuccess(result: String) {
                Log.i("绑定", "= $result")
                try {
                    // JSONObject jsonObject=new JSONObject(result);
                    bindHpyBean = Gson().fromJson(result, BindHpyBean::class.java)
                    if (bindHpyBean!!.code != 0) {
                        bindHpyBean = null
                        return
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun <T> launchOtherIfLogin(clazz: Class<T>){
        if (!UserInfoManager.isLogin()) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        } else {
            startActivity(Intent(requireContext(), clazz))
        }
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun exitApp(exit: exitApp?) {
        // Share.get().setLogintoken("");
        MToast.show(context, getString(R.string.safe_finish_success))
        SPUtils.saveLoginToken("")
        UserInfoManager.clearUser()
        updateUserState()
        CacheManager.getDefault(BaseApp.getSelf().applicationContext).acache.put("getUserWallet" + UserInfoManager.getUserInfo().fid, "")
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshInfo(refreshInfo: refreshInfo?) {
        getUserInfo()
    }


}