package huolongluo.byw.byw.ui.activity.address;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class CustomPopupWindow implements PopupWindow.OnDismissListener {
    private static final String TAG = "CustomPopupWindow";
    private PopupWindow mPopupWindow;
    private View mContentView;
    private Context mContext;
    private Activity mActivity;
    private Builder builder;

    public CustomPopupWindow(Builder builder) {
        this.builder = builder;
        mContext = builder.context;
        mContentView = getView(builder.contentviewid);
        if (builder.width == 0 || builder.height == 0) {
            builder.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            builder.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mPopupWindow = new PopupWindow(mContentView, builder.width, builder.height, builder.fouse);
        //需要跟 setBackGroundDrawable 结合
        mPopupWindow.setOutsideTouchable(builder.outsidecancel);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setAnimationStyle(builder.animstyle);
        mActivity = builder.activity;
        mPopupWindow.setOnDismissListener(this);
    }

    private View getView(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    /**
     * popup 消失
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            if (mActivity != null) {
                initWindowManagerBg(1.0f);
            }
        }
    }

    /**
     * 设置窗口的透明颜色
     *
     * @param alpha
     */
    private void initWindowManagerBg(float alpha) {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = alpha;
        mActivity.getWindow().setAttributes(params); //恢复背景色
    }

    /**
     * 根据id获取view
     *
     * @param viewid
     * @return
     */
    public View getItemView(int viewid) {
        if (mPopupWindow != null) {
            return this.mContentView.findViewById(viewid);
        }
        return null;
    }

    /**
     * 根据父布局，显示位置
     *
     * @param rootviewid
     * @param gravity
     * @param x
     * @param y
     * @return
     */
    public CustomPopupWindow showAtLocation(int rootviewid, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            //设置背景色
            if (builder.alpha > 0 && builder.alpha < 1) {
                initWindowManagerBg(builder.alpha);
            }
            mPopupWindow.showAtLocation(getView(rootviewid), gravity, x, y);
        }
        return this;
    }

    /**
     * 根据id获取view ，并显示在该view的位置
     *
     * @param targetviewId
     * @param gravity
     * @param offx
     * @param offy
     * @return
     */
    public CustomPopupWindow showAsLaction(int targetviewId, int gravity, int offx, int offy) {
        if (mPopupWindow != null) {
            if (builder.alpha > 0 && builder.alpha < 1) {
                initWindowManagerBg(builder.alpha);
            }

            mPopupWindow.showAsDropDown(getView(targetviewId), gravity, offx, offy);
        }
        return this;
    }

    /**
     * 显示在 targetview 的不同位置
     *
     * @param targetview
     * @param gravity
     * @param offx
     * @param offy
     * @return
     */
    public CustomPopupWindow showAsLaction(View targetview, int gravity, int offx, int offy) {
        if (mPopupWindow != null) {
            if (builder.alpha > 0 && builder.alpha < 1) {
                initWindowManagerBg(builder.alpha);
            }

            mPopupWindow.showAsDropDown(targetview, gravity, offx, offy);
        }
        return this;
    }

    /**
     * 根据id设置焦点监听
     *
     * @param viewid
     * @param listener
     */
    public void setOnFocusListener(int viewid, View.OnFocusChangeListener listener) {
        getItemView(viewid).setOnFocusChangeListener(listener);
    }

    /**
     * 根据id设置点击事件监听
     *
     * @param viewid
     * @param listener
     */
    public void setOnClickListener(int viewid, View.OnClickListener listener) {
        getItemView(viewid).setOnClickListener(listener);
    }

    /**
     * 监听 dismiss，还原背景色
     */
    @Override
    public void onDismiss() {
        Log.d(TAG, "onDismiss: ");
        if (mActivity != null) {
            initWindowManagerBg(1.0f);
        }
    }

    /**
     * builder 类
     */
    public static class Builder {
        private int contentviewid;
        private int width;
        private int height;
        private boolean fouse;
        private boolean outsidecancel;
        private int animstyle;
        private Context context;
        private Activity activity;
        private float alpha;
//        public final static int STYLE1 = R.style.pop_style1;
//        public final static int STYLE2 = R.style.pop_style2;
//        public final static int STYLE3 = R.style.pop_style1;

        public Builder with(Context context) {
            this.context = context;
            return this;
        }

        public Builder setContentView(int contentviewid) {
            this.contentviewid = contentviewid;
            return this;
        }

        public Builder setwidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setheight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFouse(boolean fouse) {
            this.fouse = fouse;
            return this;
        }

        public Builder setOutSideCancel(boolean outsidecancel) {
            this.outsidecancel = outsidecancel;
            return this;
        }

        public Builder setAnimationStyle(int animstyle) {
            this.animstyle = animstyle;
            return this;
        }

        public Builder setBackGroudAlpha(Activity activity, float alpha) {
            this.activity = activity;
            this.alpha = alpha;
            return this;
        }

        public CustomPopupWindow builder() {
            return new CustomPopupWindow(this);
        }
    }
}