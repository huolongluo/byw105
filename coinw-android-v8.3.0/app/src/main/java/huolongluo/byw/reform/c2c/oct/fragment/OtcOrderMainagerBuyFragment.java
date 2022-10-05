package huolongluo.byw.reform.c2c.oct.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.TabEntity;

import java.util.ArrayList;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseFragment;
/**
 * Created by dell on 2019/6/10.
 */
public class OtcOrderMainagerBuyFragment extends BaseFragment {
    SlidingTabLayout tablayout;
    ViewPager viewPager;
    boolean isMe = false;
    private String[] mTitles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otcorder_managerbuy;
    }

    int orderType = 1;

    public static OtcOrderMainagerBuyFragment newInStance(int orderType) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderType", orderType);
        OtcOrderMainagerBuyFragment fragment = new OtcOrderMainagerBuyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreatedView(View rootView) {
        if (getArguments() != null) {
            orderType = getArguments().getInt("orderType", 1);
        }
        viewPager = fv(R.id.viewPager);
        tablayout = fv(R.id.tablayout);
        if (orderType == 1) {
            mTitles = getResources().getStringArray(R.array.otc_order_man);
        } else {
            mTitles = getResources().getStringArray(R.array.otc_order_man_shop_buy);
        }
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
//        tablayout.setTabData(mTabEntities);
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), orderType);
        viewPager.setAdapter(adapter);
        tablayout.setViewPager(viewPager, mTitles);
        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (!isMe) {
                    isMe = true;
                    viewPager.setCurrentItem(position);
                } else {
                    isMe = false;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (!isMe) {
                    tablayout.setCurrentTab(position);
                } else {
                    isMe = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public static class PageAdapter extends FragmentPagerAdapter {
        Fragment fragment[] = new Fragment[5];

        public PageAdapter(FragmentManager fm, int orderType) {
            super(fm);
            for (int i = 0; i < 5; i++) {
                fragment[i] = OtcorderManItemFragment.newFragment(i, 1, orderType);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragment[position];
        }

        @Override
        public int getCount() {
            return fragment.length;
        }
    }
}
