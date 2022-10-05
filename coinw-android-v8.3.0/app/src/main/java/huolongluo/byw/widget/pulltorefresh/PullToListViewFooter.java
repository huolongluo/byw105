package huolongluo.byw.widget.pulltorefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import huolongluo.byw.R;


public class PullToListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	private ImageView mImageView;

	public PullToListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public PullToListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		mHintView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
		if (state == STATE_READY) {
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText(R.string.pull_to_refresh_load_release);
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.pull_to_refresh_load_ing);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText(R.string.pull_to_refresh_load_normal);
		}
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		return lp.bottomMargin;
	}

	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	public void loading() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	public void hide() {
		// LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
		// mContentView
		// .getLayoutParams();
		// lp.height = 0;
		// mContentView.setLayoutParams(lp);
		mContentView.setVisibility(View.GONE);
	}

	public void show() {
		// LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
		// mContentView
		// .getLayoutParams();
		// lp.height = LayoutParams.WRAP_CONTENT;
		// mContentView.setLayoutParams(lp);
		mContentView.setVisibility(View.VISIBLE);
	}

	public void setText(CharSequence cs){
		mHintView.setText(cs);
	}
	
	public boolean isLoading(){
		return mProgressBar.getVisibility() == VISIBLE;
	}
	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.pulltolistview_footer, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(moreView, lp);
		mContentView = moreView
				.findViewById(R.id.pulltolistview_footer_content);
		mProgressBar = moreView
				.findViewById(R.id.pulltolistview_footer_progressbar);
		mImageView = moreView
				.findViewById(R.id.pulltolistview_footer_image);
		mHintView = moreView
				.findViewById(R.id.pulltolistview_footer_hint_textview);
	}

}
