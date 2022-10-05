package huolongluo.byw.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import huolongluo.byw.R;


public class PullToRefreshView extends LinearLayout {
	// private static final String TAG = "PullToRefreshView";
	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;

	private final int ROTATE_ANIM_DURATION = 180;
	static final int FLAY_DURATION = 2000;
	/**
	 * last y
	 */
	private int mLastMotionY;

	private int mLastMotionX;
	/**
	 * lock
	 */
	// private boolean mLock;
	/**
	 * header view
	 */
	private View mHeaderView;
	/**
	 * footer view
	 */
	private View mFooterView;
	/**
	 * list or grid
	 */
	private AdapterView<?> mAdapterView;
	/**
	 * scrollview
	 */
	private ScrollView mScrollView;
	/**
	 * header view height
	 */
	private int mHeaderViewHeight;
	/**
	 * footer view height
	 */
	private int mFooterViewHeight;
	/**
	 * header view image
	 */
	private ImageView mArrowImageView;

	/**
	 * footer view image
	 */
	private ImageView mFooterImageView;
	/**
	 * header tip text
	 */
	private TextView mHeaderTextView;
	/**
	 * footer tip text
	 */
	private TextView mFooterTextView;
	/**
	 * header refresh time
	 */
	// private TextView mHeaderUpdateTextView;
	/**
	 * footer refresh time
	 */
	// private TextView mFooterUpdateTextView;
	/**
	 * header progress bar
	 */
	private ProgressBar mHeaderProgressBar;
	/**
	 * footer progress bar
	 */
	private ProgressBar mFooterProgressBar;
	/**
	 * layout inflater
	 */
	private LayoutInflater mInflater;
	/**
	 * header view current state
	 */
	private int mHeaderState;
	/**
	 * footer view current state
	 */
	private int mFooterState;
	/**
	 * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
	 */
	private int mPullState;

	private RotateAnimation mFlipAnimation;
	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	private Animation mTlateAnimation;

	private RotateAnimation mReverseFlipAnimation;
	/**
	 * footer refresh listener
	 */
	private OnFooterRefreshListener mOnFooterRefreshListener;
	/**
	 * footer refresh listener
	 */
	private OnHeaderRefreshListener mOnHeaderRefreshListener;

	/**
	 * last update time
	 */
	// private String mLastUpdateTime;

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		init();
	}

	/**
	 * init
	 * 
	 * @param
	 */
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);

		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		mTlateAnimation = new TranslateAnimation(0, dm.widthPixels, 0, 0);
		mTlateAnimation.setDuration(FLAY_DURATION);
		mTlateAnimation.setRepeatCount(Animation.INFINITE);

		mInflater = LayoutInflater.from(getContext());
		addHeaderView();
	}

	private void addHeaderView() {
		// header view
		mHeaderView = mInflater.inflate(R.layout.pulltolistview_header, this,
				false);

		mArrowImageView = mHeaderView
				.findViewById(R.id.pulltolistview_header_arrow);
		mHeaderTextView = mHeaderView
				.findViewById(R.id.pulltolistview_header_hint_textview);
		mHeaderProgressBar = mHeaderView
				.findViewById(R.id.pulltolistview_header_progressbar);
		mHeaderProgressBar.setVisibility(View.GONE);
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mHeaderViewHeight);
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView,params);

	}

	private void addFooterView() {
		// footer view
		mFooterView = mInflater.inflate(R.layout.pulltolistview_footer, this,
				false);
		mFooterImageView = mFooterView
				.findViewById(R.id.pulltolistview_footer_image);
		mFooterTextView = mFooterView
				.findViewById(R.id.pulltolistview_footer_hint_textview);
		mFooterProgressBar = mFooterView
				.findViewById(R.id.pulltolistview_footer_progressbar);
		// footer layout
		measureView(mFooterView);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mFooterViewHeight);
		addView(mFooterView, params);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		addFooterView();
		initContentAdapterView();
	}

	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 3) {
			throw new IllegalArgumentException(
					"This layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count - 1; ++i) {
			view = getChildAt(i);
//			if (view instanceof AdapterView<?>) {
//				mAdapterView = (AdapterView<?>) view;
//			}
			if (view instanceof ScrollView) {
				// finish later
				mScrollView = (ScrollView) view;
			}
		}
		if (mAdapterView == null && mScrollView == null) {
			throw new IllegalArgumentException(
					"must contain a AdapterView or ScrollView in this layout!");
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		int x = (int) e.getRawX();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 棣栧厛鎷︽埅down浜嬩欢,璁板綍y鍧愭爣
			mLastMotionY = y;
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 鏄悜涓嬭繍鍔�< 0鏄悜涓婅繍鍔�
			int deltaY = y - mLastMotionY;
			int deltaX = x - mLastMotionX;
			if (isRefreshViewScroll(deltaY)) {
				if (Math.abs(deltaY) > Math.abs(deltaX))
					return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/*
	 * 濡傛灉鍦╫nInterceptTouchEvent()鏂规硶涓病鏈夋嫤鎴�鍗硂nInterceptTouchEvent()鏂规硶涓�return
	 * false)鍒欑敱PullToRefreshView
	 * 鐨勫瓙View鏉ュ鐞�鍚﹀垯鐢变笅闈㈢殑鏂规硶鏉ュ鐞�鍗崇敱PullToRefreshView鑷繁鏉ュ鐞�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (mLock) {
		// return true;
		// }
		int y = (int) event.getRawY();
		int x = (int) event.getRawX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent
			// mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			int deltaX = x - mLastMotionX;
			if (mPullState == PULL_DOWN_STATE) {//
				if (mOnHeaderRefreshListener != null)
					headerPrepareToRefresh(deltaY);
				// setHeaderPadding(-mHeaderViewHeight);
			} else if (mPullState == PULL_UP_STATE) {//
				if (mOnFooterRefreshListener != null)
					footerPrepareToRefresh(deltaY);
			}
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();
			if (mPullState == PULL_DOWN_STATE) {
				if (topMargin >= 0) {
					if (mOnHeaderRefreshListener != null)
						headerRefreshing();
				} else {
					// 
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			} else if (mPullState == PULL_UP_STATE) {
				if (Math.abs(topMargin) >= mHeaderViewHeight
						+ mFooterViewHeight) {
					//
					if (mOnFooterRefreshListener != null)
						footerRefreshing();
				} else {
					// 
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
			return false;
		}
		if (mScrollView != null) {
			View child = mScrollView.getChildAt(0);
			if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				mPullState = PULL_DOWN_STATE;
//				DLog.d("Pull_down_state ,deltaY :"+deltaY );
				return true;
			} else if (deltaY < 0
					&& child.getMeasuredHeight() <= getHeight()
					+ mScrollView.getScrollY()) {
				mPullState = PULL_UP_STATE;
				return true;
			}
		}
		if (mAdapterView != null) {
			if (deltaY > 0) {

				View child = mAdapterView.getChildAt(0);
				if (child == null) {
					return false;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= 8) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			} else if (deltaY < 0) {
				View lastChild = mAdapterView.getChildAt(mAdapterView
						.getChildCount() - 1);
				if (lastChild == null) {
					return false;
				}
				if (lastChild.getBottom() <= getHeight()
						&& mAdapterView.getLastVisiblePosition() == mAdapterView
								.getCount() - 1) {
					mPullState = PULL_UP_STATE;
					return true;
				}
			}
		}

		return false;
	}

	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
			mHeaderProgressBar.setVisibility(View.GONE);
			mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
			// mHeaderUpdateTextView.setVisibility(View.VISIBLE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mArrowImageView.startAnimation(mFlipAnimation);

			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {
			mHeaderProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mArrowImageView.startAnimation(mReverseFlipAnimation);
			mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
				&& mFooterState != RELEASE_TO_REFRESH) {
			mFooterTextView.setText(R.string.pull_to_refresh_load_release);
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mReverseFlipAnimation);
			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mReverseFlipAnimation);
			mFooterTextView.setText(R.string.pull_to_refresh_load_normal);
			mFooterState = PULL_TO_REFRESH;
		}
	}

	private int changingHeaderViewTopMargin(int deltaY) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.8f;
		if (deltaY > 0 && mPullState == PULL_UP_STATE
				&& Math.abs(params.topMargin) <= mHeaderViewHeight) {
			return params.topMargin;
		}
		if (deltaY < 0 && mPullState == PULL_DOWN_STATE
				&& Math.abs(params.topMargin) >= mHeaderViewHeight) {
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
		return params.topMargin;
	}

	/**
	 * header refreshing
	 * 
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.GONE);
		mHeaderProgressBar.setVisibility(View.VISIBLE);
		mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}

	/**
	 * footer refreshing
	 * 
	 */
	private void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mFooterImageView.setVisibility(View.GONE);
		mFooterImageView.clearAnimation();
		mFooterImageView.setImageDrawable(null);
		mFooterProgressBar.setVisibility(View.VISIBLE);
		mFooterTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onFooterRefresh(this);
		}
	}

	private void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}

	public void onHeaderRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
		mHeaderProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mHeaderState = PULL_TO_REFRESH;
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		setLastUpdated(lastUpdated);
		onHeaderRefreshComplete();
	}

	public void onFooterRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mFooterImageView.setVisibility(View.VISIBLE);
		mFooterTextView.setText(R.string.pull_to_refresh_pull_label);
		mFooterProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mFooterState = PULL_TO_REFRESH;
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {
		// if (lastUpdated != null) {
		// mHeaderUpdateTextView.setVisibility(View.VISIBLE);
		// mHeaderUpdateTextView.setText(lastUpdated);
		// } else {
		// mHeaderUpdateTextView.setVisibility(View.GONE);
		// }
	}

	private int getHeaderTopMargin() {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}

	// /**
	// * lock
	// *
	// */
	// private void lock() {
	// mLock = true;
	// }
	//
	// /**
	// * unlock
	// *
	// */
	// private void unlock() {
	// mLock = false;
	// }

	/**
	 * set headerRefreshListener
	 * 
	 * @param headerRefreshListener
	 */
	public void setOnHeaderRefreshListener(
			OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(
			OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer
	 * view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		void onFooterRefresh(PullToRefreshView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		void onHeaderRefresh(PullToRefreshView view);
	}
}
