package com.android.legend.ui.taskcenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.coinw.biz.event.BizEvent
import com.android.legend.common.isLoginOrGotoLoginActivity
import com.android.legend.model.TaskDetail
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.utils.ToastUtils
import huolongluo.byw.R
import huolongluo.byw.byw.share.Event
import huolongluo.byw.databinding.ActivityTaskCenterMoreBinding
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.dialog_my_task.view.*
import kotlinx.android.synthetic.main.item_my_task2.view.*
import kotlinx.android.synthetic.main.item_task_center.view.*
import kotlinx.android.synthetic.main.item_task_center_more.view.*
import kotlinx.android.synthetic.main.item_task_center_more2.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class TaskCenterMoreActivity : BaseActivity<TaskCenterViewModel>() {


    companion object {
        fun launch(context: Context, intent: Intent) {
            intent.setClass(context, TaskCenterMoreActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val textGray by lazy {
        ContextCompat.getColor(this, R.color.color_FEFEFC)
    }
    private val textBlue by lazy {
        ContextCompat.getColor(this, R.color.color_8F72FB)
    }
    private val textWhite by lazy {
        ContextCompat.getColor(this, R.color.white)
    }
    private val moreAdapter by lazy { getMoreTaskListAdapter() }
    lateinit var listener: SwipeRefreshLayout.OnRefreshListener
    lateinit var mBinding: ActivityTaskCenterMoreBinding
    private var classify: Int = 0
    private var position: Int = 0
    private var list = mutableListOf<TaskDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_task_center_more)
        initView()
        initData()
        showTitle()
        initLiveData()
    }

    private fun showTitle() {
        when (classify) {
            1 ->
                applyToolBar(getString(R.string.new_task))
            8 ->
                applyToolBar(getString(R.string.spot_task))
            11 ->
                applyToolBar(getString(R.string.contract_task))
            14 ->
                applyToolBar(getString(R.string.study_task))
            else ->
                applyToolBar(getString(R.string.unknown_task))
        }
    }

    override fun createViewModel() = ViewModelProvider(this).get(TaskCenterViewModel::class.java)

    private fun initView() {
        mBinding.rvTaskListMore.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvTaskListMore.adapter = moreAdapter
        moreAdapter.setFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space_50, mBinding.rvTaskListMore, false))
        moreAdapter.setEmptyView(R.layout.base_no_data1)
        //SwipeRefreshLayout下拉刷新回调
        listener = SwipeRefreshLayout.OnRefreshListener {
            mViewModel.getTaskCenterList(classify, UserInfoManager.getToken())
        }
        mBinding.refreshLayout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72)
        mBinding.refreshLayout.setOnRefreshListener(listener)
    }

    private fun initData() {
        classify = intent.getIntExtra("classify", 0)
        mViewModel.getTaskCenterList(classify, UserInfoManager.getToken())
        //val list = intent.getParcelableExtra<TaskDetail>("taskCenterList")
    }

    private fun initLiveData() {
        mViewModel.mGetMyTaskBeanLiveDate.observe(this, Observer {
            if (it.code == "200") {
                showDialog()
                //刷新列表
                autoRefresh()
                //发通知刷新任务中心列表
                EventBus.getDefault().post(BizEvent.TaskCenterRefresh())
            } else {
                ToastUtils.showShortToast(it.message)
            }
        })

        mViewModel.mTaskAllBeanLiveDate.observe(this, Observer {
            if (it.isSuccess) {
                if (mBinding.refreshLayout.isRefreshing) {
                    SnackBarUtils.ShowBlue(this, getString(R.string.dd45))
                }

                list.clear()
                when (classify) {
                    1 -> {
                        it.data?.NEW_TASK?.apply {
                            list.addAll(this)
                        }
                    }
                    8 -> {
                        it.data?.SPOT_TASK?.apply {
                            list.addAll(this)
                        }
                    }
                    11 -> {
                        it.data?.CONTARCT_TASK?.apply {
                            list.addAll(this)
                        }
                    }
                    14 -> {
                        it.data?.STUDY_TASK?.apply {
                            list.addAll(this)
                        }
                    }
                }
            }
            moreAdapter.setList(list)
            if (mBinding.refreshLayout.isRefreshing) {
                refreshFinish()
            }
        })
    }

    /**
     * SwipeRefreshLayout刷新结束
     */
    private fun refreshFinish() {
        mBinding.refreshLayout.isRefreshing = false
    }

    /**
     * SwipeRefreshLayout自动刷新
     */
    private fun autoRefresh() {
        mBinding.refreshLayout.isRefreshing = true
        listener.onRefresh()
    }


    private fun getMoreTaskListAdapter(): BaseQuickAdapter<TaskDetail, BaseViewHolder> =
            // object : BaseQuickAdapter<TaskDetail, BaseViewHolder>(R.layout.item_task_center_more) {
            object : BaseQuickAdapter<TaskDetail, BaseViewHolder>(R.layout.item_task_center_more2) {
                override fun convert(holder: BaseViewHolder, item: TaskDetail) {
                    holder.itemView.apply {
                        tvTaskMoreDes.text = "${item.content}"
                        tvTaskMoreTime.text = ": ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date(item.dueTime))}"
                        tvTaskMoreTitle.text = item.name

                        when (item.awardType) {
                            1 -> {
                                //1现货赠金2合约体验金
                                tvTaskMoreNumber.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.bb_cash)}"
                            }
                            2 -> {
                                tvTaskMoreNumber.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.contract_cash)}"
                            }
                            else -> {
                                tvTaskMoreNumber.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.contract_cash)}"
                            }
                        }

                        var statusIndex = 0
                        if (!UserInfoManager.isLogin()) {
                            //如果没有登录，tvGet全部显示立即领取
                            statusIndex = 1
                        } else {
                            //已经领取
                            statusIndex = if (item.isClaimTask == 1) {
                                2
                            } else if (!item.isQualified) {
                                //不符合领取条件
                                3
                            } else if (item.receiveNumber >= item.number) {
                                //已经领光了
                                4
                            } else {
                                //未领取
                                1
                            }
                        }

                        when (statusIndex) {
                            1 -> {
                                tvTaskMoreStatus.text = getString(R.string.task_get)
                                tvTaskMoreStatus.isEnabled = true
                                tvTaskMoreStatus.isClickable = true
                                tvTaskMoreStatus.setTextColor(textWhite)
                            }
                            2 -> {
                                tvTaskMoreStatus.text = getString(R.string.task_already)
                                tvTaskMoreStatus.isEnabled = false
                                tvTaskMoreStatus.isClickable = false
                                tvTaskMoreStatus.setTextColor(textGray)
                            }
                            3 -> {
                                tvTaskMoreStatus.text = getString(R.string.task_not_match)
                                tvTaskMoreStatus.isEnabled = false
                                tvTaskMoreStatus.isClickable = false
                                tvTaskMoreStatus.setTextColor(textGray)
                            }
                            4 -> {
                                tvTaskMoreStatus.text = getString(R.string.task_null)
                                tvTaskMoreStatus.isEnabled = false
                                tvTaskMoreStatus.isClickable = false
                                tvTaskMoreStatus.setTextColor(textGray)
                            }
                        }

                        tvTaskMoreDes.setOnClickListener {
                            showTaskDESDialog(item.content)
                        }
                        tvTaskMoreStatus.setOnClickListener {
                            if (isLoginOrGotoLoginActivity(this@TaskCenterMoreActivity)) {
                                mViewModel.getMyTask(item.id, UserInfoManager.getToken())
                                position = getItemPosition(item)
                            }
                        }
                    }
                }
            }

    /**
     * 任务描述对话框
     */
    private fun showTaskDESDialog(content: String?) {
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

    /**
     * 领取任务对话框
     */
    private fun showDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.decorView?.background = null;
        dialog.setCanceledOnTouchOutside(false)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_my_task, null, false)
        dialog.setView(view)
        view.findViewById<ImageView>(R.id.tvClose).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.tvCheckTask).setOnClickListener {
            MyTaskActivity.launch(this)
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * 登录成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshInfo(refreshInfo: Event.refreshInfo?) {
        //刷新列表
        autoRefresh()
    }
}

