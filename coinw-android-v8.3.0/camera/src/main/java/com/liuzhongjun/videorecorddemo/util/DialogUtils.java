package com.liuzhongjun.videorecorddemo.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.liuzhongjun.videorecorddemo.R;
import com.liuzhongjun.videorecorddemo.activity.adapter.MyPagerAdapter;

import java.util.ArrayList;

public class DialogUtils {
    private static AlertDialog dialog;
    private static int lastPage = 0;
    private static ArrayList<View> viewList;

    public interface ShowListener {
        void OnListener();
    }

    public static void showKycDes(Context context, boolean isFirst, final ShowListener listener) {
        lastPage = 0;
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_viewpager, null);
        ViewPager viewPager = view.findViewById(R.id.rime_view_pager);
        initItemView(context, listener, isFirst);
        final LinearLayout dot_parent = view.findViewById(R.id.dot_parent);
        if (isFirst) {
            initDot(context, viewPager, dot_parent, viewList);
        }
        viewPager.setAdapter(new MyPagerAdapter(viewList));
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = dp2px(context, 260);
        params.height = dp2px(context, 245);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        Window windows = dialog.getWindow();
//        windows.setBackgroundDrawableResource(android.R.color.transparent);
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();
    }

    private static void initItemView(Context context, final ShowListener listener, boolean isFirst) {
        viewList = new ArrayList();
        if (isFirst) {
            View view1 = View.inflate(context, R.layout.dialog_viewpager_item1, null);
            View view2 = View.inflate(context, R.layout.dialog_viewpager_item2, null);
            View view3 = View.inflate(context, R.layout.dialog_viewpager_item3, null);
            View view4 = View.inflate(context, R.layout.dialog_viewpager_item4, null);
            viewList.add(view1);
            viewList.add(view2);
            viewList.add(view3);
            viewList.add(view4);
            view4.findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissDialog(dialog);
                    listener.OnListener();
                }
            });
        } else {
            View view1 = View.inflate(context, R.layout.dialog_viewpager_item5, null);
            view1.findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissDialog(dialog);
                    listener.OnListener();
                }
            });
            viewList.add(view1);
        }
    }

    private static void initDot(Context context, ViewPager viewPager, final LinearLayout dot_parent, final ArrayList<View> viewList) {
        for (int i = 0; i < viewList.size(); i++) {
            ImageView dotView = new ImageView(context);
            if (i == 0) {
                dotView.setImageResource(R.drawable.dot_white_);
            } else {
                dotView.setImageResource(R.drawable.dot_white_sel_);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(dp2px(context, 5), 0, 0, 0);
            dot_parent.addView(dotView, layoutParams);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (dot_parent == null) {
                    return;
                }
                if (viewList == null || viewList.size() == 0) {
                    return;
                }
                int realPos = position % viewList.size();
                ImageView lasview = (ImageView) dot_parent.getChildAt(lastPage);
                if (lasview != null) {
                    lasview.setImageResource(R.drawable.dot_white_sel_);
                }
                lastPage = realPos;
                ImageView view = (ImageView) dot_parent.getChildAt(realPos);
                if (view != null) {
                    view.setImageResource(R.drawable.dot_white_);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void dismiss() {
        dismissDialog(dialog);
        dialog = null;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        if (isDestroy(dialog.getContext())) {
            return;
        }
        if (dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static boolean isDestroy(Context context) {
        if (context instanceof Activity) {
            return isDestroy((Activity) context);
        }
        return false;
    }

    public static int dp2px(Context ctx, float dp) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
