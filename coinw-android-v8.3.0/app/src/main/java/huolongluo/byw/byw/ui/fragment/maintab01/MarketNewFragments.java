package huolongluo.byw.byw.ui.fragment.maintab01;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.util.ScreenShotListenManager;

/**
 * Created by LS on 2018/7/4.
 */

public class MarketNewFragments extends BaseFragment implements MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>, TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private AllMarketFragment[] fragments = new AllMarketFragment[]{};
    private List<MarketListBean> listBeen = new ArrayList<>();
    private Map<Integer, List<MarketListBean>> listMap = new HashMap<>();
    private Map<Integer, List<Integer>> childAreMap = new HashMap<>();
    private Map<Integer, List<MarketListBean>> copyListMap = new HashMap<>();
    public static boolean isSearching = false;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.ll_share)
    LinearLayout ll_share;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.errer_view)
    FrameLayout errer_view;
    @BindView(R.id.title)
    Toolbar title;
    @BindView(R.id.cancle_tv)
    TextView cancle_tv;
    @BindView(R.id.search_ll)
    LinearLayout search_ll;
    @BindView(R.id.main_search_ll)
    LinearLayout main_search_ll;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.delete_history_ll)
    LinearLayout delete_history_ll;
    private RelativeLayout hot_ll1;
    private RelativeLayout hot_ll2;
    private RelativeLayout hot_ll3;
    private TextView hot_tv1;
    private TextView hot_tv2;
    private TextView hot_tv3;
    private TextView hot_tv1_1;
    private TextView hot_tv2_1;
    private TextView hot_tv3_1;
    private EditText et_content;
    private Button srarch_button;
    public ScreenShotListenManager screenShotListenManager;
    public static MarketNewFragments instance;
    public static boolean isShow = true;
    @BindView(R.id.his_ll1)
    RelativeLayout his_ll1;
    @BindView(R.id.his_ll2)
    RelativeLayout his_ll2;
    @BindView(R.id.his_ll3)
    RelativeLayout his_ll3;
    @BindView(R.id.his_tv1)
    TextView his_tv1;
    @BindView(R.id.his_tv2)
    TextView his_tv2;
    @BindView(R.id.his_tv3)
    TextView his_tv3;
    @BindView(R.id.his_tv1_1)
    TextView his_tv1_1;
    @BindView(R.id.his_tv2_1)
    TextView his_tv2_1;
    @BindView(R.id.his_tv3_1)
    TextView his_tv3_1;
    @BindView(R.id.sousuo_title_rl)
    LinearLayout sousuo_title_rl;
    @BindView(R.id.licai_rl)
    RelativeLayout licai_rl;
    @BindView(R.id.iv_close1)
    ImageView iv_close1;
    @BindView(R.id.iv_close2)
    ImageView iv_close2;
    @BindView(R.id.iv_close3)
    ImageView iv_close3;

    //***************************************************************************************************************
    @Override
    protected int getContentViewId() {
        instance = this;
        return R.layout.fragment_market_new;
    }

    public static MarketNewFragments getInstance() {
        Bundle args = new Bundle();
        MarketNewFragments fragment = new MarketNewFragments();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        search_ll.setVisibility(View.VISIBLE);
        main_search_ll.setVisibility(View.GONE);
        MarketDataPresent.getSelf().setDataCallback(this);
        MarketDataPresent.getSelf().requestTitle1();
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        MarketDataPresent.getSelf().exchangeMarket(1);
        MarketDataPresent.getSelf().exchangeMarket(2);
    }


    private void setMarketDataCallback(MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback) {
        MarketDataPresent.getSelf().setDataCallback(callback);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSuccess(Map<Integer, List<MarketListBean>> object) {

    }

    @Override
    public void onTitleSuccess(TradingArea titles) {
        Log.i("MarketNewFragments", titles.toString());
    }

    @Override
    public void onFail(String failMessage) {

    }

    @Override
    public void onTitleSuccess(TitleEntity titleEntity) {

    }
}
