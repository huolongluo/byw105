package huolongluo.byw.widget.pulltorefresh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import huolongluo.byw.R;

/**
 * @author JUNJ
 * @Title: LoadView.java
 */
public class LoadingView extends LinearLayout implements LoadingViewImpl {

    private static final int DEFAULT_DELAYED_TIME = 1000;
    /**
     * 加载中
     */
    private static final int STATE_LOADING = 1;
    /**
     * 加载错误
     */
    private static final int STATE_FINISH_ERROR = 4;
    /**
     * 加载成功
     */
    private static final int STATE_FINISH_NORMAL = 2;
    /**
     * 加载成功，内容为空
     */
    private static final int STATE_FINISH_EMPTY = 3;
    /**
     * market加载成功，内容为空
     */
    private static final int STATE_FINISH_EMPTY1 = 5;
    private int mState = -1;
    private View mLoadView = null;
    private View mTagetView = null;
    private LinearLayout mProgressCompant = null;//正在加载视图
    private LinearLayout mImageDesCompant = null;// 显示图片视图
    private LinearLayout mCustomMarketLoad = null;
    private onRetryListener mRetryListener = null;// 重试监听
    private String mEmpty_btn_text = null, mEmpty_text = null;
    private OnClickListener mBtn_empty_listener = null;
    private Handler handler = null;

    public LoadingView(Context context, View targetView) {
        super(context);
        // TODO Auto-generated constructor stub
        mTagetView = targetView;
        applyParams();
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView();
        applyParams();
    }

    private void initView() {
        mLoadView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_load_view, null);
        mProgressCompant = mLoadView
                .findViewById(R.id.ll_load_progress);
        mImageDesCompant = mLoadView
                .findViewById(R.id.ll_load_imgwithdes);
        mCustomMarketLoad = mLoadView.findViewById(R.id.ll_load_custom);
        mLoadView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mState == STATE_FINISH_ERROR) {
                    if (mRetryListener != null)
                        mRetryListener.onRetry();
                }
            }
        });
        this.addView(mLoadView);
        this.setGravity(Gravity.CENTER);
    }

    /**
     * 设置内容为空的时候显示按钮
     */
    public void setEmptyBtnEvent(String text, OnClickListener listener) {
        mEmpty_btn_text = text;
        mBtn_empty_listener = listener;
    }

    /**
     * 自定义内容空的时候显示文字
     */
    public void setMyEmptyDescription(String text) {
        mEmpty_text = text;
    }

    private void applyParams() {
        ViewGroup.LayoutParams lp = mTagetView.getLayoutParams();
        ViewParent parent = mTagetView.getParent();
        ViewGroup group = (ViewGroup) parent;
        FrameLayout container = new FrameLayout(getContext());
        int index = group.indexOfChild(mTagetView);
        group.removeView(mTagetView);
        if (lp instanceof LinearLayout.LayoutParams) {
            group.addView(container, index, lp);
        } else {
            group.addView(container, index, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }
        container.addView(mTagetView, new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setVisibility(View.VISIBLE);
        container.addView(this);
        group.invalidate();
    }

    public void setOnRetryListener(onRetryListener listener) {
        mRetryListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see sy.vspk.pkwan.view.LoadViewImpl#startLoad()
     */
    @Override
    public void startLoad() {
        // TODO Auto-generated method stub
        mState = STATE_LOADING;
        dealWithStateChange();
    }

    /*
     * (non-Javadoc)
     *
     * @see sy.vspk.pkwan.view.LoadViewImpl#stopLoadByError()
     */
    @Override
    public void stopLoadByError() {
        // TODO Auto-generated method stub
        mState = STATE_FINISH_ERROR;
        dealWithStateChange();
    }

    /*
     * (non-Javadoc)
     *
     * @see sy.vspk.pkwan.view.LoadViewImpl#stopLoadByEmpty()
     */
    @Override
    public void stopLoadByEmpty() {
        // TODO Auto-generated method stub
        mState = STATE_FINISH_EMPTY;
        dealWithStateChange();
    }

    @Override
    public void stopLoadByEmpty1() {
        mState = STATE_FINISH_EMPTY1;
        dealWithStateChange();
    }

    /*
     * (non-Javadoc)
     *
     * @see sy.vspk.pkwan.view.LoadViewImpl#stopLoad()
     */
    @Override
    public void stopLoad() {
        // TODO Auto-generated method stub
        mState = STATE_FINISH_NORMAL;
        dealWithStateChange();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDetachedFromWindow();
    }

    private void dealWithStateChange() {
        switch (mState) {
            case STATE_LOADING:
                mImageDesCompant.setVisibility(View.GONE);
                mProgressCompant.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_FINISH_EMPTY:
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mProgressCompant.setVisibility(View.GONE);
                        mImageDesCompant.setVisibility(View.VISIBLE);
                        setLoadEmptyView();
                        setVisibility(View.VISIBLE);
                    }
                }, DEFAULT_DELAYED_TIME);
                break;
            case STATE_FINISH_EMPTY1:
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mProgressCompant.setVisibility(View.GONE);
                        mImageDesCompant.setVisibility(View.GONE);
                        mCustomMarketLoad.setVisibility(View.VISIBLE);
                        setLoadEmptyView();
                        setVisibility(View.VISIBLE);
                    }
                }, DEFAULT_DELAYED_TIME);
                break;
            case STATE_FINISH_ERROR:
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mProgressCompant.setVisibility(View.GONE);
                        mImageDesCompant.setVisibility(View.VISIBLE);
                        setLoadErrorView();
                        setVisibility(View.VISIBLE);
                    }
                }, DEFAULT_DELAYED_TIME);
                break;
            case STATE_FINISH_NORMAL:
                mProgressCompant.setVisibility(View.GONE);
                mImageDesCompant.setVisibility(View.GONE);
                this.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mLoadView.invalidate();
    }

    /**
     * 显示加载失败View
     */
    private void setLoadErrorView() {
        if (mImageDesCompant != null) {
            ImageView iv = mImageDesCompant
                    .findViewById(R.id.iv_load_img);
            TextView tv_description = mImageDesCompant
                    .findViewById(R.id.iv_load_description);
            iv.setImageResource(R.drawable.ic_load_error);
            tv_description.setText("请再次尝试");
            Button btn = mImageDesCompant
                    .findViewById(R.id.btn_load_even);
            btn.setVisibility(View.GONE);
        }
    }

    /**
     * 显示内容为空View
     */
    private void setLoadEmptyView() {
        ImageView iv = mImageDesCompant
                .findViewById(R.id.iv_load_img);
        ImageView imageView = mCustomMarketLoad.findViewById(R.id.add_custom);
        TextView tv_description = mImageDesCompant
                .findViewById(R.id.iv_load_description);
        iv.setImageResource(R.drawable.ic_load_empty);
        imageView.setImageResource(R.drawable.icon_emptystate_addcustom2x);
        if (mEmpty_text == null)
            tv_description.setText("暂无数据");
        else
            tv_description.setText(mEmpty_text);
        Button btn = mImageDesCompant.findViewById(R.id.btn_load_even);
        if (mEmpty_btn_text != null && mBtn_empty_listener != null) {
            btn.setVisibility(View.VISIBLE);
            btn.setText(mEmpty_btn_text);
            btn.setOnClickListener(mBtn_empty_listener);
        } else {
            btn.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(mBtn_empty_listener);
    }

    public interface onRetryListener {

        void onRetry();
    }
}
