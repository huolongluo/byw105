package com.android.legend.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseLoginActivity
import com.android.legend.model.login.LoginParam
import com.android.legend.util.TimerUtil
import com.android.legend.view.edittext.VerificationCodeView
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.util.Constant
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import kotlinx.android.synthetic.main.activity_login_sms_verify.*
import kotlinx.android.synthetic.main.inc_login_btn.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 登录流程验证验证码，只有短信或者邮箱验证码的页面
 */
class LoginSmsVerifyActivity : BaseLoginActivity() {
    private val viewModel:LoginViewModel by viewModels()
    private val loginParam by lazy { intent.getParcelableExtra("loginParam") as LoginParam }
    private lateinit var aliEntity:AliManMachineEntity
    private val timerUtil by lazy { TimerUtil() }
    private var dialogAliManMachine: AlertDialog? = null
    companion object {
        fun launch(context: Context, loginParam: LoginParam, aliEntity: AliManMachineEntity) {
            val intent=Intent(context, LoginSmsVerifyActivity::class.java)
            intent.putExtra("loginParam", loginParam)
            intent.putExtra("aliEntity", aliEntity)
            context.startActivity(intent)
        }
    }
    override fun isRegisterEventBus(): Boolean {
        return true
    }
    override fun initTitle(): String {
        return if(loginParam.isPhone) getString(R.string.msg_code) else getString(R.string.email_code)
    }

    override fun initTips(): String {
        return if(loginParam.isPhone) "" else getString(R.string.email_tips)
    }

    override fun initCodeTips(): Spanned {
        return LoginHelper.getCodeTips(loginParam,this)
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_login_sms_verify
    }

    override fun initView() {
        tvSendCode.setOnClickListener { dialogAliManMachine= DialogUtils.getInstance().showAliManMachineDialog(this, Constant.ALI_MAN_MACHINE_TYPE_CODE) }
        verifyCode.setInputCompleteListener(object : VerificationCodeView.InputCompleteListener {
            override fun inputComplete() {
                if(verifyCode.inputContent.length==6){
                    login()
                }
            }

            override fun deleteContent() {
            }

        })
    }

    override fun initData() {
        aliEntity=intent.getSerializableExtra("aliEntity") as AliManMachineEntity
        getCode()
    }

    override fun initObserve() {
        viewModel.sendCodeData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            if (it.isSuccess) {
                if (it.data?.code == 0) {
                    SnackBarUtils.ShowBlue(this, getString(R.string.ws13))
                    timerUtil.startCountDown(tvSendCode, this)
                } else {
                    SnackBarUtils.ShowRed(this, it.data?.value)
                }
            } else {
                SnackBarUtils.ShowRed(this, it.message)
            }
        })
        viewModel.aliVerifyData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "阿里验证返回数据:$it")
            if (it.isSuccess && it.data?.code == 1) {
                getCode()
            } else {
                Logger.getInstance().report("人机验证失败", "$TAG-${it.data}")
            }
        })
        viewModel.loginData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "登录返回数据:$it")
            if (it.isSuccess) {
                if (it.data?.code == 0) {//登录成功
                    LoginHelper.loginSuccess(it.data,this,loginParam.username,loginParam.fromClass)
                }else{
                    SnackBarUtils.ShowRed(this,it.data?.value)
                }
            }else {
                SnackBarUtils.ShowRed(this, it.message)
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        timerUtil.release()
    }
    private fun login(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        loginParam.googleCode="0"
        loginParam.phoneCode=verifyCode.inputContent
        viewModel.login(loginParam)
    }

    private fun getCode(){
        DialogManager2.INSTANCE.showProgressDialog(this)
        if(loginParam.isPhone) getSms() else getEmailCode()
    }
    private fun getSms(){
        viewModel.sendLoginSms(loginParam, aliEntity)
    }
    private fun getEmailCode(){
        viewModel.sendLoginEmail(loginParam, aliEntity)
    }
    private fun aliVerify(entity: AliManMachineEntity){
        aliEntity=entity
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.aliVerify(entity)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun aliManMachineSuccess(event: BizEvent.AliManMachine) {
        if (event.type == Constant.ALI_MAN_MACHINE_TYPE_CODE) {
            AppHelper.dismissDialog(dialogAliManMachine)
            aliVerify(event.entity)
        }
    }
}