package com.android.legend.ui.login

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseLoginActivity
import com.android.legend.model.login.LoginParam
import com.android.legend.util.InputUtil
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.util.FastClickUtils
import huolongluo.byw.util.PasswordChecker
import huolongluo.byw.util.tip.MToast
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.activity_reset_login_pwd_third.*
import kotlinx.android.synthetic.main.activity_reset_login_pwd_third.cetPwd
import kotlinx.android.synthetic.main.activity_reset_login_pwd_third.vPwd
import kotlinx.android.synthetic.main.inc_login_btn.*
import kotlinx.android.synthetic.main.inc_login_phone_email.*
import kotlinx.android.synthetic.main.inc_pwd_intensity.*
import org.greenrobot.eventbus.EventBus

/**
 * 重置登录密码第三步
 */
class ResetLoginPwdThirdActivity : BaseLoginActivity() {
    private val viewModel:ResetLoginPwdViewModel by viewModels()
    private val loginParam by lazy { intent.getParcelableExtra("loginParam") as LoginParam }
    private val passwordChecker by lazy { PasswordChecker() }

    companion object {
        fun launch(context: Context, loginParam: LoginParam) {
            val intent= Intent(context, ResetLoginPwdThirdActivity::class.java)
            intent.putExtra("loginParam", loginParam)
            context.startActivity(intent)
        }
    }
    override fun initTitle(): String {
        return getString(R.string.set_new_pwd)
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_reset_login_pwd_third
    }

    override fun initView() {
        tvSubmit.text=getString(R.string.next)
        cetPwd.et.setOnFocusChangeListener { v, hasFocus ->
            vPwd.isSelected=hasFocus
        }
        cetPwd2.et.setOnFocusChangeListener { v, hasFocus ->
            vPwd2.isSelected=hasFocus
        }
        initTextWatcher()
        tvSubmit.setOnClickListener {
            if(FastClickUtils.isFastClick(500)) return@setOnClickListener
            if(!InputUtil.checkInputPwd(this,cetPwd.text.toString().trim(),passwordChecker,null)) return@setOnClickListener
            if(!InputUtil.checkInputPwd(this,cetPwd.text.toString().trim(),cetPwd2.text.toString().trim(),passwordChecker,null)) return@setOnClickListener
            DialogManager2.INSTANCE.showProgressDialog(this)
            loginParam.password=cetPwd2.text.toString().trim()
            viewModel.resetPwd(loginParam)
        }
    }

    override fun initData() {
    }

    override fun initObserve() {
        viewModel.resetPwdData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "重置密码返回数据:$it")
            if(it.isSuccess){
                if(it.data?.code=="200"){
                    MToast.show(this, getString(R.string.reset_success), 1)
                    val intent=Intent(this,LoginActivity::class.java)
                    intent.putExtra("isPhone",loginParam.isPhone)
                    intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                else{
                    SnackBarUtils.ShowRed(this,it.data?.message)
                }
            }else{
                SnackBarUtils.ShowRed(this,it.message)
            }
        })
    }
    private fun initTextWatcher() {
        cetPwd.et.addTextChangedListener{text->
            InputUtil.refreshIntensity(this,cetPwd.et,clIntensity,tvIntensity, ivPwd1, ivPwd2, ivPwd3, passwordChecker)
            InputUtil.checkInputPwd(this,text.toString().trim(),cetPwd2.text.toString().trim(),passwordChecker,tvPwdError2)
            checkEnable()
        }
        cetPwd2.et.addTextChangedListener{text->
            InputUtil.checkInputPwd(this,cetPwd.text.toString().trim(),text.toString().trim(),passwordChecker,tvPwdError2)
            checkEnable()
        }
    }
    private fun checkEnable(){
        tvSubmit.isEnabled = !TextUtils.isEmpty(cetPwd.text.toString())  &&!TextUtils.isEmpty(cetPwd2.text.toString())
    }
}