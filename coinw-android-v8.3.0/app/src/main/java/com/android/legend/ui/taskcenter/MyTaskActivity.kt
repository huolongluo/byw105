package com.android.legend.ui.taskcenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.legend.model.MyTaskDetail
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.base.BaseActivity
import huolongluo.byw.R
import huolongluo.byw.databinding.ActivityMyTaskBinding
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.MToast
import kotlinx.android.synthetic.main.activity_zj_record.*
import kotlinx.android.synthetic.main.dialog_my_task.view.*
import kotlinx.android.synthetic.main.item_my_task.view.*
import kotlinx.android.synthetic.main.item_my_task2.view.*
import kotlinx.android.synthetic.main.item_task_center.view.*
import kotlinx.android.synthetic.main.pop_all_my_task.view.*
import kotlinx.android.synthetic.main.pop_all_zj.view.*
import kotlinx.android.synthetic.main.pop_all_zj.view.tvCancel
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class MyTaskActivity : BaseActivity<MyTaskViewModel>() {


    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, MyTaskActivity::class.java))
        }
    }

    private val textYellow by lazy {
        ContextCompat.getColor(this, R.color.color_FF9201)
    }
    private val textBlue by lazy {
        ContextCompat.getColor(this, R.color.color_8F72FB)
    }
    private val textGray by lazy {
        ContextCompat.getColor(this, R.color.color_FEFEFC)
    }
    private val textGray2 by lazy {
        ContextCompat.getColor(this, R.color.color_C2C3CD)
    }
    private val myTaskListAdapter by lazy { getMyTaskAdapter() }
    lateinit var mBinding: ActivityMyTaskBinding
    private var allStatePopupWindow: PopupWindow? = null
    private var list = mutableListOf<MyTaskDetail>()
    private var stateIndex = 0//状态类型索引

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_task)
        applyToolBar(getString(R.string.my_task))
        initView()
        initData()
        initLiveData()
    }

    override fun createViewModel() = ViewModelProvider(this).get(MyTaskViewModel::class.java)


    private fun initView() {
        mBinding.tvState.setOnClickListener { showAllStatePop() }
        mBinding.rvMyTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvMyTaskList.adapter = myTaskListAdapter
        myTaskListAdapter.setEmptyView(R.layout.base_no_data1)
        myTaskListAdapter.setFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space_50, mBinding.rvMyTaskList, false))
    }

    private fun initData() {
        mViewModel.getMyTaskList(UserInfoManager.getToken())
    }


    private fun initLiveData() {
        mViewModel.mMyTaskAllBeanLiveDate.observe(this, androidx.lifecycle.Observer {
            //  var list: MutableList<MyTaskDetail> = mutableListOf()
            if (it.isSuccess) {
                list.clear()
                it.data?.PENDING_PAY?.let { it1 ->
                    list.addAll(it1)
                    //默认显示进行中的任务
                    myTaskListAdapter.setList(it1)
                }
                it.data?.COMPLETE?.let { it1 -> list.addAll(it1) }
                it.data?.PROVIDED?.let { it1 -> list.addAll(it1) }// 已发放 和已完成一样
                it.data?.EXPIRED?.let { it1 -> list.addAll(it1) }

            } else {
                MToast.show(this, it.message)
            }
        })
    }

    private fun showAllStatePop() {
        if (allStatePopupWindow == null) {
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_all_my_task, null, false)
            allStatePopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            contentView.tvCancel.setOnClickListener { allStatePopupWindow?.dismiss() }
            val tvs = arrayOf(contentView.tvAll, contentView.tvPending, contentView.tvComplete, contentView.tvExpired)
            tvs.forEachIndexed { index, view ->
                view.setOnClickListener {
                    if (it.isSelected) return@setOnClickListener
                    tvs.forEach { view -> view.isSelected = false }
                    it.isSelected = true
                    allStatePopupWindow?.dismiss()
                    stateIndex = index
                    filter()
                }
            }
            contentView.tvPending.isSelected = true
            val location = IntArray(2)
            mBinding.tvState.getLocationOnScreen(location)
        }
        allStatePopupWindow?.showAtLocation(mBinding.tvState, Gravity.BOTTOM, 0, 0)
    }

    private fun filter() {
//        if (list.size == 0) {
//            return
//        }
        val listFilter = mutableListOf<MyTaskDetail>()
        when (stateIndex) {
            0 -> {//全部状态
                listFilter.addAll(list)
                mBinding.tvState.text = getString(R.string.all_state)
            }
            1 -> {//进行中
                listFilter.addAll(list.filter { it.taskState == 1 })
                mBinding.tvState.text = getString(R.string.state_pending_pay)
            }
            2 -> {//已完成
                listFilter.addAll(list.filter { it.taskState == 2 || it.taskState == 3 })
                mBinding.tvState.text = getString(R.string.state_complete)
            }
            3 -> {//已过期
                listFilter.addAll(list.filter { it.taskState == 4 })
                mBinding.tvState.text = getString(R.string.state_expired)
            }
        }
        myTaskListAdapter.setList(listFilter)
    }

    /**
     * 任务描述对话框
     */
    private fun showTaskDESDialog(content: String) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.decorView?.background = null;
        dialog.setCanceledOnTouchOutside(false)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_my_task_des, null, false)
        dialog.setView(view)
        view.tvCheckTask.text = getString(R.string.des_ok)
        view.findViewById<TextView>(R.id.tvDesContent).text = content
        view.findViewById<ImageView>(R.id.tvClose).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.tvCheckTask).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getMyTaskAdapter(): BaseQuickAdapter<MyTaskDetail, BaseViewHolder> =
            // object : BaseQuickAdapter<MyTaskDetail, BaseViewHolder>(R.layout.item_my_task) {
            object : BaseQuickAdapter<MyTaskDetail, BaseViewHolder>(R.layout.item_my_task2) {
                override fun convert(holder: BaseViewHolder, item: MyTaskDetail) {
                    holder.itemView.apply {
                        when (item.taskState) {
                            //任务状态 1：进行中 2：已完成 3:已发放 4：已过期
                            1 -> {
                                tvTaskStatus.text = getString(R.string.state_pending_pay)
                                tvTaskStatus.setTextColor(textBlue)
                                ivTaskMyProgress.background = resources.getDrawable(R.mipmap.ic_task_progress)
                            }
                            2 -> {
                                tvTaskStatus.text = getString(R.string.state_complete)
                                tvTaskStatus.setTextColor(textBlue)
                                ivTaskMyProgress.background = resources.getDrawable(R.mipmap.ic_task_compele)
                            }
                            3 -> {
                                tvTaskStatus.text = getString(R.string.state_complete)
                                tvTaskStatus.setTextColor(textBlue)
                                ivTaskMyProgress.background = resources.getDrawable(R.mipmap.ic_task_compele)
                            }
                            4 -> {
                                tvTaskStatus.text = getString(R.string.state_expired)
                                tvTaskStatus.setTextColor(textGray2)
                                ivTaskMyProgress.background = resources.getDrawable(R.mipmap.ic_task_expired)
                            }
                            else ->
                                tvTaskStatus.text = getString(R.string.state_expired)
                        }

                        tvMyTitle.text = item.taskName
                        tvTaskMyDes.text = item.content
                        tvTime.text = ": ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date(item.dueTime))}"
                        pbTaskProgressbar.max = item.totalAmount.toInt()
                        pbTaskProgressbar.progress = item.finishAmount.toInt()
                        // val progressNum = BigDecimal(item.totalAmount.toString()).toPlainString()
                        // val progressNum = item.totalAmount.toString()
                        // val pattern = Pattern.compile("^[-\\+]?[\\d]*$")
                        val isInt = item.totalAmount - floor(item.totalAmount) < 1e-10
                        val isInt2 = item.finishAmount - floor(item.finishAmount) < 1e-10
                        if (isInt) {
                            if (isInt2) {
                                tvTaskProgress.text = "${BigDecimal(item.finishAmount.toString()).toInt()}/${BigDecimal(item.totalAmount.toString()).toInt()}"
                            } else {
                                tvTaskProgress.text = "${BigDecimal(item.finishAmount.toString()).toPlainString()}/${BigDecimal(item.totalAmount.toString()).toInt()}"
                            }
                        } else {
                            if (isInt2) {
                                tvTaskProgress.text = "${BigDecimal(item.finishAmount.toString()).toInt()}/${BigDecimal(item.totalAmount.toString()).toPlainString()}"
                            } else {
                                tvTaskProgress.text = "${BigDecimal(item.finishAmount.toString()).toPlainString()}/${BigDecimal(item.totalAmount.toString()).toPlainString()}"
                            }
                        }

                        when (item.taskAwardType) {
                            1 -> {
                                //1现货赠金2合约体验金
                                tvNumber.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.bb_cash)}"
                            }
                            2 -> {
                                tvNumber.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.contract_cash)}"
                            }
                            else -> {
                                tvNumber.text = "+0 ${getString(R.string.contract_cash)}"
                            }
                        }
//                        TypefaceUtil.setDinproMedium(this@MyTaskActivity,tvNumber)
//                            if (item.taskAward?.amount.equals(Double.NaN)) {
//                                tvNumber.text = "+0 ${getString(R.string.contract_cash)}"
//                            }
                        tvTaskMyDes.setOnClickListener {
                            showTaskDESDialog(item.content)
                        }
                    }
                }
            }
}
