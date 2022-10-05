package com.android.legend.ui.bottomSheetDialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.coinw.biz.event.BizEvent
import com.android.legend.extension.gone
import com.android.legend.extension.invisible
import com.android.legend.extension.visible
import com.android.legend.util.ClipboardUtils
import com.android.legend.util.TimerUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.legend.common.base.BaseBottomSheetDialogFragment
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.Constant
import huolongluo.byw.util.StringUtil
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.helper.AppHelper
import kotlinx.android.synthetic.main.fragment_common_sms_google_verify.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 通用的包含google或者短信验证码二选一的页面
 */
class CommonSmsGoogleVerifyBottomDialogFragment : BaseBottomSheetDialogFragment() {
    private val viewModel: CommonSmsGoogleVerifyViewModel by viewModels()
    private val type:Int by lazy { arguments?.getInt("type", 0)?:0 }//发送短信的type
    private lateinit var aliEntity:AliManMachineEntity
    private val timerUtil by lazy { TimerUtil() }
    private var dialogAliManMachine: AlertDialog? = null
    private var isGoogle=true //默认google
    companion object {
        private var codeListener:CodeListener?=null
        fun newInstance(type: Int,codeListener:CodeListener): CommonSmsGoogleVerifyBottomDialogFragment {
            val args = Bundle()
            val fragment = CommonSmsGoogleVerifyBottomDialogFragment()
            args.putInt("type", type)
            fragment.arguments = args

            this.codeListener=codeListener
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_common_sms_google_verify, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {//去除背景默认的白色
        val dialog: BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = View.inflate(context, R.layout.fragment_common_sms_google_verify, null)
        dialog.setContentView(view)

        (view.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.transparent))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerUtil.release()
        EventBus.getDefault().unregister(this)
    }
    private fun initView() {
        if(UserInfoManager.getUserInfo().isGoogleBind&&UserInfoManager.getUserInfo().isBindMobil){
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
        }
        else if(UserInfoManager.getUserInfo().isGoogleBind){
            refreshChangeUi()
            tvCode.gone()
            vGoogle.invisible()
        }
        else{
            isGoogle=false
            refreshChangeUi()
            tvGoogle.gone()
            vGoogle.gone()
            vCode.gone()
        }

        tvCodeTips.text = Html.fromHtml(String.format(getString(R.string.phone_code_tips),
                "${StringUtil.getHidePhone(UserInfoManager.getUserInfo().tel)}"))
        tvSend.setOnClickListener { dialogAliManMachine= DialogUtils.getInstance().showAliManMachineDialog(requireContext(), Constant.ALI_MAN_MACHINE_TYPE_CODE) }
        tvPaste.setOnClickListener {
            etGoogle.setText(ClipboardUtils.getPasteText(requireContext()))
        }
        etGoogle.addTextChangedListener{ text->
            verifyBtnEnable(text.toString())
        }
        etCode.addTextChangedListener{ text ->
            verifyBtnEnable(text.toString())
        }
        tvSubmit.setOnClickListener { verify() }
        ivBack.setOnClickListener { dismiss() }
    }

    private fun initObserve() {
        viewModel.smsData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            if (it.isSuccess) {
                if (it.data?.code == 0) {
                    SnackBarUtils.ShowBlue(requireActivity(), getString(R.string.ws13))
                    timerUtil.startCountDown(tvSend, requireContext())
                } else {
                    SnackBarUtils.ShowRed(requireActivity(), it.data?.value)
                }
            } else {
                SnackBarUtils.ShowRed(requireActivity(), it.message)
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
    }
    private fun verifyBtnEnable(text:String){
        tvSubmit.isEnabled = !TextUtils.isEmpty(text)
    }
    private fun verify(){
        codeListener?.getCode(gCode(),!isGoogle)
        dismiss()
    }
    private fun gCode():String{
       return if(isGoogle) etGoogle.text.toString().trim() else etCode.text.toString().trim()
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
    private fun submit(){
//        DialogManager2.INSTANCE.showProgressDialog(this)
//        viewModel.login(loginParam)
    }
    private fun getCode(){
        DialogManager2.INSTANCE.showProgressDialog(requireContext())
        getSms()
    }
    private fun getSms(){
        viewModel.sendSms(type,aliEntity)
    }
    private fun aliVerify(entity: AliManMachineEntity){
        aliEntity=entity
        DialogManager2.INSTANCE.showProgressDialog(requireContext())
        viewModel.aliVerify(entity)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun aliManMachineSuccess(event: BizEvent.AliManMachine) {
        if (event.type == Constant.ALI_MAN_MACHINE_TYPE_CODE) {
            AppHelper.dismissDialog(dialogAliManMachine)
            aliVerify(event.entity)
        }
    }
    interface CodeListener{
        fun getCode(code:String,isSms:Boolean)
    }
}