package com.android.legend.ui.mine.safe

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.android.legend.base.BaseActivity
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.common.CommonCodeTypeEnum
import com.android.legend.ui.bottomSheetDialogFragment.CommonSmsGoogleVerifyBottomDialogFragment
import com.android.legend.util.InputUtil
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.FastClickUtils
import huolongluo.byw.util.PasswordChecker
import huolongluo.byw.util.tip.MToast
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.activity_modify_login_pwd.*
import kotlinx.android.synthetic.main.activity_modify_login_pwd.tvSubmit
import kotlinx.android.synthetic.main.inc_pwd_intensity.*

class ModifyLoginPwdActivity : BaseActivity() {
    private val viewModel:ModifyLoginPwdViewModel by viewModels()
    private val passwordChecker by lazy { PasswordChecker() }
    companion object{
        fun launch(context: Context){
            context.startActivity(Intent(context,ModifyLoginPwdActivity::class.java))
        }
    }
    override fun initTitle(): String {
        return getString(R.string.bb54)
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_modify_login_pwd
    }

    override fun initView() {
        cetOldPwd.et.setOnFocusChangeListener { v, hasFocus ->
            vOldPwd.isSelected=hasFocus
        }
        cetNewPwd.et.setOnFocusChangeListener { v, hasFocus ->
            vNewPwd.isSelected=hasFocus
        }
        cetNewPwd2.et.setOnFocusChangeListener { v, hasFocus ->
            vNewPwd2.isSelected=hasFocus
        }
        initTextWatcher()
        tvSubmit.setOnClickListener {
            if(FastClickUtils.isFastClick(500)) return@setOnClickListener
            if(!InputUtil.checkInputPwd(this,cetNewPwd.text.toString().trim(),passwordChecker,null)) return@setOnClickListener
            if(!InputUtil.checkInputPwd(this,cetNewPwd.text.toString().trim(),cetNewPwd2.text.toString().trim(),passwordChecker,null)) return@setOnClickListener
            CommonSmsGoogleVerifyBottomDialogFragment.newInstance(CommonCodeTypeEnum.TYPE_MODIFY_LOGIN_PWD.type,
            object:CommonSmsGoogleVerifyBottomDialogFragment.CodeListener{
                override fun getCode(code: String, isSms: Boolean) {
                    DialogManager2.INSTANCE.showProgressDialog(this@ModifyLoginPwdActivity)
                    viewModel.modifyPwd(cetOldPwd.text.toString().trim(),cetNewPwd.text.toString().trim(),
                    if(isSms) code else "", if(!isSms) code else "")
                }
            }).show(supportFragmentManager,"Dialog")
        }
    }

    override fun initData() {
    }

    override fun initObserve() {
        viewModel.modifyPwdData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            Logger.getInstance().debug(TAG, "修改登录密码返回数据:$it")
            if(it.isSuccess){
                if(it.data?.code==0){
                    MToast.show(this, getString(R.string.change_suc), 1)
                    finish()
                }
                else{
                    SnackBarUtils.ShowRed(this,it.data?.value)
                }
            }else{
                SnackBarUtils.ShowRed(this,it.message)
            }
        })
    }
    private fun initTextWatcher() {
        cetNewPwd.et.addTextChangedListener{text->
            InputUtil.refreshIntensity(this,cetNewPwd.et,clIntensity,tvIntensity, ivPwd1, ivPwd2, ivPwd3, passwordChecker)
            InputUtil.checkInputPwd(this,text.toString().trim(),cetNewPwd2.text.toString().trim(),passwordChecker,tvNewPwdError2)
            if(TextUtils.isEmpty(tvNewPwdError2.text.toString())){
                tvNewPwdError2.gone()
            }else{
                tvNewPwdError2.visible()
            }
            checkEnable()
        }
        cetNewPwd2.et.addTextChangedListener{text->
            InputUtil.checkInputPwd(this,cetNewPwd.text.toString().trim(),text.toString().trim(),passwordChecker,tvNewPwdError2)
            if(TextUtils.isEmpty(tvNewPwdError2.text.toString())){
                tvNewPwdError2.gone()
            }else{
                tvNewPwdError2.visible()
            }
            checkEnable()
        }
    }
    private fun checkEnable(){
        tvSubmit.isEnabled = !TextUtils.isEmpty(cetOldPwd.text.toString())&&!TextUtils.isEmpty(cetNewPwd.text.toString())
                &&!TextUtils.isEmpty(cetNewPwd2.text.toString())
    }
}