package huolongluo.byw.byw.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/10.
 */

public class PopupWindowView extends PopupWindow {
    private int popupWidth;
    private int popupHeight;
    private ListView listView;
    private View parentView;
    public PopupWindowView(Context context , ArrayList<String> popBeans ) {
        super(context);
        initView(context);
        setPopConfig();
        initData(context,popBeans);
    }
    /**
     *   初始化控件
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/29,22:00
     * <h3>UpdateTime</h3> 2016/6/29,22:00
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     * @param context
     */
    private void initView(Context context) {
        parentView = View.inflate(context, R.layout.layout_popup_window, null);
        listView = parentView.findViewById(R.id.popup_window_list);
        setContentView(parentView);
    }

    /**
     *   初始化数据
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/29,22:03
     * <h3>UpdateTime</h3> 2016/6/29,22:03
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     * @param context
     * @param popBeans
     */
    private void initData(Context context, ArrayList<String> popBeans) {
        //给ListView添加数据
        PopListViewAdapter popListViewAdapter = new PopListViewAdapter(context,popBeans);
        listView.setAdapter(popListViewAdapter);
    }
    /**
     *
     * 配置弹出框属性
     * @version 1.0
     *
     * @createTime 2015/12/1,12:45
     * @updateTime 2015/12/1,12:45
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     *
     */
    private void setPopConfig() {
//        this.setContentView(mDataView);//设置要显示的视图
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xcc000000);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);// 设置外部触摸会关闭窗口

        //获取自身的长宽高
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = parentView.getMeasuredHeight();
        popupWidth = parentView.getMeasuredWidth();
    }
    public ListView getListView() {
        return listView;
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     * @param v
     */
    public void showUp2(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }
}
