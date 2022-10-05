package com.android.legend.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseLoginActivity
import com.android.legend.extension.gone
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.model.login.LoginParam
import com.android.legend.util.ClipboardUtils
import com.android.legend.util.TimerUtil
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.util.Constant
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import kotlinx.android.synthetic.main.activity_login_sms_google_verify.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 登录流程验证验证码，包含google和短信或者邮箱验证码的页面
 */
class LoginSmsGoogleVerifyActivity : BaseLoginActivity() {
    private val viewModel:LoginViewModel by viewModels()
    private val loginParam by lazy { intent.getParcelableExtra("loginParam") as LoginParam }
    private lateinit var aliEntity:AliManMachineEntity
    private val timerUtil by lazy { TimerUtil() }
    private var dialogAliManMachine: AlertDialog? = null
    private var isGoogle=true //默认google
    companion object {
        fun launch(context: Context, loginParam: LoginParam, aliEntity: AliManMachineEntity) {
            val intent=Intent(context, LoginSmsGoogleVerifyActivity::class.java)
            intent.putExtra("loginParam", loginParam)
            intent.putExtra("aliEntity", aliEntity)
            context.startActivity(intent)
        }
    }
    override fun isRegisterEventBus(): Boolean {
        return true
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_login_sms_google_verify
    }

    override fun initView() {
        refreshChangeUi()
        tvGoogle.setOnClickListener {
            if(isGoogle) return@setOnClickListener
            isGoogle=true
            refreshChangeUi()
        }
        tvCode.setOnClickListener {
            if(!isGoogle) return@setOnClickListener
            isGoogle=false
            refreshChangeUi()
        }
        tvCodeTips.text = LoginHelper.getCodeTips(loginParam,this)
        tvCode.text=if(loginParam.isPhone) getString(R.string.msg) else getString(R.string.email_verify)
        tvSend.setOnClickListener { dialogAliManMachine= DialogUtils.getInstance().showAliManMachineDialog(this, Constant.ALI_MAN_MACHINE_TYPE_CODE) }
        tvPaste.setOnClickListener {
            etGoogle.setText(ClipboardUtils.getPasteText(this))
        }
        etGoogle.addTextChangedListener{ text->
            if(text?.length?:0>=6){
                loginParam.phoneCode="0"
                loginParam.googleCode=text.toString()
                login()
            }
        }
        etCode.addTextChangedListener{ text ->
            if(text?.length?:0>=6){
                loginParam.googleCode="0"
                loginParam.phoneCode=text.toString()
                login()
            }
        }
    }

    override fun initData() {
        aliEntity=intent.getSerializableExtra("aliEntity") as AliManMachineEntity
    }

    override fun initObserve() {
        viewModel.sendCodeData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            if (it.isSuccess) {
                if (it.data?.code == 0) {
                    SnackBarUtils.ShowBlue(this, getString(R.string.ws13))
                    timerUtil.startCountDown(tvSend, this)
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
    private fun refreshChangeUi(){
        if(isGoogle){
            tvGoogle.isSelected=true
            tvCode.isSelected=false
            vGoogle.visible()
            vCode.invisible()
            etGoogle.visible()
            tvPaste.visible()
            clCode.gone()
        }else{
            tvGoogle.isSelected=false
            tvCode.isSelected=true
            vGoogle.invisible()
            vCode.visible()
            etGoogle.gone()
            tvPaste.gone()
            clCode.visible()
        }
    }
    private fun login(){
        DialogManager2.INSTANCE.showProgressDialog(this)
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