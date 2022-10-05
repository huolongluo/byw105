package com.android.legend.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseLoginActivity
import com.android.legend.extension.gone
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.model.login.LoginParam
import com.android.legend.util.InputUtil
import huolongluo.byw.BuildConfig
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.net.okhttp.HttpUtils
import huolongluo.byw.byw.share.Event.*
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.model.AreaCodeBean
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.reform.login_regist.AreaCodeChoiceActivity
import huolongluo.byw.reform.login_regist.AreaCodeChoiceActivity2
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.*
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.AppUtils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_reset_login_pwd_third.*
import kotlinx.android.synthetic.main.inc_login_btn.*
import kotlinx.android.synthetic.main.inc_login_phone_email.*
import kotlinx.android.synthetic.main.inc_login_phone_email.cetPwd
import kotlinx.android.synthetic.main.inc_login_phone_email.tvPwdError
import kotlinx.android.synthetic.main.inc_login_phone_email.vPwd
import kotlinx.android.synthetic.main.inc_pwd_intensity.*
import kotlinx.android.synthetic.main.widget_common_edittext.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 注册流程第一个页面
 */
class RegisterActivity :BaseLoginActivity(){
    private val viewModel:LoginViewModel by viewModels()
    private val userName by lazy { SPUtils.getString(this, SPUtils.USER_NAME, "") }
    private val passwordChecker by lazy { PasswordChecker() }
    private var stopMills = System.currentTimeMillis()
    var isPhone=true
    private var dialogAliManMachine: AlertDialog? = null
    private lateinit var aliEntity: AliManMachineEntity
    private lateinit var loginParam:LoginParam
    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }
    }
    override fun initTitle(): String {
        return getString(R.string.regist)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_register
    }

    override fun initView() {
        tvAreaCode.text= "+1"

        if(StringUtil.isEmail(userName)){
            isPhone=false
        }
        initPhoneEmail()
        tvPhoneEmail2.setOnClickListener {
            AppUtils.hideSoftKeyboard(this)
            isPhone=!isPhone
            initPhoneEmail()
        }
        etPhone.setOnFocusChangeListener { v, hasFocus ->
            vPhone.isSelected = hasFocus
        }
        etEmail.setOnFocusChangeListener { v, hasFocus ->
            vPhone.isSelected = hasFocus
        }
        cetPwd.et.setOnFocusChangeListener { v, hasFocus ->
            vPwd.isSelected=hasFocus
        }
        etInvite.setOnFocusChangeListener { v, hasFocus ->
            vInvite.isSelected=hasFocus
        }
        tvAreaCode.setOnClickListener { startActivityForResult(Intent(this, AreaCodeChoiceActivity2::class.java), 101) }
        cetPwd.visible()
        tvPwdError.text=getString(R.string.str_pwd_tip)
        //根据渠道需求，进行特殊处理
        if (BuildConfig.APP_CHANNEL_VALUE === 1) {
            tvInvite.gone()
            etInvite.setText(BuildConfig.CHANNEL_INTRO_VALUE)
        } else if (BuildConfig.APP_CHANNEL_VALUE === 2) {
            etInvite.setText(BuildConfig.CHANNEL_INTRO_VALUE)
        }
        tvInvite.setOnClickListener {
            if(etInvite.visibility== View.GONE){
                etInvite.visible()
                vInvite.visible()
            }else{
                etInvite.gone()
                vInvite.gone()
            }
        }
        tvSubmit.text=getString(R.string.regist)
        tvSubmit.setOnClickListener {
            if (FastClickUtils.isFastClick(500)) {
                return@setOnClickListener
            }
            if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                SnackBarUtils.ShowRed(this, getString(R.string.net_exp))
                return@setOnClickListener
            }
            if (isPhone) {
                if (!InputUtil.checkInputPhone(this, etPhone.text.trim().toString(), null)) {
                    return@setOnClickListener
                }
            } else {
                if (!InputUtil.checkInputEmail(this, etEmail.text.trim().toString(), null)) {
                    return@setOnClickListener
                }
            }
            if(!InputUtil.checkInputPwd(this, cetPwd.text.toString().trim(), passwordChecker, null)) return@setOnClickListener
            dialogAliManMachine= DialogUtils.getInstance().showAliManMachineDialog(this, Constant.ALI_MAN_MACHINE_TYPE_LOGIN)
        }
        initTextWatcher()
        llLogin.setOnClickListener {
            LoginActivity.launch(this)
            finish()
        }
        tvAgreement.setOnClickListener {
            var url= ""
            when {
                LogicLanguage.getLanguage(this).contains("zh") -> {
                    url = UrlConstants.AGREEMENT_URL_ZH
                }
                LogicLanguage.getLanguage(this).contains("en") -> {
                    url = UrlConstants.AGREEMENT_URL_EN
                }
                LogicLanguage.getLanguage(this).contains("ko") -> {
                    url = UrlConstants.AGREEMENT_URL_KO
                }
            }
            val intent = Intent(this, NewsWebviewActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("token", UserInfoManager.getToken())
            val title = getString(R.string.str_coinw_agreement_title)
            intent.putExtra("title", title)
            startActivity(intent)
        }
    }

    override fun initData() {
//        getAreaCode()
    }

    override fun initObserve() {
        viewModel.aliVerifyData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "阿里验证返回数据:$it")
            if (it.isSuccess && it.data?.code == 1) {
                register()
            } else {
                Logger.getInstance().report("人机验证失败", "$TAG-${it.data}")
            }
        })
        viewModel.checkRegisterAccountData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "注册验证返回数据:$it")
            if (it.isSuccess) {
                if(it.data?.code == "200"){
                    loginParam.messageType = gMessageType()
                    RegisterSmsVerifyActivity.launch(this, loginParam, aliEntity)
                }else{
                    SnackBarUtils.ShowRed(this,it.data?.message)
                }
            } else {
                SnackBarUtils.ShowRed(this, it.message)
            }
        })
        viewModel.areaCodeData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "区域码国家列表数据:$it")
            if(it.code=="0"){//该接口code0为成功。。。
                val list=it.data
                list?.forEach { bean->
                    if(bean.areaCode == "86"){
                        tvAreaCode.text=LogicLanguage.getPhonePreByLanguage(this)
                    }
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        if (System.currentTimeMillis() - stopMills > 60 * 1000L) {
            cetPwd?.clearText()
        }
    }

    override fun onStop() {
        super.onStop()
        stopMills = System.currentTimeMillis()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && data != null) {
            val code = data.getStringExtra("areaCode")
            tvAreaCode.text=code
        }
    }
    private fun getAreaCode(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.getAreaCode()
    }
    private fun register(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        loginParam=LoginParam(
                gUserName(), etPwd.text.toString().trim(), "0", "0", 12, "",
                gAreaCode(), "", "", "", gInvite(), isPhone
        )
        viewModel.checkRegisterAccount(loginParam)
    }
    private fun gUserName():String=if(isPhone) etPhone.text.toString().trim() else etEmail.text.toString().trim()
    private fun gAreaCode():String=if(isPhone) tvAreaCode.text.toString().replace("+", "") else ""
    private fun gMessageType():Int=if(isPhone) 12 else 3 //发送短信和邮箱验证码的type
    private fun gInvite():String= if (BuildConfig.APP_CHANNEL_VALUE == 1) BuildConfig.CHANNEL_INTRO_VALUE else etInvite.text.toString().trim()
    private fun initPhoneEmail(name: String = ""){
        if(!isPhone) {
            tvPhoneEmail1.text=getString(R.string.email)
            tvPhoneEmail2.text=getString(R.string.phone_register)
            etEmail.visible()
            llPhone.invisible()
            if(!TextUtils.isEmpty(name)){
                etEmail.setText(name)
            }
//            if (etEmail.text.trim().isNotEmpty()) {
//                InputUtil.checkInputEmail(this, etEmail.text.trim().toString(), tvPhoneOrEmailError)
//            }
        }else{
            tvPhoneEmail1.text=getString(R.string.phone)
            tvPhoneEmail2.text=getString(R.string.email_register)
            llPhone.visible()
            etEmail.invisible()
            if(!TextUtils.isEmpty(name)){
                etPhone.setText(name)
            }
//            InputUtil.checkInputPhone(this, etPhone.text.trim().toString(), tvPhoneOrEmailError)
        }
        checkEnable()
    }
    private fun initTextWatcher() {
        etPhone.addTextChangedListener { text ->
//            InputUtil.checkInputPhone(this, text.toString(), tvPhoneOrEmailError)
            checkEnable()
        }
        etEmail.addTextChangedListener { text ->
//            InputUtil.checkInputEmail(this, text.toString(), tvPhoneOrEmailError)
            checkEnable()
        }
        cetPwd.et.addTextChangedListener{ text ->
            InputUtil.refreshIntensity(this,cetPwd.et,clIntensity,tvIntensity, ivPwd1, ivPwd2, ivPwd3, passwordChecker)
            checkEnable()
        }
    }
    private fun checkEnable(){
        tvSubmit.isEnabled = !TextUtils.isEmpty(cetPwd.text.toString())  &&
                ((isPhone&&!TextUtils.isEmpty(etPhone.text.toString()))
                ||(!isPhone&&!TextUtils.isEmpty(etEmail.text.toString())))
    }
    private fun aliVerify(entity: AliManMachineEntity){
        aliEntity=entity
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.aliVerify(entity)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun aliManMachineSuccess(event: BizEvent.AliManMachine) {
        if (event.type == Constant.ALI_MAN_MACHINE_TYPE_LOGIN) {
            AppHelper.dismissDialog(dialogAliManMachine)
            DialogManager2.INSTANCE.dismiss()
            aliVerify(event.entity)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun close(event: BizEvent.CloseActivity) {
        finish()
    }
}