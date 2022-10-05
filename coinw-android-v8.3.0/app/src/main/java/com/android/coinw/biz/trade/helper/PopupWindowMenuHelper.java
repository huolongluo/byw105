package com.android.coinw.biz.trade.helper;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.reflect.Method;

import huolongluo.byw.R;
import huolongluo.byw.util.Util;
public class PopupWindowMenuHelper {
    private static PopupWindowMenuHelper instance = null;

    public static PopupWindowMenuHelper getInstance() {
        if (instance == null) {
            instance = new PopupWindowMenuHelper();
        }
        return instance;
    }

    private PopupWindowMenuHelper() {
    }

    private PopupWindow popupWindow;

    public void hideInput(Activity atv) {
        if (atv == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) atv.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = atv.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 获取状态栏高度
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static int[] getAccurateScreenDpi(Activity atv) {
        int[] screenWH = new int[2];
        Display display = atv.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class<?> c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            screenWH[0] = dm.widthPixels;
            screenWH[1] = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWH;
    }

    /**
     * 获取底部虚拟键盘的高度
     */
    public static int getBottomKeyboardHeight(Activity context) {
        int screenHeight = getAccurateScreenDpi(context)[1];
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightDifference = screenHeight - dm.heightPixels;
        return heightDifference;
    }

    public static int getNavigationBarHeight(Context activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void openPopupMenu(Activity atv, View rootView, View.OnClickListener listener) {
        if (atv == null || rootView == null || listener == null) {
            return;
        }
        hideInput(atv);
        int height = atv.getResources().getDisplayMetrics().heightPixels;// 屏幕的高
        View contentView = LayoutInflater.from(atv).inflate(R.layout.layout_trade_pop_menu, null, false);
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, height + getStatusBarHeight(atv) + getBottomKeyboardHeight(atv), true);//设置popupWindow的高为屏幕的高+顶部状态栏的高+底部虚拟按键的高
        LinearLayout rechargeLayout = contentView.findViewById(R.id.ll_recharge);
        LinearLayout transferLayout = contentView.findViewById(R.id.ll_transfer);
        ImageView imageview1 = contentView.findViewById(R.id.imageview1);
        ImageView imageview2 = contentView.findViewById(R.id.imageview2);
        //规避方法处理多语言问题【暂时处理方法】
        TextView rechargeTxt = contentView.findViewById(R.id.tv_recharge);
        TextView transferTxt = contentView.findViewById(R.id.tv_transfer);
        rechargeTxt.setText(atv.getString(R.string.c52));
        transferTxt.setText(atv.getString(R.string.huazhuan));
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.ll_recharge:
                            v.setBackgroundResource(R.drawable.otcmenu_bg3);
                            imageview1.setImageResource(R.mipmap.hu1);
                            break;
                        case R.id.ll_transfer:
                            v.setBackgroundColor(atv.getResources().getColor(R.color.ff5e568a));
                            imageview2.setImageResource(R.mipmap.order1);
                            break;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    switch (v.getId()) {
                        case R.id.ll_recharge:
                            v.setBackgroundResource(R.drawable.otcmenu_bg1);
                            imageview1.setImageResource(R.mipmap.hz2);
                            break;
                        case R.id.ll_transfer:
                            v.setBackgroundColor(atv.getResources().getColor(R.color.ff4d447f));
                            imageview2.setImageResource(R.mipmap.order2);
                            break;
                    }
                }
                return false;
            }
        };
        contentView.findViewById(R.id.rl_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        rechargeLayout.setOnClickListener(listener);
        transferLayout.setOnClickListener(listener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setClippingEnabled(false);
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(rootView, 0, Util.dp2px(atv, 5));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
}
