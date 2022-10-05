package com.android.coinw.test.log

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.android.legend.base.ListActivity
import com.android.legend.extension.appViewModels
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import huolongluo.byw.R

class HttpLogActivity : ListActivity<BaseNode>() {

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, HttpLogActivity::class.java))
        }
    }

    private val viewModel by appViewModels<HttpLogViewModel>()

    override fun initView() {
        super.initView()
        mRefreshLayout.isEnabled = false
    }

    override fun initObserve() {
        viewModel.logData.observe(this, Observer {
            val nodeList = it.map { item ->
                val index = item.indexOf('\n')
                TitleNode(
                    item.substring(0, index),
                    item.contains("请求成功"),
                    mutableListOf(ContentNode(item.substring(index + 1)))
                ).apply { isExpanded = false }
            }
            loadDataSuccess(nodeList)
        })
        viewModel.updateLogData.observe(this, Observer {
            val index = it.indexOf('\n')
            val node = TitleNode(
                it.substring(0, index),
                it.contains("请求成功"),
                mutableListOf(ContentNode(it.substring(index + 1)))
            ).apply { isExpanded = false }
            mAdapter.addData(0, node)
        })
    }

    override fun initAdapter() = NodeAdapter()


    override fun loadData() {
        viewModel.loadData()
    }

    override fun initAnimationTitle() = "Http log"

    class NodeAdapter : BaseNodeAdapter(), LoadMoreModule {
        init {
            addFullSpanNodeProvider(TitleNodeProvider())
            addFullSpanNodeProvider(ContentNodeProvider())
        }

        override fun getItemType(data: List<BaseNode>, position: Int): Int {
            val node = data[position]
            return if (node is TitleNode) {
                0
            } else {
                1
            }
        }
    }

    class TitleNode(
        val title: String,
        val isSuccess: Boolean,
        override val childNode: MutableList<BaseNode>?
    ) :
        BaseExpandNode()

    class ContentNode(val content: String) : BaseNode() {
        override val childNode: MutableList<BaseNode>?
            get() = null
    }

    class TitleNodeProvider : BaseNodeProvider() {
        override val itemViewType: Int
            get() = 0
        override val layoutId: Int
            get() = R.layout.item_http_log_title

        override fun convert(helper: BaseViewHolder, item: BaseNode) {
            val titleNode = item as TitleNode
            val color = ContextCompat.getColor(context, if(titleNode.isSuccess) R.color.green else R.color.red)
            helper.setText(R.id.tvTitle, titleNode.title)
                .setTextColor(R.id.tvTitle, color)
        }

        override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
            super.onClick(helper, view, data, position)
            getAdapter()?.expandOrCollapse(position)
        }
    }

    class ContentNodeProvider : BaseNodeProvider() {
        override val itemViewType: Int
            get() = 1
        override val layoutId: Int
            get() = android.R.layout.activity_list_item

        override fun convert(helper: BaseViewHolder, item: BaseNode) {
            helper.setText(android.R.id.text1, (item as ContentNode).content)
        }
    }
}