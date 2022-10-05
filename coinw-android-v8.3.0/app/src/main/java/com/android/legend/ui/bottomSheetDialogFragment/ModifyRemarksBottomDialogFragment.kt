package com.android.legend.ui.bottomSheetDialogFragment

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.legend.common.base.BaseBottomSheetDialogFragment
import huolongluo.byw.R
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.fragment_modify_remarks.*

/**
 * 修改备注内容
 */
class ModifyRemarksBottomDialogFragment : BaseBottomSheetDialogFragment() {
    companion object {
        private var remarksListener:RemarksListener?=null
        fun newInstance(remarksListener: RemarksListener): ModifyRemarksBottomDialogFragment {
            val args = Bundle()
            val fragment = ModifyRemarksBottomDialogFragment()
            fragment.arguments = args
            this.remarksListener=remarksListener
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
        return inflater.inflate(R.layout.fragment_modify_remarks, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {//去除背景默认的白色
        val dialog: BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = View.inflate(context, R.layout.fragment_modify_remarks, null)
        dialog.setContentView(view)

        (view.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        ivBack.setOnClickListener { dismiss() }
        tvSubmit.setOnClickListener {
            if(TextUtils.isEmpty(etRemarks.text.toString())){
                MToast.show(requireContext(), getString(R.string.bb94), 1)
                return@setOnClickListener
            }
            remarksListener?.getRemarks(etRemarks.text.toString())
            dismiss()
        }
    }


    interface RemarksListener{
        fun getRemarks(remarks: String)
    }
}