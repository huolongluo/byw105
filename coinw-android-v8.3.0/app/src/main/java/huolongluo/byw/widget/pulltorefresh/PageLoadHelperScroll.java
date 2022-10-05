package huolongluo.byw.widget.pulltorefresh;

import android.content.Context;

import huolongluo.byw.byw.base.BaseActivity;


public class PageLoadHelperScroll {

    private int mPage = 1;

    private BaseActivity.LoadType mLoatType = BaseActivity.LoadType.FRISTLOAD;

    private LoadingView mLoadingView = null;

    private PullToRefreshListViewScroll mLV = null;

    private PullToRefreshView mRefreshView = null;

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    private Context mCxt = null;

    private boolean hasNext = false;

    private int pageLimit = 10;

    private PullToRefreshView.OnFooterRefreshListener mFooterListener = null;

    public PageLoadHelperScroll(Context context, PullToRefreshListViewScroll listView,
                                LoadingView loadingView) {
        mCxt = context;
        mLoadingView = loadingView;
        mLV = listView;
    }

    public PageLoadHelperScroll(Context context, PullToRefreshView refreshView,
                                LoadingView loadingView,
                                PullToRefreshView.OnFooterRefreshListener footerRefreshListener) {
        mCxt = context;
        mLoadingView = loadingView;
        mRefreshView = refreshView;
        mFooterListener = footerRefreshListener;
    }

    public void setView(PullToRefreshListViewScroll listview, LoadingView loadingView) {
        mLoadingView = loadingView;
        mLV = listview;
    }

    public void setView(PullToRefreshView refreshView, LoadingView loadingView) {
        mLoadingView = loadingView;
        mRefreshView = refreshView;
    }

    public int getPage() {
        return mPage;
    }

    public void firstLoad() {
        mPage = 1;
        mLoatType = BaseActivity.LoadType.FRISTLOAD;
        if (mLV != null) {
            mLV.setPullLoadEnable(false);
        }
        if (mRefreshView != null) {
            mRefreshView.setOnFooterRefreshListener(null);
        }
    }

    public void refersh() {
        mPage = 1;
        mLoatType = BaseActivity.LoadType.REFRESH;
    }

    public void loadMore() {
        mPage++;
        mLoatType = BaseActivity.LoadType.LOADMORE;
    }

    public void onStart() {
        if (mLoatType == BaseActivity.LoadType.FRISTLOAD)
            mLoadingView.startLoad();
    }

    public void onError(String msg) {
        if (mLoatType == BaseActivity.LoadType.FRISTLOAD)
            mLoadingView.stopLoadByError();
        else if (mLoatType == BaseActivity.LoadType.REFRESH) {
            if (mLV != null)
                mLV.stopRefresh();
            if (mRefreshView != null)
                mRefreshView.onHeaderRefreshComplete();
            //	ShowMessage.toastMsg(mCxt, msg);
        } else if (mLoatType == BaseActivity.LoadType.LOADMORE) {
            if (mPage > 1)
                mPage--;
            if (mLV != null)
                mLV.stopLoadMore();
            if (mRefreshView != null)
                mRefreshView.onFooterRefreshComplete();
        }
//		ShowMessage.toastMsg(mCxt, msg);

    }

    public void onComplete() {
        mLoadingView.stopLoad();
        if (mLV != null) {
            mLV.stopRefresh();
            mLV.stopLoadMore();
        }
        if (mRefreshView != null) {
            mRefreshView.onHeaderRefreshComplete();
            mRefreshView.onFooterRefreshComplete();
        }
    }

    public void onEmpty() {
        if (mLoatType == BaseActivity.LoadType.FRISTLOAD)
            mLoadingView.stopLoadByEmpty();
        else if (mLoatType == BaseActivity.LoadType.REFRESH) {
            if (mLV != null)
                mLV.stopRefresh();
            if (mRefreshView != null)
                mRefreshView.onHeaderRefreshComplete();
            mLoadingView.stopLoadByEmpty();
        } else if (mLoatType == BaseActivity.LoadType.LOADMORE) {
            if (mPage > 1)
                mPage--;
            if (mLV != null)
                mLV.stopLoadMore();
            if (mRefreshView != null)
                mRefreshView.onFooterRefreshComplete();
            setLoadMore(0);
        }
    }

    //CustomMarket界面加载
    public void onEmpty1() {
        if (mLoatType == BaseActivity.LoadType.FRISTLOAD)
            mLoadingView.stopLoadByEmpty1();
        else if (mLoatType == BaseActivity.LoadType.REFRESH) {
            if (mLV != null)
                mLV.stopRefresh();
            if (mRefreshView != null)
                mRefreshView.onHeaderRefreshComplete();
            mLoadingView.stopLoadByEmpty1();
        } else if (mLoatType == BaseActivity.LoadType.LOADMORE) {
            if (mPage > 1)
                mPage--;
            if (mLV != null)
                mLV.stopLoadMore();
            if (mRefreshView != null)
                mRefreshView.onFooterRefreshComplete();
            setLoadMore(0);
        }
    }

    public void setLoadMore(int size) {
        hasNext = size >= pageLimit;
        if (mLV != null)
            mLV.setPullLoadEnable(hasNext);
        if (mRefreshView != null) {
            if (hasNext)
                mRefreshView.setOnFooterRefreshListener(mFooterListener);
            else
                mRefreshView.setOnFooterRefreshListener(null);
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    public BaseActivity.LoadType getLoadType() {
        return mLoatType;
    }
}
