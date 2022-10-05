package huolongluo.byw.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import huolongluo.byw.R;


public class PullToRefreshListView extends ListView implements OnScrollListener {
    protected final static int SCROLLBACK_HEADER = 0;
    protected final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400;
    private final static float OFFSET_RADIO = 1.8f;

    private float mLastY = -1;
    private Scroller mScroller;
    private OnScrollListener mScrollListener;

    private PullToListViewListener mListViewListener;

    private PullToListViewHeader mHeaderView;
    private LinearLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight;
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false;

    private PullToListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;
    private int mScrollBack;
    private float mDispLastY = 0;

    public PullToRefreshListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        mHeaderView = new PullToListViewHeader(context);
        mHeaderViewContent = mHeaderView
                .findViewById(R.id.pulltolistview_header_content);
        mHeaderTimeView = mHeaderView
                .findViewById(R.id.pulltolistview_header_time);
        addHeaderView(mHeaderView, null, true);

        mFooterView = new PullToListViewFooter(context);
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public boolean removeHeaderView(View v) {
        return super.removeHeaderView(v);
    }

    public void removeHeadView() {
        if (mHeaderView != null) {
            removeHeaderView(mHeaderView);
        }

    }

    @Override
    public void setEmptyView(View emptyView) {
        android.view.ViewGroup.LayoutParams lp = new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        ((ViewGroup) getParent()).addView(emptyView, lp);
        super.setEmptyView(emptyView);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView, null, true);
        }
        super.setAdapter(adapter);
    }

    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) {
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {

            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            //mFooterView.show();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(PullToListViewFooter.STATE_NORMAL);
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(PullToListViewFooter.STATE_NORMAL);
        }
    }

    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) {
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(PullToListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(PullToListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0);
    }

    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0)
            return;
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0;
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        invalidate();
    }

    private void startLoadMore() {
        if (mEnablePullLoad && !mPullLoading && !mPullRefreshing) {
            mPullLoading = true;
            mFooterView.setState(PullToListViewFooter.STATE_LOADING);
            if (mListViewListener != null) {
                mListViewListener.onLoadMore();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDispLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mDispLastY;
                mDispLastY = ev.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                if (getLastVisiblePosition() == getCount() - 1 && (deltaY < 0)) {
                }
                break;
            default:
                mLastY = -1;
                if (getFirstVisiblePosition() == 0) {
                    if (mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(PullToListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (mEnablePullLoad && !mPullLoading
                && scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                View lastChild = this.getChildAt(view.getChildCount() - 1);
                if (lastChild != null) {
                    if (lastChild.getBottom() <= view.getBottom()) {
                        startLoadMore();
                    }
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setPullToListViewListener(PullToListViewListener listener) {
        mListViewListener = listener;
    }

    public void setScorllListViewListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    public interface PullToListViewListener {
        void onRefresh();

        void onLoadMore();
    }

}
