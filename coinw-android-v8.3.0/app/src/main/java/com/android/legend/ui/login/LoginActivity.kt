package com.android.legend.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseLoginActivity
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.model.login.LoginParam
import com.android.legend.util.InputUtil
import huolongluo.byw.BuildConfig
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.net.okhttp.HttpUtils
import huolongluo.byw.byw.share.Event.*
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.reform.login_regist.AreaCodeChoiceActivity
import huolongluo.byw.util.*
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.AppUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.inc_login_btn.*
import kotlinx.android.synthetic.main.inc_login_phone_email.*
import kotlinx.android.synthetic.main.widget_common_edittext.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 登录页面
 */
class LoginActivity : BaseLoginActivity() {
    private val viewModel:LoginViewModel by viewModels()
    private val isTokenExpired by lazy { intent.getBooleanExtra("isTokenExpired", false) }
    private val fromClass by lazy { intent.getBundleExtra("bundle")?.getString("fromClass") }
    private val userName by lazy { SPUtils.getString(this, SPUtils.USER_NAME, "") }
    private var stopMills = System.currentTimeMillis()
    var isPhone=true
    private var dialogAliManMachine: AlertDialog? = null
    private lateinit var aliEntity: AliManMachineEntity
    private var random=""
    private lateinit var loginParam:LoginParam
    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
    override fun initTitle(): String {
        return getString(R.string.login)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_login
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val isPhoneIntent=intent.getBooleanExtra("isPhone",isPhone)
        if(isPhoneIntent!=isPhone){
            isPhone=isPhoneIntent
            initPhoneEmail()
        }
    }

    override fun initView() {
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { back() }

        tvAreaCode.text= LogicLanguage.getPhonePreByLanguage(this)

        if(StringUtil.isEmail(userName)){
            isPhone=false
        }
        initPhoneEmail(userName)
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
        tvAreaCode.setOnClickListener { startActivityForResult(Intent(this, AreaCodeChoiceActivity::class.java), 101) }
        cetPwd.visible()
        tvResetPwd.visible()
        tvResetPwd.setOnClickListener { ResetLoginPwdActivity.launch(this) }

        tvSubmit.text=getString(R.string.login)
        tvSubmit.setOnClickListener {
            if (FastClickUtils.isFastClick(900)) {
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
            dialogAliManMachine= DialogUtils.getInstance().showAliManMachineDialog(this, Constant.ALI_MAN_MACHINE_TYPE_LOGIN)
        }
        initTextWatcher()
        llRegister.setOnClickListener {
            RegisterActivity.launch(this)
            finish()
        }
    }

    override fun initData() {
        Constant.isOfflineRedEnvelopesToRegister = intent.getBooleanExtra("isOfflineRedEnvelopes", false)
    }

    override fun initObserve() {
        viewModel.aliVerifyData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "阿里验证返回数据:$it")
            if (it.isSuccess && it.data?.code == 1) {
                login()
            } else {
                Logger.getInstance().report("人机验证失败", "$TAG-${it.data}")
            }
        })
        viewModel.loginData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "登录返回数据:$it")
            if (it.data?.code == 0 && random == "${it.data.random}") {//登录成功
                LoginHelper.loginSuccess(it.data, this, gUserName(), fromClass)
            } else if (it.data?.code == -99) {//只需要输入短信或邮件验证码
                LoginSmsVerifyActivity.launch(this, loginParam, aliEntity)
            } else if (it.data?.code == -90) {//可以用google验证
                LoginSmsGoogleVerifyActivity.launch(this, loginParam, aliEntity)
            } else {
                SnackBarUtils.ShowRed(this, it.data?.value)
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

    override fun onBackPressed() {
        back()
    }
    private fun login(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        random="1000${System.currentTimeMillis()}5"
        loginParam=LoginParam(
                gUserName(), etPwd.text.toString().trim(), "0", "0", gMessageType(), FingerprintUtil.getFingerprint(BaseApp.getSelf()),
                gAreaCode(), random, "${BuildConfig.VERSION_CODE}", fromClass, "", isPhone
        )
        viewModel.login(loginParam)
    }
    private fun gUserName():String=if(isPhone) etPhone.text.toString().trim() else etEmail.text.toString().trim()
    private fun gAreaCode():String=if(isPhone) tvAreaCode.text.toString().replace("+", "") else ""
    private fun gMessageType():Int=if(isPhone) 16 else 0 //发送短信和邮箱验证码的type
    private fun initPhoneEmail(name: String = ""){
        if(!isPhone) {
            tvPhoneEmail1.text=getString(R.string.email)
            tvPhoneEmail2.text=getString(R.string.phone_login)
            etEmail.visible()
            llPhone.invisible()
            if(!TextUtils.isEmpty(name)&&StringUtil.isEmail(userName)){
                etEmail.setText(name)
            }
//            if (etEmail.text.trim().isNotEmpty()) {
//                InputUtil.checkInputEmail(this, etEmail.text.trim().toString(), tvPhoneOrEmailError)
//            }
        }else{
            tvPhoneEmail1.text=getString(R.string.phone)
            tvPhoneEmail2.text=getString(R.string.email_login)
            llPhone.visible()
            etEmail.invisible()
            if(!TextUtils.isEmpty(name)&&!StringUtil.isEmail(userName)){
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
        etPwd.addTextChangedListener{ text ->
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
    private fun back() {
        if (isTokenExpired) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        finish()
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