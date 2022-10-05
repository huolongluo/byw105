package huolongluo.byw.byw.ui.fragment.search;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.bean.SelectCoinBean;
import huolongluo.byw.byw.ui.fragment.maintab01.LineTabVPAdapter;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.log.Logger;
/**
 * Created by LS on 2018/7/10.
 */
public class SearchCoinActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.rl_back)
    RelativeLayout Back;
    @BindView(R.id.tv_coin)
    TextView tvCoin;
    @BindView(R.id.rl_search)
    RelativeLayout rl_search;
    /* @BindView(R.id.tabLayout)
     TabLayout tabLayout;*/
    private List<Fragment> mData = null;
    public static int currentPager = 1;
    private List<SelectCoinBean> list;
    private String coinName;
    private TabLayout tabLayout;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initViewsAndEvents() {
        tabLayout = findViewById(R.id.tabLayout);
        if (getBundle() != null) {
            list = (List<SelectCoinBean>) getBundle().getSerializable("coisList");
        }
        mData = new ArrayList<Fragment>();
        // mData.add(SearchCusFragment.getInstance(list));
        //  mData.add(SearchCnytFragment.getInstance(list));
//        if (getBundle() != null){
//            coinName = getIntent().getExtras().getString("coinName");
//        }
//        if (coinName != null){
        tvCoin.setText(TradeDataHelper.getInstance().getCoinName());
//        }
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int position) {
                currentPager = position;
                if (position == 0) {
                }
                if (position == 1) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //点击事件
        eventClick(Back).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_search).subscribe(o -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("coisList", (Serializable) list);
            startActivity(SearchActivity.class, bundle);
            // finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        initTitles();
    }

    private Map<Integer, List<MarketListBean>> listMap = new HashMap<>();
    String[] titleString = null;

    private void initTitles() {
        List<TradingArea.TradItem> list = MarketDataPresent.getSelf().getTitleList();
        listMap.putAll(MarketDataPresent.getSelf().getMarketData());
        titleString = new String[list.size() + 1];
        titleString[0] = getString(R.string.ff1);
        SearchCnytFragment fragmentcus = new SearchCnytFragment();
        Bundle bundlecus = new Bundle();
        bundlecus.putInt("type", 0);
        Log.e("bbbbbbbbb", "bbbbbbbb   =  " + listMap);
        bundlecus.putSerializable("data", (Serializable) listMap.get(0));
        fragmentcus.setArguments(bundlecus);
        mData.add(fragmentcus);
        for (int i = 0; i < list.size(); i++) {
            TradingArea.TradItem item = list.get(i);
            titleString[i + 1] = item.getfShortName();
            SearchCnytFragment fragment = new SearchCnytFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", item.getFid());
            bundle.putSerializable("data", (Serializable) listMap.get(item.getFid()));
            fragment.setArguments(bundle);
            mData.add(fragment);
        }
        tabLayout.removeAllTabs();
        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setTag("0");
        View view1 = View.inflate(this, R.layout.market_tab_item, null);
        TextView zixuanview = view1.findViewById(R.id.tv_cnyt1);
        zixuanview.setText(R.string.ff1);
        tab1.setCustomView(zixuanview);
        tabLayout.addTab(tab1);
        if (list != null) {
            for (TradingArea.TradItem item : list) {
                TabLayout.Tab tab = tabLayout.newTab();
                View view = View.inflate(this, R.layout.market_tab_item, null);
                TextView textView = view.findViewById(R.id.tv_cnyt1);
                textView.setText(item.getfShortName());
                tab.setTag(item.getFid());
                tab.setCustomView(view);
                tabLayout.addTab(tab);
            }
        }
        tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tv_cnyt1).setEnabled(false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewpager.setCurrentItem(tab.getPosition(), false);
                Log.e("onTabSelected", "onTabSelected==   " + tab.getPosition());
                // cutPager(tab.getPosition(),true,tab.getTag().toString());
                tab.getCustomView().findViewById(R.id.tv_cnyt1).setEnabled(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("onTabSelected", "onTabUnselected==   " + tab.getPosition());
                // cutPager(tab.getPosition(),false,tab.getTag().toString());
                tab.getCustomView().findViewById(R.id.tv_cnyt1).setEnabled(true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //   tabLayout.setupWithViewPager(mViewpager,true);
        //   newIndexmarketListJson();
        setAdapter();
    }

    private void setAdapter() {
        mViewpager.setAdapter(new LineTabVPAdapter(this, getSupportFragmentManager(), mData, titleString));
        //mViewpager.setCurrentItem(currentPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        //    setAdapter();
    }
}
