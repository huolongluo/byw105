package huolongluo.byw.byw.ui.fragment.maintab01.home;
import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import huolongluo.byw.R;
import huolongluo.byw.util.Util;
//首页行情滑动
public class HomeTopCoinHandler implements PagingScrollHelper.onPageChangeListener {
    private ImageView[] dotViews;//创建存放图片集合
    private LinearLayout dotLayout;

    public void init(Activity atv, PagingScrollHelper scrollHelper, LinearLayout dotLayout) {
        if (atv == null || atv.isFinishing() || atv.isDestroyed() || scrollHelper == null || dotLayout == null) {
            //TODO 处理异常
            return;
        }
        scrollHelper.setOnPageChangeListener(this);//设置滑动监听
        this.dotLayout = dotLayout;
        updateDot(atv.getApplicationContext());
    }

    public void updateDot(Context context) {
        //生成相应数量的导航小圆点
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置小圆点左右之间的间隔
        params.setMargins(10, 0, 10, 0);
        //得到页面个数
        dotViews = new ImageView[2];//我这里是固定的六页  也可以根据自己需要设置圆点个数
        for (int i = 0; i < 2; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.bg_cor2_background_tab_w15_h2);//初始化六个灰色Img
            if (i == 0) {
                //默认启动时，选中第一个小圆点
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);//其他的设置不选择
            }
            //得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。
            dotViews[i] = imageView;
            dotViews[i].setImageResource(R.drawable.bg_cor2_accent_w15_h2);//设置第一个页面选择
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(Util.dp2px(context, 0), 0, 0, 0);
            layoutParams.setMargins(Util.dp2px(context, 0), 0, 0, 0);
            dotLayout.addView(imageView, layoutParams);
        }
    }

    @Override
    public void onPageChange(int index) {
        //这里是配合圆点指示器实现的，可以忽略
        for (int i = 0; i < dotViews.length; i++) {
            if (index == i) {
                dotViews[i].setSelected(true);
                dotViews[i].setImageResource(R.drawable.bg_cor2_accent_w15_h2);
            } else {
                dotViews[i].setSelected(false);
                dotViews[i].setImageResource(R.drawable.bg_cor2_background_tab_w15_h2);
            }
        }
    }
}
