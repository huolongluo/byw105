package com.android.legend.base;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
/**
 * Created by guoziwei on 2017/12/16.
 */

public abstract class ListActivity<T> extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    protected int mPage = 1;
    protected int mTotalPages=1000;//总的page数，未设置默认较大值
    protected RecyclerView mRvList;
    protected SwipeRefreshLayout mRefreshLayout;
    protected View mLoadView;
    protected BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    protected View mErrorPanel;
    private View mProgressBar;
    protected LinearLayout mRootList;
    protected FrameLayout mFlContainer;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.swipe_refresh);
        mRootList = findViewById(R.id.root_list);
        mRvList = findViewById(R.id.rv_list);
        mFlContainer = findViewById(R.id.fl_container);
        int color = ContextCompat.getColor(this,R.color.FF212227);
        mRefreshLayout.setColorSchemeColors(color);

        setManager();
        mLoadView = findViewById(R.id.load_view);
        mErrorPanel = mLoadView.findViewById(R.id.errorPanel);
        mProgressBar = mLoadView.findViewById(R.id.progress_bar);
        mErrorPanel.findViewById(R.id.btnError).setOnClickListener(v1 -> {
            mLoadView.setVisibility(View.VISIBLE);
            mErrorPanel.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            loadData();
        });
        mAdapter = initAdapter();
        if (mAdapter.getLoadMoreModule().isEnableLoadMore()) {
            mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        }
        mRvList.setAdapter(mAdapter);
        View emptyView = getEmptyView(LayoutInflater.from(this));
        if (emptyView != null) {
            mAdapter.setEmptyView(emptyView);
        } else {
            mAdapter.setEmptyView(getEmptyViewResId());
        }
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadData();
    }

    public abstract BaseQuickAdapter<T, BaseViewHolder> initAdapter();

    protected abstract void loadData();

    /**
     * 子类加载成功调用
     */
    protected void loadDataSuccess(List<T> list, boolean hasNextPage) {
        mLoadView.setVisibility(View.GONE);
        mRvList.setVisibility(View.VISIBLE);
        mRefreshLayout.setRefreshing(false);
        if (mPage == 1) {
            mAdapter.setList(list);
        } else {
            mAdapter.addData(list);
        }
        mAdapter.getLoadMoreModule().loadMoreComplete();
        if (!hasNextPage) {
            mAdapter.getLoadMoreModule().loadMoreEnd(true);
        }
    }

    protected void loadDataSuccess(List<T> list) {
        boolean noMore = mAdapter.getLoadMoreModule().isEnableLoadMore()
                && (list == null || list.size() < getPageCount()||mPage>=mTotalPages);
        loadDataSuccess(list, !noMore);
    }

    /**
     * 子类加载失败调用
     */
    protected void loadFailed() {
        loadFailed(mAdapter.getData().size() == 0);
    }

    protected void loadFailed(boolean showErrorView) {
        if (showErrorView && mPage == 1) {
            mLoadView.setVisibility(View.VISIBLE);
            mErrorPanel.setVisibility(View.VISIBLE);
            mRvList.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);

        mRefreshLayout.setRefreshing(false);
        if (mAdapter.getLoadMoreModule().isEnableLoadMore()) {
            if (mPage != 1) {
                mAdapter.getLoadMoreModule().loadMoreFail();
                mPage--; // 还原mPage
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void setManager() {
        mRvList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    protected int getPageCount() {
        return AppConstants.UI.DEFAULT_PAGE_SIZE;
    }

    protected void addFixHeaderView(View view) {
        mRootList.addView(view, 0);
    }

    protected void addFixFooterView(View view) {
        mRootList.addView(view);
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadData();
    }

    @Override
    public void onLoadMore() {
        mPage++;
        loadData();
    }


    protected int getEmptyViewResId() {
        return R.layout.view_empty;
    }

    protected View getEmptyView(LayoutInflater inflater) {
        return null;
    }
}