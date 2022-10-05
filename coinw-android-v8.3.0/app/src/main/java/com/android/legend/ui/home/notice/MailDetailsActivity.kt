package com.android.legend.ui.home.notice

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.android.legend.base.BaseActivity
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.activity_mail_details.*

/**
 * 站内信详情
 */
class MailDetailsActivity : BaseActivity() {
    private val viewModel:MailDetailsViewModel by viewModels()

    override fun getContentViewId(): Int {
        return R.layout.activity_mail_details
    }

    override fun initTitle(): String {
        return getString(R.string.mail)
    }

    override fun initView() {
    }

    override fun initData() {
        val fId=intent.getLongExtra("fId",0)
        getMailDetails(fId)
    }

    override fun initObserve() {
        viewModel.mailData.observe(this, Observer {
            DialogManager2.INSTANCE.dismiss()
            if (it.isSuccess){
                tvTitle2.text=it.data?.fTitle
                tvTime.text=it.data?.fCreateDate
                tvContent.text=it.data?.fContent
            }else{
                MToast.show(this, it.message, 1)
            }
        })
    }

    private fun getMailDetails(fId:Long){
        DialogManager2.INSTANCE.showProgressDialog(this)
        viewModel.getMailDetails(fId)
    }
}