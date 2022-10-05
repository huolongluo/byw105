package com.android.legend.base;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.io.AppConstants;
/**
 * Created by guoziwei on 2017/12/16.
 */

public abstract class ListFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    protected int mPage = 1;
    protected RecyclerView mRvList;
    protected SwipeRefreshLayout mRefreshLayout;
    protected View mLoadView;
    protected BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    protected View mErrorPanel;
    protected View mProgressBar;
    protected LinearLayout mRootList;
    protected FrameLayout mFlContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        mRefreshLayout = v.findViewById(R.id.swipe_refresh);
        int color = getResources().getColor(R.color.colorAccent);
        mRefreshLayout.setColorSchemeColors(color);
        mRvList = v.findViewById(R.id.rv_list);
        mRootList = v.findViewById(R.id.root_list);
        mFlContainer = v.findViewById(R.id.fl_container);
        setManager();
        mLoadView = v.findViewById(R.id.load_view);
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
        View emptyView = getEmptyView(inflater);
        if (emptyView != null) {
            mAdapter.setEmptyView(emptyView);
        } else {
            mAdapter.setEmptyView(getEmptyViewResId());
        }
        mRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar.setVisibility(View.VISIBLE);
        loadData();

    }


    protected View getEmptyView(LayoutInflater inflater) {
        return null;
    }

    protected int getEmptyViewResId() {
        return R.layout.view_empty;
    }

    public abstract BaseQuickAdapter<T, BaseViewHolder> initAdapter();

    protected abstract void loadData();

    /**
     * 子类加载成功调用
     */
    protected void loadDataSuccess(List<T> list) {
        boolean noMore = mAdapter.getLoadMoreModule().isEnableLoadMore()
                && (list == null || list.size() < getPageCount());
        loadDataSuccess(list, !noMore);
    }

    /**
     * 子类加载成功调用
     */
    protected void loadDataSuccess(List<T> list, boolean hasMore) {
        mLoadView.setVisibility(View.GONE);
        mRvList.setVisibility(View.VISIBLE);
        mRefreshLayout.setRefreshing(false);
        if (mPage == 1) {
            mAdapter.setList(list);
        } else {
            mAdapter.addData(list);
        }
        mAdapter.getLoadMoreModule().loadMoreComplete();
        if (!hasMore) {
            mAdapter.getLoadMoreModule().loadMoreEnd(true);
        }
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
        mRvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
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


    protected void addFixHeaderView(View view) {
        mRootList.addView(view, 0);
    }

    protected void addFixFooterView(View view) {
        mRootList.addView(view);
    }

    protected int getPageCount() {
        return AppConstants.UI.DEFAULT_PAGE_SIZE;
    }
}