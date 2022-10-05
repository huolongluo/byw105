package com.android.coinw.biz.trade;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.coinw.biz.event.BizEvent;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.cancelorder.CancelOrderFragmentNew;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.FastClickUtils;
/**
 * 交易 底部
 */
public class TradeBottomView extends BaseView implements View.OnClickListener {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tab2_2)
    ImageView tab2_2;
    @BindView(R.id.tab1_1)
    ImageView tab1_1;
    protected CancelOrderFragmentNew cancelOrderFragmentNew; // 当前委托
    private MyPagerAdapter pageAdapter;
    private boolean isETF = false;

    public TradeBottomView(Context context,Fragment fragment) {
        super(context,fragment);
    }

    public void setETF(boolean isETF) {
        this.isETF = isETF;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trade_bottom_view;
    }

    @Override
    protected void initView() {
    }

    public void clearData() {
        if (cancelOrderFragmentNew != null) {
            cancelOrderFragmentNew.reSetView();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tab2_2.setVisibility(View.GONE);
                    tab1_1.setVisibility(View.VISIBLE);
                } else {
                    tab2_2.setVisibility(View.VISIBLE);
                    tab1_1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (viewPager != null) {
            viewPager.setCurrentItem(0);
        }
    }

    public void setViewPager(FragmentManager fragmentManager) {
        ArrayList<View> vis = new ArrayList<>();
        cancelOrderFragmentNew = new CancelOrderFragmentNew(mContext, fragmentManager,mFragment);
        cancelOrderFragmentNew.setETF(isETF);
        cancelOrderFragmentNew.setUserVisibleHint(true);
        vis.add(cancelOrderFragmentNew.getView());
        pageAdapter = new MyPagerAdapter(vis);
        viewPager.setAdapter(pageAdapter);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            if (cancelOrderFragmentNew != null) {
                cancelOrderFragmentNew.setVisible(visible);
            }
        } else {
            cancelOrderFragmentNew.setVisible(visible);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCoin(BizEvent.Trade.CoinEvent dataEvent) {
        if(cancelOrderFragmentNew!=null){
            cancelOrderFragmentNew.setVisible(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cancelOrderFragmentNew != null) {
            cancelOrderFragmentNew.onDestroy();
            cancelOrderFragmentNew=null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @OnClick()
    public void onClickx(View v) {
        switch (v.getId()) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectViewPager(BizEvent.Trade.SelectViewPager selectViewPager) {
        if (null != viewPager) {
            if (selectViewPager.isETF == isETF) {
                viewPager.setCurrentItem(selectViewPager.index);
            }
        }
    }

    public static class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> viewLists;

        public MyPagerAdapter(ArrayList<View> viewLists) {
            super();
            this.viewLists = viewLists;
        }

        @Override
        public int getCount() {
            return viewLists.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(viewLists.get(position));
            return viewLists.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewLists.get(position));
        }
    }

    public void applyTheme(){
        if (cancelOrderFragmentNew != null) {
            cancelOrderFragmentNew.applyTheme();
        }
    }

}
