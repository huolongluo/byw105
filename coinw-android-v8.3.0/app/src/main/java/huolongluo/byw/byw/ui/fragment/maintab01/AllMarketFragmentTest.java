package huolongluo.byw.byw.ui.fragment.maintab01;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.coinw.biz.event.BizEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.manager.DialogManager;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.util.tip.SnackBarUtils;
/**
 * zh行情 USDT  CNYT的fragment(最小)
 */
public class AllMarketFragmentTest extends Fragment implements MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> {
    public int fid;
    public String coinName = "";
    public String areaName;
    public String areaCoinsStr;
    private View mView;
    private ListView MmainList;
    private RelativeLayout empty_tv;
    private FrameLayout empty_view;
    private SwipeRefreshLayout refresh_layout;
    private LinearLayout ll_nologin;
    private Button btn_bus_login;
    private List<MarketListBean> listBeen = new ArrayList<>();
    private MarketItemAdapter marketItemAdapter = null;
    private LinearLayout sort_market_ll;
    private LinearLayout price_market_ll;
    private LinearLayout zhangf_market_ll;
    private ImageView sort_iv1;
    private ImageView sort_iv2;
    private ImageView sort_iv3;
    private TextView empty_tip_tv;
    private static AllMarketFragmentTest fragment;
    private int sortType = 0;//排序方式 1,11,2,12,3,13
    public int pageCount;

    public AllMarketFragmentTest() {
        EventBus.getDefault().register(this);
    }

    public void release() {
        EventBus.getDefault().unregister(this);
    }

    public static AllMarketFragmentTest getInstance() {
        Bundle args = new Bundle();
        if (fragment == null) {
            fragment = new AllMarketFragmentTest();
        }
        return fragment;
    }

    public String mType;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layot_market_item, null);
        listBeen.clear();
        initView();
        return mView;
    }

    @Override
    public void onSuccess(Map<Integer, List<MarketListBean>> object) {
        if (isScroll) {
            return;
        }
        Set<Map.Entry<Integer, List<MarketListBean>>> entry = object.entrySet();
        List<MarketListBean> value = new ArrayList<>();
        for (Map.Entry<Integer, List<MarketListBean>> entry1 : entry) {
            if (mType != null && entry1.getKey() == Integer.parseInt(mType)) {
//                Logger.getInstance().debug("AllMarketFragmentTest", "key: " + entry1.getKey());
                for (int i = 0; i < entry1.getValue().size(); i++) {
                    //由于服务器接口数据返回极其不规范（短时间协调解决不了），故由客户端解决。。。。。坑
//                    if (fid == entry1.getValue().get(i).getFid()) {
                    if (TextUtils.equals(coinName, entry1.getValue().get(i).getCnyName())) {
                        value.add(entry1.getValue().get(i));
                    }
                }
                reFreshData(new ArrayList<>(value));
                break;
            }
        }
        //  }
        if (refresh_layout != null && refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            //  Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
            SnackBarUtils.ShowBlue(getActivity(), getString(R.string.cc63));
        }
    }
    public void refreshAdapter(){
        if(marketItemAdapter==null){
            return;
        }
        marketItemAdapter.notifyDataSetChanged();
    }
    @Override
    public void onTitleSuccess(TradingArea object) {
    }

    @Override
    public void onFail(String failMessage) {
        if (refresh_layout != null && refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.cc64));
        }
    }

    @Override
    public void onTitleSuccess(TitleEntity titleEntity) {
    }

    private boolean mHidden = false;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHidden = hidden;
        Log.e("xxxxxx", "xxx=  " + mHidden);
        if (!mHidden) {
            if (KeybordS.isSoftInputShow(getActivity())) {
                //KeybordS.closeKeybord(et_content, getActivity());
            }
        }
    }

    public void checkUserInfo() {
        Log.e("checkUserInfo", "11mType= " + mType);
        if (UserInfoManager.isLogin()) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.GONE);
            }
            if (TextUtils.equals(Constant.ZI_XUAN + "", mType)) {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.cc67);
                }
            } else if (TextUtils.equals(Constant.MY_CODE, mType)) {
                if (ll_nologin != null) {
                    ll_nologin.setVisibility(View.VISIBLE);
                }
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.cc68);
                }
            } else {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.nodata4);
                }
            }
            return;
        }
        if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType)) {
            if (empty_tip_tv != null) {
                empty_tip_tv.setText(R.string.cc67);
            }
        } else if (TextUtils.equals("1", mType)) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.VISIBLE);
            }
        } else {
            if (empty_tip_tv != null) {
                empty_tip_tv.setText(R.string.nodata4);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpen(BizEvent.Quotes.SlidingMenu slidingMenu) {
        if (empty_tv == null) {
            empty_tv.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sort_market_ll:
                    if (sortType == 1) {
                        sort_iv1.setImageResource(R.mipmap.market_sort_2);
                        sortType = 11;
                    } else if (sortType == 11) {
                        sort_iv1.setImageResource(R.mipmap.market_sort_0);
                        sortType = 0;
                    } else {
                        sort_iv1.setImageResource(R.mipmap.market_sort_1);
                        sort_iv2.setImageResource(R.mipmap.market_sort_0);
                        sort_iv3.setImageResource(R.mipmap.market_sort_0);
                        sortType = 1;
                    }
                    break;
                case R.id.price_market_ll:
                    if (sortType == 2) {
                        sort_iv2.setImageResource(R.mipmap.market_sort_2);
                        sortType = 12;
                    } else if (sortType == 12) {
                        sort_iv2.setImageResource(R.mipmap.market_sort_0);
                        sortType = 0;
                    } else {
                        sort_iv2.setImageResource(R.mipmap.market_sort_1);
                        sort_iv1.setImageResource(R.mipmap.market_sort_0);
                        sort_iv3.setImageResource(R.mipmap.market_sort_0);
                        sortType = 2;
                    }
                    break;
                case R.id.zhangf_market_ll:
                    if (sortType == 3) {
                        sort_iv3.setImageResource(R.mipmap.market_sort_2);
                        sortType = 13;
                    } else if (sortType == 13) {
                        sort_iv3.setImageResource(R.mipmap.market_sort_0);
                        sortType = 0;
                    } else {
                        sort_iv3.setImageResource(R.mipmap.market_sort_1);
                        sort_iv1.setImageResource(R.mipmap.market_sort_0);
                        sort_iv2.setImageResource(R.mipmap.market_sort_0);
                        sortType = 3;
                    }
                    break;
            }
            reFreshData(copeListBee);
        }
    };
    private boolean refresh = false;
    private boolean isScroll = false;

    private void initView() {
        empty_tip_tv = mView.findViewById(R.id.empty_tip_tv);
        sort_iv1 = mView.findViewById(R.id.sort_iv1);
        sort_iv2 = mView.findViewById(R.id.sort_iv2);
        sort_iv3 = mView.findViewById(R.id.sort_iv3);
        sort_market_ll = mView.findViewById(R.id.sort_market_ll);
        price_market_ll = mView.findViewById(R.id.price_market_ll);
        zhangf_market_ll = mView.findViewById(R.id.zhangf_market_ll);
        sort_market_ll.setOnClickListener(clickListener);
        price_market_ll.setOnClickListener(clickListener);
        zhangf_market_ll.setOnClickListener(clickListener);
        ll_nologin = mView.findViewById(R.id.ll_nologin);
        btn_bus_login = mView.findViewById(R.id.btn_bus_login);
        btn_bus_login.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        refresh_layout = mView.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        refresh_layout.setOnRefreshListener(() -> {
            //  MarketDataPresent.getSelf().requestMarketData();
            MarketNewFragment.instance.reFresh();
            refresh_layout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        refresh_layout.setRefreshing(false);
                    } catch (Throwable t) {
                        Logger.getInstance().error(t);
                    }
                }
            }, 8000L);
        });
        MmainList = mView.findViewById(R.id.lv_content);
        MmainList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("AbsListView", "==  " + scrollState);
                if (scrollState == 0) {//0静止，1滑动，2,滚动
                    isScroll = false;
                } else {
                    isScroll = true;
                }
                marketItemAdapter.setScrollStatus(isScroll);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        MmainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (marketItemAdapter != null) {
                    MarketListBean bean = marketItemAdapter.getItem(position);
                    if (TextUtils.equals(mType, "10")) {
                        ((MainActivity) getActivity()).showXianshiWarn(0);
                    } else if (TextUtils.equals(mType, "20")) {
                        ((MainActivity) getActivity()).showXianshiWarn(2);
                    } else {
                        if (bean.getFid() == 100) {//创新区
                            ((MainActivity) getActivity()).ShowTradeExplain(1);
                        } else {//主区
//                            ((CameraMainActivity) getActivity()).ShowTradeExplain(0);
                        }
                    }
                }
            }
        });
        empty_tv = mView.findViewById(R.id.empty_tv);
        empty_view = mView.findViewById(R.id.empty_view);
        if (TextUtils.equals(mType, String.valueOf(Constant.ZI_XUAN)) || TextUtils.equals(mType, Constant.MY_CODE)) {
            MmainList.setEmptyView(empty_view);
        }
        if (listBeen == null) {
            listBeen = new ArrayList<>();
        }
        marketItemAdapter = new MarketItemAdapter(getActivity(), listBeen);
        marketItemAdapter.setmType(mType);
        MmainList.setAdapter(marketItemAdapter);
        MmainList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        MarketNewFragment.isShow = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        MarketNewFragment.isShow = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        MarketNewFragment.isShow = true;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (marketItemAdapter != null) {
            marketItemAdapter.isNeedData = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("显示--  ", "mType==  " + mType + ";   isVisibleToUser=  " + isVisibleToUser);
    }

    private List<MarketListBean> mListBee = new ArrayList<>();
    private List<MarketListBean> copeListBee = new ArrayList<>();

    private void reFreshData(List<MarketListBean> listBee) {
        if (marketItemAdapter == null) {
            return;
        }
        if (listBee.size() == 0 && (!TextUtils.equals(mType, "0") && (!TextUtils.equals(mType, "10")) && !TextUtils.equals(mType, "1") && !MarketNewFragment.isSearching)) {
            return;
        }
        listBeen.clear();
        //保留一份副本
        copeListBee = listBee;
        handleData(listBee);
        if (listBeen.size() == 0) {
            empty_tv.setVisibility(View.VISIBLE);
        } else {
            empty_tv.setVisibility(View.GONE);
//                listener.OnListener(pageCount);
        }
        if (listBeen != null && (listBeen.size() > 0 || TextUtils.equals(mType, "0") || TextUtils.equals(mType, "10") || TextUtils.equals(mType, "1") || MarketNewFragment.isSearching)) {
            marketItemAdapter.notifyDataSetChanged();
            if (!MarketNewFragment.isSearching) {
                if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType)) {
                    if (empty_tip_tv != null) {
                        empty_tip_tv.setText(R.string.cc67);
                    }
                } else if (TextUtils.equals(Constant.MY_CODE, mType)) {
                    if (empty_tip_tv != null) {
                        String msgStr = String.format("coinName:%s,areaName:%s,areaCoinsStr:%s", coinName, areaName, areaCoinsStr);
                        empty_tip_tv.setText(R.string.cc68);
                    }
                } else {
                    empty_tv.setVisibility(View.GONE);
                }
            } else {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.cc71);
                }
            }
        }
    }

    void handleData(List<MarketListBean> listBee) {
        if (sortType == 0) {
            if (MarketNewFragment.isSearching || TextUtils.equals(mType, "1") || TextUtils.equals(mType, "0")) {
                listBeen.addAll(listBee);
                return;
            }
            for (int i = 0; i < 2; i++) {
                List<MarketListBean> listBeans = new ArrayList<>();
                if (i == 1) {
                    if (listBeans.size() > 0) {
                        MarketListBean bean1 = new MarketListBean();
                        bean1.setFid(100);
                        listBeen.add(bean1);
                    }
                    listBeen.addAll(listBeans);
                } else {
                    for (MarketListBean bean : listBee) {
//                        if (bean.getInnovationZone() == 0) {
                        listBeans.add(bean);
//                        }
                    }
                    listBeen.addAll(listBeans);
                }
            }
        } else {
            listBeen.addAll(listBee);
            Collections.sort(listBeen, new Comparator<MarketListBean>() {
                @Override
                public int compare(MarketListBean o1, MarketListBean o2) {
                    int i = 0;
                    switch (sortType) {
                        case 1:
                            i = (o2.getOneDayTotal() - o1.getOneDayTotal()) > 0 ? 1 : -1;
                            break;
                        case 11:
                            i = (o1.getOneDayTotal() - o2.getOneDayTotal()) > 0 ? 1 : -1;
                            break;
                        case 2:
                            i = (Double.parseDouble(o2.getLatestDealPrice()) - Double.parseDouble(o1.getLatestDealPrice())) > 0 ? 1 : -1;
                            break;
                        case 12:
                            i = (Double.parseDouble(o1.getLatestDealPrice()) - Double.parseDouble(o2.getLatestDealPrice())) > 0 ? 1 : -1;
                            break;
                        case 3:
                            i = (o2.getPriceRaiseRate() - o1.getPriceRaiseRate()) > 0 ? 1 : -1;
                            break;
                        case 13:
                            i = (o1.getPriceRaiseRate() - o2.getPriceRaiseRate()) > 0 ? 1 : -1;
                            break;
                    }
                    return i;
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // et_content.setText("");
        DialogManager.INSTANCE.dismiss();
        if (marketItemAdapter != null) {
            marketItemAdapter.isNeedData = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        marketItemAdapter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public interface ViewPagerSelectListener {
        void OnListener(int page);
    }
}
