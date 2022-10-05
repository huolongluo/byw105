package huolongluo.byw.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import huolongluo.byw.R;

public class LoadingMoreFooter extends FrameLayout {

    //三种状态
    public final static int STATE_LOADING = 0;  //加载中
    public final static int STATE_COMPLETE = 1; //加载完成
    public final static int STATE_NOMORE = 2;  //没有更多了

    private TextView mText;
    //三种状态显示的文本
    private String loadingHint;
    private String noMoreHint;
    private String loadingDoneHint;

    private Context mContext;
    private ProgressBar mProgressBar;

    public LoadingMoreFooter(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void setLoadingHint(String hint) {
        loadingHint = hint;
    }

    public void setNoMoreHint(String hint) {
        noMoreHint = hint;
    }

    public void setLoadingDoneHint(String hint) {
        loadingDoneHint = hint;
    }

    public void initView() {
        //设置LayoutParams
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        //初始化布局
        View loadingView = layoutInflater.inflate(R.layout.loading_footer, this, true);
        mProgressBar = (ProgressBar) loadingView.findViewById(R.id.progress_loading);
        mText = (TextView) loadingView.findViewById(R.id.tv_hint);
        loadingHint = (String) getContext().getText(R.string.loading);
        noMoreHint = (String) getContext().getText(R.string.nomore_loading);
        loadingDoneHint = (String) getContext().getText(R.string.load_end);
    }

    //根据状态切切换显示
    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mText.setText(loadingHint);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mText.setText(loadingDoneHint);
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText(noMoreHint);
                mProgressBar.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}