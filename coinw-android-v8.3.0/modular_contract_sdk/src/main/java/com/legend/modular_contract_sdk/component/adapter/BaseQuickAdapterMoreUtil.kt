package com.legend.modular_contract_sdk.component.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.repository.model.BaseListResp
import com.legend.modular_contract_sdk.repository.model.BaseResp
import com.legend.modular_contract_sdk.repository.model.McBasePage
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.utils.McConstants
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//basequickadapter的分页
fun <M> setupWithBaseQuickAdapter(
    lifecycleOwner: LifecycleOwner,
    recyclerView: RecyclerView,
    refreshAction: suspend () -> BaseResp<McBasePage<M>>?,
    loadMoreAction: suspend (pageIndex: Int) -> BaseResp<McBasePage<M>>?
) {

    var pageIndex = 1
    val viewParent = recyclerView.parent
    var viewIndex = 0
    if (viewParent is ViewGroup) {

        viewParent.forEachIndexed { index, childView ->
            if (childView === recyclerView) {
                viewIndex = index
            }
        }

        val smartRefreshLayout = SmartRefreshLayout(recyclerView.context)

        viewParent.removeView(recyclerView)

        smartRefreshLayout.addView(
            recyclerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        recyclerView.tag = smartRefreshLayout
        viewParent.addView(
            smartRefreshLayout,
            viewIndex,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        smartRefreshLayout.setRefreshHeader(ClassicsHeader(recyclerView.context))
        smartRefreshLayout.setRefreshFooter(
            ClassicsFooter(recyclerView.context).setSpinnerStyle(
                SpinnerStyle.Scale
            )
        )
        //取消内容不满一页时是否开启上拉加载功能
//        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false)
        smartRefreshLayout.setFooterInsetStart(-10f)
        smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                lifecycleOwner.lifecycleScope.launch {
                    val adapter: BaseQuickAdapter<M, BaseViewHolder> =
                        recyclerView.adapter as BaseQuickAdapter<M, BaseViewHolder>

                    val listResp = withContext(Dispatchers.IO) {
                        loadMoreAction(++pageIndex)
                    }
                    if(listResp==null){
                        refreshLayout.finishLoadMore()
                        return@launch
                    }
                    if (listResp.isSuccess) {
                        listResp.data?.rows?.let { adapter.addData(it) }
                    }
                    smartRefreshLayout.finishLoadMore(listResp.isSuccess)

                    smartRefreshLayout.setNoMoreData(listResp.data?.total!! <=McConstants.COMMON.PER_PAGE_SIZE*(pageIndex-1)+ listResp.data?.rows?.size!!)

                }
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                lifecycleOwner.lifecycleScope.launch {
                    val adapter: BaseQuickAdapter<M, BaseViewHolder> =
                        recyclerView.adapter as BaseQuickAdapter<M, BaseViewHolder>

                    val listResp = withContext(Dispatchers.IO) {
                        refreshAction()
                    }
                    if(listResp==null){
                        refreshLayout.finishRefresh()
                        return@launch
                    }
                    if (listResp.isSuccess) {
                        adapter.setNewInstance(listResp.data?.rows as MutableList<M>?)
                    }
                    smartRefreshLayout.finishRefresh(listResp.isSuccess)
                    smartRefreshLayout.setNoMoreData(listResp.data?.total!! <=McConstants.COMMON.PER_PAGE_SIZE*(pageIndex-1)+ listResp.data?.rows?.size!!)
                }
            }

        })

    }
}