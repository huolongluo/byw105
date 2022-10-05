package com.android.legend.ui.taskcenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.coinw.biz.event.BizEvent
import com.android.legend.common.isLoginOrGotoLoginActivity
import com.android.legend.model.TaskDetail
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.utils.ToastUtils
import huolongluo.byw.R
import huolongluo.byw.byw.share.Event
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment
import huolongluo.byw.databinding.ActivityTaskCenterBinding
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.dialog_my_task.view.*
import kotlinx.android.synthetic.main.dialog_my_task.view.tvCheckTask
import kotlinx.android.synthetic.main.dialog_my_task.view.tvClose
import kotlinx.android.synthetic.main.dialog_my_task_des.view.*
import kotlinx.android.synthetic.main.fragment_finance_zj.*
import kotlinx.android.synthetic.main.item_my_task.view.*
import kotlinx.android.synthetic.main.item_task_center.view.*
import kotlinx.android.synthetic.main.item_task_center_1.view.*
import kotlinx.android.synthetic.main.item_task_center_2.view.*
import kotlinx.android.synthetic.main.item_task_center_more2.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class TaskCenterActivity : BaseActivity<TaskCenterViewModel>() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TaskCenterActivity::class.java))
        }
    }

    private val textGray by lazy {
        ContextCompat.getColor(this, R.color.color_FEFEFC)
    }
    private val textWhite by lazy {
        ContextCompat.getColor(this, R.color.white)
    }
    private val textBlue by lazy {
        ContextCompat.getColor(this, R.color.color_8F72FB)
    }
    lateinit var mBinding: ActivityTaskCenterBinding

    // private val taskListAdapter by lazy { getTaskCenterListAdapter() }
    private val taskCenterAdapter by lazy { getMultiItemListAdapter() }
    lateinit var imgBanner: ImageView

    //总任务集合，每种任务最多显示2条
    private var taskList = mutableListOf<TaskDetail>()

    lateinit var listener: SwipeRefreshLayout.OnRefreshListener

    private var skipIndex: Int = 0 //1,我的任务，2,我的赠金

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_task_center)
        // rightBtnImgResId = R.mipmap.ic_task_center_help
        applyToolBar(getString(R.string.task_center)) {
            //跳转
        }
        initView()
        initData()
        initLiveData()
    }

    override fun createViewModel() = ViewModelProvider(this).get(TaskCenterViewModel::class.java)

    private fun initView() {
        // imgBanner = taskListAdapter.headerLayout?.findViewById<ImageView>(R.id.iv_banner)
        mBinding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvTaskList.adapter = taskCenterAdapter
        // taskCenterAdapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.task_center_header, mBinding.rvTaskList, false))
        taskCenterAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space_50, mBinding.rvTaskList, false))
        //设置空view
        taskCenterAdapter.setEmptyView(R.layout.base_no_data1)

        mBinding.tvMyTask.setOnClickListener {
            //我的任务
            skipIndex = 1
            if (isLoginOrGotoLoginActivity(this)) {
                MyTaskActivity.launch(this)
            }

        }
        mBinding.tvMyCapital.setOnClickListener {
            //我的赠金
            skipIndex = 2
            if (isLoginOrGotoLoginActivity(this)) {
                MainActivity.self?.gotoFinance(AllFinanceFragment.TYPE_ZJ)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        //SwipeRefreshLayout下拉刷新回调
        listener = SwipeRefreshLayout.OnRefreshListener {
            mViewModel.getTaskCenterList(UserInfoManager.getToken())
        }
        // 设置下拉进度的主题颜色
        mBinding.refreshLayout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72)
        mBinding.refreshLayout.setOnRefreshListener(listener)
    }

    private fun initData() {
        //Banner图用静态的，不用后台返回的图
        // mViewModel.getTaskCenterBanner()
        mViewModel.getTaskCenterList(UserInfoManager.getToken())
        Log.i("mage", "initData")
    }

    private fun initLiveData() {
//        mViewModel.mBannerLiveDate.observe(this, Observer {
//            val img = it?.data?.APP_BANNER_4?.get(0)?.img.toString()
//            Glide.with(this).load(img).into(imgBanner)
//        })
        mViewModel.mTaskAllBeanLiveDate.observe(this, Observer {
            taskList.clear()
            if (it.isSuccess) {
                if (mBinding.refreshLayout.isRefreshing) {
                    SnackBarUtils.ShowBlue(this, getString(R.string.dd45))
                }
                //新手任务
                it.data?.NEW_TASK?.apply {
                    //1:新手任务,8新币任务,11合约任务,14学习任务,-1未知任务
                    val newTaskBean = TaskDetail(classifyName = getString(R.string.new_task), classify = 1, itemStyle = true)
                    taskList.add(newTaskBean)
                    if (size > 0) taskList.add(this[0])
                    if (size > 1) taskList.add(this[1])
                }
                //新币任务
                it.data?.SPOT_TASK?.apply {
                    val newTaskBean = TaskDetail(classifyName = getString(R.string.spot_task), classify = 8, itemStyle = true)
                    taskList.add(newTaskBean)
                    if (size > 0) taskList.add(this[0])
                    if (size > 1) taskList.add(this[1])
                }
                //合约任务
                it.data?.CONTARCT_TASK?.apply {
                    val newTaskBean = TaskDetail(classifyName = getString(R.string.contract_task), classify = 11, itemStyle = true)
                    taskList.add(newTaskBean)
                    if (size > 0) taskList.add(this[0])
                    if (size > 1) taskList.add(this[1])
                }
                //学习任务
                it.data?.STUDY_TASK?.apply {
                    val newTaskBean = TaskDetail(classifyName = getString(R.string.study_task), classify = 14, itemStyle = true)
                    taskList.add(newTaskBean)
                    if (size > 0) taskList.add(this[0])
                    if (size > 1) taskList.add(this[1])
                }
            }
            taskCenterAdapter.setList(taskList)
            if (mBinding.refreshLayout.isRefreshing) {
                refreshFinish()
            }
        })
        mViewModel.mGetMyTaskBeanLiveDate.observe(this, Observer {
            if (it.code == "200") {
                showDialog()
                autoRefresh()
            } else {
                ToastUtils.showShortToast(it.message)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // autoRefresh()
        Log.i("mage", "onResume")
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

    /**
     * 列表多布局
     */
    private fun getMultiItemListAdapter(): BaseMultiItemQuickAdapter<TaskDetail, BaseViewHolder> =
            object : BaseMultiItemQuickAdapter<TaskDetail, BaseViewHolder>() {
                init {
                    addItemType(TaskDetail.itemTitle, R.layout.item_task_center_1)
                    addItemType(TaskDetail.itemContent, R.layout.item_task_center_2)
                }

                override fun convert(holder: BaseViewHolder, item: TaskDetail) {
                    when (holder.itemViewType) {
                        TaskDetail.itemTitle -> {
                            holder.itemView.apply {
                                tvTaskType.text = item.classifyName
                                when (item.classify) {
                                    //1:新手任务,8新币任务,11合约任务,14学习任务,-1未知任务
                                    1 ->
                                        tvTaskTypeIcon.background = resources.getDrawable(R.mipmap.ic_task_tyro)
                                    8 ->
                                        tvTaskTypeIcon.background = resources.getDrawable(R.mipmap.ic_task_currency)
                                    11 ->
                                        tvTaskTypeIcon.background = resources.getDrawable(R.mipmap.ic_task_contract)
                                    14 ->
                                        tvTaskTypeIcon.background = resources.getDrawable(R.mipmap.ic_task_study)
                                    else ->
                                        tvTaskTypeIcon.background = resources.getDrawable(R.mipmap.ic_task_study)
                                }
                                llTaskMore.setOnClickListener {
                                    val intent1 = Intent()
                                    intent1.putExtra("classify", item.classify)
                                    TaskCenterMoreActivity.launch(this@TaskCenterActivity, intent1)
                                }
                            }
                        }

                        TaskDetail.itemContent -> {
                            holder.itemView.apply {
                                tvTaskCenterName.text = item.name
                                tvTaskCenterDes.text = item.content

                                when (item.awardType) {
                                    1 -> {
                                        //1现货赠金2合约体验金
                                        //val bd = BigDecimal(item.taskAward?.amount.toString())
                                        tvTaskAwardAmount.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.bb_cash)}"
                                    }
                                    2 -> {
                                        tvTaskAwardAmount.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.contract_cash)}"
                                    }
                                    else -> {
                                        tvTaskAwardAmount.text = "+${PricingMethodUtil.getLargePrice(item.taskAward?.amount.toString())} ${getString(R.string.contract_cash)}"
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
                                        tvGetCenterTask.text = getString(R.string.task_get)
                                        tvGetCenterTask.isEnabled = true
                                        tvGetCenterTask.isClickable = true
                                        tvGetCenterTask.setTextColor(textWhite)
                                    }
                                    2 -> {
                                        tvGetCenterTask.text = getString(R.string.task_already)
                                        tvGetCenterTask.isEnabled = false
                                        tvGetCenterTask.isClickable = false
                                        tvGetCenterTask.setTextColor(textGray)
                                    }
                                    3 -> {
                                        tvGetCenterTask.text = getString(R.string.task_not_match)
                                        tvGetCenterTask.isEnabled = false
                                        tvGetCenterTask.isClickable = false
                                        tvGetCenterTask.setTextColor(textGray)
                                    }
                                    4 -> {
                                        tvGetCenterTask.text = getString(R.string.task_null)
                                        tvGetCenterTask.isEnabled = false
                                        tvGetCenterTask.isClickable = false
                                        tvGetCenterTask.setTextColor(textGray)
                                    }
                                }
                                tvTaskCenterDes.setOnClickListener {
                                    showTaskDESDialog(item.content)
                                }
                                tvGetCenterTask.setOnClickListener {
                                    if (isLoginOrGotoLoginActivity(this@TaskCenterActivity)) {
                                        mViewModel.getMyTask(item.id, UserInfoManager.getToken())
                                        //position = getItemPosition(item)
                                    }
                                }
                            }
                        }
                    }
                }
            }

    /**
     * 领取任务对话框
     */
    private fun showDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.decorView?.background = null;
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_my_task, null, false)
        dialog.setView(view)
        view.tvClose.setOnClickListener {
            dialog.dismiss()
        }
        view.tvCheckTask.setOnClickListener {
            MyTaskActivity.launch(this)
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * 任务描述对话框
     */
    private fun showTaskDESDialog(content: String?) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.decorView?.background = null;
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_my_task_des, null, false)
        dialog.setView(view)
        view.tvCheckTask.text = getString(R.string.des_ok)
        view.tvDesContent.text = content
        view.tvClose.setOnClickListener {
            dialog.dismiss()
        }
        view.tvCheckTask.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * 登录成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshInfo(refreshInfo: Event.refreshInfo?) {
        Log.i("mage", "refreshInfo")
        autoRefresh()
        when (skipIndex) {
            1 -> {
                MyTaskActivity.launch(this)
            }
            2 -> {
                MainActivity.self?.gotoFinance(AllFinanceFragment.TYPE_ZJ)
                startActivity(Intent(this, MainActivity::class.java))
            }
            else ->
                skipIndex = 0
        }
    }

    /**
     * 更多页面领取任务成功刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshList(refreshList: BizEvent.TaskCenterRefresh) {
        Log.i("mage", "TaskCenterRefresh")
        autoRefresh()
    }
}

