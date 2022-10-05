package com.android.legend.ui.bottomSheetDialogFragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.legend.common.base.BaseBottomSheetDialogFragment
import huolongluo.byw.R
import huolongluo.byw.byw.bean.UserInfoBean
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.dialog_senior_kyc.*

//高级实名认证
class SeniorKycBottomDialogFragment : BaseBottomSheetDialogFragment() {
    val viewModel:SeniorKycViewModel by viewModels()
    val userInfoBean: UserInfoBean by lazy { UserInfoManager.getUserInfo() }

    companion object {
        fun newInstance(): SeniorKycBottomDialogFragment {
            val args = Bundle()
            val fragment = SeniorKycBottomDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_senior_kyc, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
    }

    private fun initView() {
        llAgree.setOnClickListener {
            ivAgree.isSelected=!ivAgree.isSelected
            tvAgree.isEnabled=ivAgree.isSelected
        }
        tvCancel.setOnClickListener { dismiss() }
        tvInfo.text=String.format(getString(R.string.kyc_info),userInfoBean.realName,userInfoBean.identityNo)
        tvAgree.setOnClickListener {
            DialogManager2.INSTANCE.showProgressDialog(requireContext())
            viewModel.overseaSeniorKyc()
        }

        dialog?.window?.setWindowAnimations(R.style.AnimBottomIn)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun initObserve() {
        viewModel.agreeLiveData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            if (it.isSuccess){
                MToast.show(context,getString(R.string.auth_success))
                userInfoBean.isHasC3Validate=true
                dismiss()
                val intent=Intent(context,AuthActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }else{
                MToast.show(requireContext(),it.message)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}