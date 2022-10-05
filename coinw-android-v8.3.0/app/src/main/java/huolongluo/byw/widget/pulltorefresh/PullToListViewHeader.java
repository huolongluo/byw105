package huolongluo.byw.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import huolongluo.byw.R;


public class PullToListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;
    private RotateAnimation mFlipAnimation,mReverseFlipAnimation;
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public PullToListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public PullToListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.pulltolistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = findViewById(R.id.pulltolistview_header_arrow);
		mHintTextView = findViewById(R.id.pulltolistview_header_hint_textview);
		mProgressBar = findViewById(R.id.pulltolistview_header_progressbar);
		mProgressBar.setVisibility(View.GONE);

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


	}

	public void setState(int state) {
		if (state == mState)
			return;
		switch (state) {
		case STATE_NORMAL:// 正常状态
			mArrowImageView.clearAnimation();
			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mArrowImageView.startAnimation(mReverseFlipAnimation);
			mHintTextView.setText(R.string.pull_to_refresh_pull_label);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mFlipAnimation);
				mHintTextView.setText(R.string.pull_to_refresh_release_label);
			}
			break;
		case STATE_REFRESHING:// 刷新状态
			mArrowImageView.clearAnimation();
			mHintTextView.setText(R.string.pull_to_refresh_refreshing_label);
			mArrowImageView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
