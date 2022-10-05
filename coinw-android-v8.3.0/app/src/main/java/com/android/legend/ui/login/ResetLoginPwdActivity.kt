package com.android.legend.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent.AliManMachine
import com.android.legend.base.BaseLoginActivity
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.model.login.LoginParam
import com.android.legend.util.InputUtil
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.reform.login_regist.AreaCodeChoiceActivity
import huolongluo.byw.util.*
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import huolongluo.bywx.utils.AppUtils
import kotlinx.android.synthetic.main.activity_reset_login_pwd.*
import kotlinx.android.synthetic.main.inc_login_btn.*
import kotlinx.android.synthetic.main.inc_login_phone_email.*
import kotlinx.android.synthetic.main.widget_common_edittext.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 重置登录密码第一步
 */
class ResetLoginPwdActivity : BaseLoginActivity() {
    private val viewModel:LoginViewModel by viewModels()
    private lateinit var loginParam:LoginParam
    var isPhone=true
    private val userName by lazy { SPUtils.getString(this, SPUtils.USER_NAME, "") }
    private var dialogAliManMachine: AlertDialog? = null
    private lateinit var aliEntity: AliManMachineEntity

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, ResetLoginPwdActivity::class.java))
        }
    }

    override fun initTitle(): String {
        return getString(R.string.reset_psw)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initTips(): String {
        return getString(R.string.notice_change_pwd)
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_reset_login_pwd
    }

    override fun initView() {
        tvAreaCode.text=LogicLanguage.getPhonePreByLanguage(this)
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
        tvAreaCode.setOnClickListener { startActivityForResult(Intent(this, AreaCodeChoiceActivity::class.java), 101) }

        tvSubmit.text=getString(R.string.next)
        tvSubmit.setOnClickListener {
            if (FastClickUtils.isFastClick(500)) {
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
            loginParam= LoginParam(gUserName(),"", "0", "0",gMessageType(),"",
                    gAreaCode(),"", "","","",isPhone)
            dialogAliManMachine=DialogUtils.getInstance().showAliManMachineDialog(this, Constant.ALI_MAN_MACHINE_TYPE_RESET_PWD)
        }
        initTextWatcher()
    }

    override fun initData() {
    }

    override fun initObserve() {
        viewModel.aliVerifyFindPwdData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "阿里验证返回数据:$it")
            if (it.isSuccess ) {
                if(it.data?.code == 200){
                    RegisterSmsVerifyActivity.launch(this,loginParam,aliEntity)
                }else{
                    SnackBarUtils.ShowRed(this,it.data?.value)
                }
            } else {
                Logger.getInstance().report("人机验证失败", "$TAG-${it.data}")
            }
        })
    }
    private fun gUserName():String=if(isPhone) etPhone.text.toString().trim() else etEmail.text.toString().trim()
    private fun gAreaCode():String=if(isPhone) tvAreaCode.text.toString().replace("+", "") else ""
    private fun gMessageType():Int=if(isPhone) 9 else 2
    private fun initPhoneEmail(name: String = ""){
        if(!isPhone) {
            tvPhoneEmail1.text=getString(R.string.email)
            tvPhoneEmail2.text=getString(R.string.phone_login)
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
            tvPhoneEmail2.text=getString(R.string.email_login)
            llPhone.visible()
            etEmail.invisible()
            if(!TextUtils.isEmpty(name)){
                etPhone.setText(name)
            }
//            InputUtil.checkInputPhone(this, etPhone.text.trim().toString(), tvPhoneOrEmailError)
        }
        checkEnable()
    }
    private fun checkEnable(){
        tvSubmit.isEnabled =((isPhone&&!TextUtils.isEmpty(etPhone.text.toString()))
                        ||(!isPhone&&!TextUtils.isEmpty(etEmail.text.toString())))
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
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && data != null) {
            val code = data.getStringExtra("areaCode")
            tvAreaCode.text=code
        }
    }
    private fun aliVerify(entity: AliManMachineEntity){
        aliEntity=entity
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.aliVerifyFindPwd(loginParam,entity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun aliManMachineSuccess(event: AliManMachine) {
        if (event.type == Constant.ALI_MAN_MACHINE_TYPE_RESET_PWD) {
            AppHelper.dismissDialog(dialogAliManMachine)
            aliVerify(event.entity)
        }
    }
}