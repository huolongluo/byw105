package com.android.legend.ui.transfer

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.legend.base.BaseAdapter
import com.android.legend.base.ListActivity
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.model.enumerate.transfer.TransferAccountPosition
import com.android.legend.model.enumerate.transfer.TransferRecordStatus
import com.android.legend.model.transfer.TransferRecordBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.log.Logger
import huolongluo.byw.util.Constant
import huolongluo.byw.util.DateUtils
import huolongluo.byw.util.GsonUtil
import huolongluo.bywx.utils.DataUtils
import kotlinx.android.synthetic.main.activity_account_transfer_record.view.*
import kotlinx.android.synthetic.main.item_account_transfer_record.view.*
import kotlinx.android.synthetic.main.item_account_transfer_record_pop.view.*
import kotlinx.android.synthetic.main.pop_account_transfer_record_filter.view.*

/**
 * 账户划转记录页面
 */
class AccountTransferRecordActivity : ListActivity<TransferRecordBean>() {
    private val viewModel: AccountTransferRecordViewModel by viewModels()
    val instance by lazy { this }

    private var srcAccount: String? = null//划转的源账户 出入账都不传查全部
    private var targetAccount: String? = null //划转的目标账户
    private var filterPopupWindow: PopupWindow? = null
    private var currentFilterPosition = TransferAccountPosition.ALL.position//默认全部，用于记录当前筛选的位置，切换时相同不做操作
    private lateinit var headerView: View

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AccountTransferRecordActivity::class.java))
        }
    }

    override fun initView() {
        super.initView()
        headerView = LayoutInflater.from(this).inflate(R.layout.activity_account_transfer_record, mRootList, false)
        headerView.toolbar.setNavigationOnClickListener { finish() }
        headerView.llFilter.setOnClickListener { showFilterPopupWindow(headerView.llFilter) }
        addFixHeaderView(headerView)
    }

    override fun initObserve() {
        viewModel.recordList.observe(this, Observer {
            Logger.getInstance().debug(TAG, "viewModel.recordList.observe $it")
            if (it.isSuccess) {
                it.data?.bizVo?.let {
                    mTotalPages = it.totalPages
                    loadDataSuccess(it.result)
                }
            } else {
                loadFailed(true)
            }
        })
    }

    override fun initAdapter(): BaseQuickAdapter<TransferRecordBean, BaseViewHolder> =
            object : BaseAdapter<TransferRecordBean>(R.layout.item_account_transfer_record) {
                init {
                    loadMoreModule.isEnableLoadMore = true
                }

                override fun convert(helper: BaseViewHolder, item: TransferRecordBean) {
                    helper.itemView.run {
                        Logger.getInstance().debug(TAG, "initAdapter convert item:" + GsonUtil.obj2Json(item, TransferRecordBean::class.java))
                        tvAccountSide.text = getAccountText(getPositionByAccount(item.fromAccount, item.targetAccount))
                        tvTime.text = DateUtils.format(item.transferTime, DateUtils.FORMAT1)
                        tvAmount.text = "${item.amount} ${item.coinName}"
                        tvStatus.text = TransferRecordStatus.SUCCESS.getText(instance, item.status)
                    }
                }

            }

    override fun loadData() {
        viewModel.getTransferRecord(srcAccount, targetAccount, mPage)
    }

    private fun showFilterPopupWindow(view: View) {
        if (filterPopupWindow == null) {
            val contentView = LayoutInflater.from(this).inflate(R.layout.pop_account_transfer_record_filter, null, false)
            contentView.tvCancel.setOnClickListener { filterPopupWindow?.dismiss() }
            initPopView(contentView.rcvFilter)
            filterPopupWindow = PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true)
            filterPopupWindow?.setBackgroundDrawable(ColorDrawable(0x7f000000))
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            filterPopupWindow?.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        } else {
            filterPopupWindow?.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        }
    }

    private fun initPopView(rcvFilter: RecyclerView) {
        rcvFilter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = initPopAdapter()
        var list = arrayListOf<String>()
        for (position in TransferAccountPosition.values().indices) {
            if (Constant.IS_BDB_CLOSE && (position == TransferAccountPosition.BDB_TO_WEALTH.position || position == TransferAccountPosition.WEALTH_TO_BDB.position)) {
                continue
            }
            if (!DataUtils.isOpenHeader()) {
                if (position == TransferAccountPosition.WEALTH_TO_OTC.position || position == TransferAccountPosition.OTC_TO_WEALTH.position) {
                    continue
                } else if (position == TransferAccountPosition.SPOT_TO_OTC.position || position == TransferAccountPosition.OTC_TO_SPOT.position) {
                    continue
                }
            }
            list.add(getAccountText(position))
        }
        adapter.setNewData(list)
        adapter.setOnItemClickListener { adapter, view, position -> filter(position, adapter.getItem(position) as String) }
        rcvFilter.adapter = adapter
    }

    private fun initPopAdapter(): BaseQuickAdapter<String, BaseViewHolder> =
            object : BaseAdapter<String>(R.layout.item_account_transfer_record_pop) {
                init {
                    loadMoreModule.isEnableLoadMore = true
                }

                override fun convert(helper: BaseViewHolder, item: String) {
                    helper.itemView.run {
                        tvName.text = item
                        if (helper.adapterPosition == TransferAccountPosition.ALL.position) {//全部
                            tvName.setTextColor(ContextCompat.getColor(instance, R.color.accent_main))
                        } else {
                            tvName.setTextColor(ContextCompat.getColor(instance, R.color.text_main2))
                        }
                    }
                }

            }

    private fun getAccountText(position: Int): String {
        var text = ""
        when (position) {
            TransferAccountPosition.ALL.position -> text = getString(R.string.qa15)
            TransferAccountPosition.WEALTH_TO_SPOT.position -> text = getString(R.string.wealth_to_spot)
            TransferAccountPosition.SPOT_TO_WEALTH.position -> text = getString(R.string.spot_to_wealth)
            TransferAccountPosition.WEALTH_TO_OTC.position -> text = getString(R.string.wealth_to_fb)
            TransferAccountPosition.OTC_TO_WEALTH.position -> text = getString(R.string.fb_to_wealth)
            TransferAccountPosition.WEALTH_TO_CONTRACT.position -> text = getString(R.string.wealth_to_contract)
            TransferAccountPosition.CONTRACT_TO_WEALTH.position -> text = getString(R.string.contract_to_wealth)
            TransferAccountPosition.WEALTH_TO_BDB.position -> text = getString(R.string.wealth_to_bdb)
            TransferAccountPosition.BDB_TO_WEALTH.position -> text = getString(R.string.bdb_to_wealth)
            TransferAccountPosition.SPOT_TO_OTC.position -> text = getString(R.string.spot_to_fb)
            TransferAccountPosition.OTC_TO_SPOT.position -> text = getString(R.string.fb_to_spot)
            TransferAccountPosition.WEALTH_TO_EARN.position -> text = getString(R.string.wealth_to_earn)
            TransferAccountPosition.EARN_TO_WEALTH.position -> text = getString(R.string.earn_to_wealth)
        }
        return text
    }

    private fun getPositionByAccount(srcAccount: String, targetAccount: String): Int {
        var position = -1
        when {
            srcAccount == TransferAccount.WEALTH.value -> {
                when (targetAccount) {
                    TransferAccount.SPOT.value -> position = TransferAccountPosition.WEALTH_TO_SPOT.position
                    TransferAccount.OTC.value -> position = TransferAccountPosition.WEALTH_TO_OTC.position
                    TransferAccount.CONTRACT.value -> position = TransferAccountPosition.WEALTH_TO_CONTRACT.position
                    TransferAccount.BDB.value -> position = TransferAccountPosition.WEALTH_TO_BDB.position
                    TransferAccount.EARN.value -> position = TransferAccountPosition.WEALTH_TO_EARN.position
                }
            }
            targetAccount == TransferAccount.WEALTH.value -> {
                when (srcAccount) {
                    TransferAccount.SPOT.value -> position = TransferAccountPosition.SPOT_TO_WEALTH.position
                    TransferAccount.OTC.value -> position = TransferAccountPosition.OTC_TO_WEALTH.position
                    TransferAccount.CONTRACT.value -> position = TransferAccountPosition.CONTRACT_TO_WEALTH.position
                    TransferAccount.BDB.value -> position = TransferAccountPosition.BDB_TO_WEALTH.position
                    TransferAccount.EARN.value -> position = TransferAccountPosition.EARN_TO_WEALTH.position
                }
            }
            srcAccount == TransferAccount.SPOT.value && targetAccount == TransferAccount.OTC.value -> position = TransferAccountPosition.SPOT_TO_OTC.position
            srcAccount == TransferAccount.OTC.value && targetAccount == TransferAccount.SPOT.value -> position = TransferAccountPosition.OTC_TO_SPOT.position
        }
        return position
    }

    private fun filter(position: Int, accountSide: String) {
        filterPopupWindow?.dismiss()
        if (position == currentFilterPosition) {
            return
        }
        when (position) {
            TransferAccountPosition.ALL.position -> {
                srcAccount = null
                targetAccount = null
            }
            TransferAccountPosition.WEALTH_TO_SPOT.position -> {
                srcAccount = TransferAccount.WEALTH.value
                targetAccount = TransferAccount.SPOT.value
            }
            TransferAccountPosition.SPOT_TO_WEALTH.position -> {
                srcAccount = TransferAccount.SPOT.value
                targetAccount = TransferAccount.WEALTH.value
            }
            TransferAccountPosition.WEALTH_TO_OTC.position -> {
                srcAccount = TransferAccount.WEALTH.value
                targetAccount = TransferAccount.OTC.value
            }
            TransferAccountPosition.OTC_TO_WEALTH.position -> {
                srcAccount = TransferAccount.OTC.value
                targetAccount = TransferAccount.WEALTH.value
            }
            TransferAccountPosition.WEALTH_TO_CONTRACT.position -> {
                srcAccount = TransferAccount.WEALTH.value
                targetAccount = TransferAccount.CONTRACT.value
            }
            TransferAccountPosition.CONTRACT_TO_WEALTH.position -> {
                srcAccount = TransferAccount.CONTRACT.value
                targetAccount = TransferAccount.WEALTH.value
            }
            TransferAccountPosition.WEALTH_TO_BDB.position -> {
                srcAccount = TransferAccount.WEALTH.value
                targetAccount = TransferAccount.BDB.value
            }
            TransferAccountPosition.BDB_TO_WEALTH.position -> {
                srcAccount = TransferAccount.BDB.value
                targetAccount = TransferAccount.WEALTH.value
            }
            TransferAccountPosition.SPOT_TO_OTC.position -> {
                srcAccount = TransferAccount.SPOT.value
                targetAccount = TransferAccount.OTC.value
            }
            TransferAccountPosition.OTC_TO_SPOT.position -> {
                srcAccount = TransferAccount.OTC.value
                targetAccount = TransferAccount.SPOT.value
            }
            TransferAccountPosition.WEALTH_TO_EARN.position -> {
                srcAccount = TransferAccount.WEALTH.value
                targetAccount = TransferAccount.EARN.value
            }
            TransferAccountPosition.EARN_TO_WEALTH.position -> {
                srcAccount = TransferAccount.EARN.value
                targetAccount = TransferAccount.WEALTH.value
            }
        }
        currentFilterPosition = position
        headerView.tvFilter.text = accountSide
        loadData()
    }
}
