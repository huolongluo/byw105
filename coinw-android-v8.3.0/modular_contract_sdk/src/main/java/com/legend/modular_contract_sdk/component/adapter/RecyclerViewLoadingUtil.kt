package com.legend.modular_contract_sdk.component.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.legend.modular_contract_sdk.repository.model.McBasePage
import com.legend.modular_contract_sdk.repository.model.RespInterface
import com.legend.modular_contract_sdk.utils.McConstants
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <M, Wrap> setupWithRecyclerView(
    lifecycleOwner: LifecycleOwner,
    recyclerView: RecyclerView,
    refreshAction: suspend () -> RespInterface<McBasePage<M>>,
    loadMoreAction: suspend (pageIndex: Int) -> RespInterface<McBasePage<M>>,
    enableLoadMore:Boolean = true,
    useWrap:Boolean = false,
    emptyView: View? = null,
    wrapTranslationAction:((M) -> Wrap)? = null
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

        val recyclerViewLayoutParams = recyclerView.layoutParams

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
            recyclerViewLayoutParams
        )

        smartRefreshLayout.setRefreshHeader(ClassicsHeader(recyclerView.context))
        smartRefreshLayout.setRefreshFooter(
            ClassicsFooter(recyclerView.context).setSpinnerStyle(
                SpinnerStyle.Scale
            )
        )
        //取消内容不满一页时是否开启上拉加载功能
//      smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false)
        smartRefreshLayout.setEnableLoadMore(enableLoadMore)
        smartRefreshLayout.setFooterInsetStart(-10f)
        smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                lifecycleOwner.lifecycleScope.launch(CoroutineExceptionHandler{coroutineContext, throwable ->
                    throwable.printStackTrace()
                    smartRefreshLayout.finishLoadMore(false)
                    smartRefreshLayout.setNoMoreData(true)
                }) {
                    val adapter: DataBindingRecyclerViewAdapter<M> =
                        recyclerView.adapter as DataBindingRecyclerViewAdapter<M>

                    val listResp = withContext(Dispatchers.IO) {
                        loadMoreAction(++pageIndex)
                    }

                    if (listResp.isReqSuccess()) {
                        if (useWrap && wrapTranslationAction != null) {
                            (adapter as DataBindingRecyclerViewAdapter<Wrap>).addData(listResp.getReqData()?.rows!!.map(wrapTranslationAction))
                        } else {
                            adapter.addData(listResp.getReqData()?.rows)
                        }
                    }
                    smartRefreshLayout.finishLoadMore(listResp.isReqSuccess())

                    smartRefreshLayout.setNoMoreData(listResp.getReqData()?.total!! <= McConstants.COMMON.PER_PAGE_SIZE*(pageIndex-1) + listResp.getReqData()?.rows?.size!!)

                }
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                lifecycleOwner.lifecycleScope.launch(CoroutineExceptionHandler{coroutineContext, throwable ->
                    throwable.printStackTrace()
                    smartRefreshLayout.finishRefresh(false)
                    smartRefreshLayout.setNoMoreData(true)
                }) {
                    val adapter: DataBindingRecyclerViewAdapter<M> =
                        recyclerView.adapter as DataBindingRecyclerViewAdapter<M>

                    val listResp = withContext(Dispatchers.IO) {
                        refreshAction()
                    }

                    if (listResp.isReqSuccess()) {
                        if (useWrap && wrapTranslationAction != null) {
                            (adapter as DataBindingRecyclerViewAdapter<Wrap>).refreshData(listResp.getReqData()?.rows!!.map(wrapTranslationAction))
                        } else {
                            adapter.refreshData(listResp.getReqData()?.rows)
                        }
                        pageIndex = 1
                    }
                    smartRefreshLayout.finishRefresh(listResp.isReqSuccess())
                    smartRefreshLayout.setNoMoreData(listResp.getReqData()?.total!! <= McConstants.COMMON.PER_PAGE_SIZE*(pageIndex-1) + listResp.getReqData()?.rows?.size!!)
                }
            }

        })

    }
}