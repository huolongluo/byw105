package huolongluo.byw.byw.ui.fragment.maintab01;
import android.annotation.SuppressLint;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.coinw.biz.event.BizEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MainAreaFragment;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.reform.market.MarketCoinSortManager;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/4.
 */
public class AllMarketFragment extends Fragment implements MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> {
    private static final String TAG = "AllMarketFragment";
    public String areaName;
    public String areaCoinsStr;
    private View mView;
    private ListView MmainList;
    private TextView cancle_tv;
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
    TextView empty_tip_tv;
    private static AllMarketFragment fragment;
    private int sortType = 0;//排序方式 1,11,2,12,3,13
    private boolean isVisibleToUser;

    public void setData(List<MarketListBean> listBeen) {
        this.listBeen = listBeen;
    }

    public static AllMarketFragment getInstance() {
        if (fragment == null) {
            fragment = new AllMarketFragment();
        }
        return fragment;
    }

    public String mType;
    public boolean isShowWarn;

    public String getPagerType() {
        return mType;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layot_market_item, null);
        listBeen.clear();
        if (getArguments() != null) {
            List<MarketListBean> listBeen1 = (List<MarketListBean>) getArguments().getSerializable("data");
            if (listBeen1 != null) {
                copeListBee = listBeen1;
                handleData(listBeen1);
            }
        }
        Log.e("onTabSelected", "onCreateView==   " + listBeen);
        initView();
        showWarn();
        return mView;
    }

    @Override
    public void onSuccess(Map<Integer, List<MarketListBean>> object) {
        if (isScroll) {
            return;
        }
        //Log.i("onSuccess", "type==  " + mType + "   object= " + object);
        Set<Map.Entry<Integer, List<MarketListBean>>> entry = object.entrySet();
        for (Map.Entry<Integer, List<MarketListBean>> entry1 : entry) {
            if (mType != null && entry1.getKey() == Integer.parseInt(mType)) {
                reFreshData(new ArrayList<>(entry1.getValue()));
                break;
            }
        }
        if (refresh_layout != null && refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            SnackBarUtils.ShowBlue(getActivity(), "刷新成功!");
        }
    }

    @Override
    public void onTitleSuccess(TradingArea object) {
    }

    @Override
    public void onFail(String failMessage) {
        if (refresh_layout != null && refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            SnackBarUtils.ShowRed(getActivity(), "刷新失败,请检查网络!");
        }
    }

    @Override
    public void onTitleSuccess(TitleEntity titleEntity) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void refreshAdapter(){
        if(marketItemAdapter!=null){
            marketItemAdapter.notifyDataSetChanged();
        }
    }

    public void checkUserInfo() {
        Log.e("checkUserInfo", "11mType= " + mType);
        if (UserInfoManager.isLogin()) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.GONE);
            }
            if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType)) {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.cc67);
                }
            } else if (TextUtils.equals("1", mType)) {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.nodata4);
                }
            }
        } else if (TextUtils.equals(Constant.MY_CODE, mType)) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.VISIBLE);
            }
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
    private boolean isScroll = false;

    @SuppressLint("ClickableViewAccessibility")
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
            MarketNewFragment.instance.reFresh();
            if (refresh_layout == null) {
                return;
            }
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
        MmainList.setOnItemClickListener((parent, view, position, id) -> {
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
                        //((CameraMainActivity) getActivity()).ShowTradeExplain(0);
                    }
                }
            }
        });
        empty_tv = mView.findViewById(R.id.empty_tv);
        empty_view = mView.findViewById(R.id.empty_view);
        if (TextUtils.equals(mType, String.valueOf(Constant.ZI_XUAN)) || TextUtils.equals(mType, "301")) {
            MmainList.setEmptyView(empty_view);
        }
        cancle_tv = mView.findViewById(R.id.cancle_tv);
        cancle_tv.setVisibility(View.GONE);
        if (listBeen == null) {
            listBeen = new ArrayList<>();
        }
        marketItemAdapter = new MarketItemAdapter(getActivity(), listBeen);
        marketItemAdapter.setmType(mType);
        MmainList.setAdapter(marketItemAdapter);
        MmainList.setOnTouchListener((v, event) -> {
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
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (marketItemAdapter != null) {
            marketItemAdapter.isNeedData = true;
        }
        if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType) || TextUtils.equals(mType, "301")) {
            if(isVisibleToUser){
                register();
                checkUserInfo();
                try {
                    reFreshData(MarketDataPresent.getSelf().getMarketData().get(Constant.ZI_XUAN));
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType) || TextUtils.equals(mType, "301")) {
            unregister();
        }
    }
    public void register() {
        if (!EventBus.getDefault().isRegistered(this)) {//自选才注册获取币币的数据
            EventBus.getDefault().register(this);
            EventBus.getDefault().post(new Event.MarketBBSocket(true));
        }
    }

    public void unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            EventBus.getDefault().post(new Event.MarketBBSocket(false));
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        if (isVisibleToUser) {
            if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType) || TextUtils.equals(mType, "301")) {
                checkUserInfo();
                register();
            }
        }
        else {
            if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType) || TextUtils.equals(mType, "301")) {
                unregister();
            }
        }
    }

    private List<MarketListBean> copeListBee = new ArrayList<>();

    private void reFreshData(List<MarketListBean> listBee) {
        if (marketItemAdapter == null) {
            return;
        }
        if (listBee.size() == 0 && (!TextUtils.equals(mType, String.valueOf(Constant.ZI_XUAN)) && (!TextUtils.equals(mType, "10")) && !TextUtils.equals(mType, "301") && !MarketNewFragment.isSearching)) {
            return;
        }
        listBeen.clear();
        //保留一份副本
        copeListBee = listBee;
        handleData(listBee);
        if (listBeen != null && (listBeen.size() > 0 || TextUtils.equals(mType, String.valueOf(Constant.ZI_XUAN)) || TextUtils.equals(mType, "10") || TextUtils.equals(mType, "301") || MarketNewFragment.isSearching)) {
            marketItemAdapter.notifyDataSetChanged();
            if (!MarketNewFragment.isSearching) {
                if (TextUtils.equals(String.valueOf(Constant.ZI_XUAN), mType)) {
                    if (empty_tip_tv != null) {
                        empty_tip_tv.setText(R.string.cc67);
                    }
                }
            } else {
                if (empty_tip_tv != null) {
                    empty_tip_tv.setText(R.string.cc71);
                }
            }
            if (listBeen.size() == 0) {
                empty_tv.setVisibility(View.VISIBLE);
            } else {
                empty_tv.setVisibility(View.GONE);
            }
        }
    }

    void handleData(List<MarketListBean> listBee) {
        if (sortType == 0) {
            if (MarketNewFragment.isSearching || TextUtils.equals(mType, "301") || TextUtils.equals(mType, String.valueOf(Constant.ZI_XUAN))) {
                listBeen.addAll(listBee);
                return;
            }
            listBeen.addAll(listBee);
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

    public void showWarn() {
        //未使用该页面，需要在AllMarketFragmentTest页面做逻辑添加
        if (!TextUtils.equals(mType, "2") && !TextUtils.equals(mType, "3") && !TextUtils.equals(mType, "0") && !TextUtils.equals(mType, "6")
        &&!TextUtils.equals(mType, "7") &&!TextUtils.equals(mType, MarketNewFragment.TYPE_STORAGE+"")) {
            return;
        }
        TextView area_name_tv = mView.findViewById(R.id.area_name_tv);
        RelativeLayout total_view = mView.findViewById(R.id.total_view);
        mView.findViewById(R.id.warn_view).setVisibility(View.VISIBLE);
        total_view.setOnClickListener(view -> {
            if (TextUtils.equals(mType, "2")) {
                ((MainActivity) getActivity()).showXianshiWarn(0);
            } else if (TextUtils.equals(mType, "3")) {
                ((MainActivity) getActivity()).showXianshiWarn(2);
            } else if (TextUtils.equals(mType, "0")) {
                ((MainActivity) getActivity()).showXianshiWarn(3);
            } else if (TextUtils.equals(mType, "6")) {
                ((MainActivity) getActivity()).showXianshiWarn(6);
            }
            else if (TextUtils.equals(mType, "7")) {
                ((MainActivity) getActivity()).showXianshiWarn(7);
            }
            else if (TextUtils.equals(mType, MarketNewFragment.TYPE_STORAGE+"")) {
                ((MainActivity) getActivity()).showXianshiWarn(MarketNewFragment.TYPE_STORAGE);
            }
        });
        TextView text_tv = mView.findViewById(R.id.text_tv);
        if (TextUtils.equals(mType, "2")) {
            area_name_tv.setText(R.string.dd10);
        } else if (TextUtils.equals(mType, "3")) {
            area_name_tv.setText(R.string.dd11);
        } else if (TextUtils.equals(mType, "0")) {
            area_name_tv.setText(R.string.cx_area);
        } else if (TextUtils.equals(mType, "6")) {
            area_name_tv.setText(R.string.defi_name);
        }  else if (TextUtils.equals(mType, "7")) {
            area_name_tv.setText(R.string.dao_name);
        } else if (TextUtils.equals(mType, MarketNewFragment.TYPE_STORAGE+"")) {
            area_name_tv.setText(R.string.storage_name);
        }else {
            area_name_tv.setText(R.string.main_area);
        }
        if (TextUtils.equals(mType, "2") || TextUtils.equals(mType, "3") || TextUtils.equals(mType, "0") || TextUtils.equals(mType, "6")
        ||TextUtils.equals(mType, "7")||TextUtils.equals(mType, MarketNewFragment.TYPE_STORAGE+"")) {
            total_view.setBackgroundColor(getResources().getColor(R.color.fffaeef0));
            text_tv.setBackgroundResource(R.drawable.home_more_bg2);
            text_tv.setText(R.string.dd12);
            text_tv.setTextColor(getResources().getColor(R.color.ffEC4A4A));
        } else {
            text_tv.setTextColor(getResources().getColor(R.color.ff8881a6));
            total_view.setBackgroundColor(getResources().getColor(R.color.fff0f1f6));
            text_tv.setBackgroundResource(R.drawable.home_more_bg);
            text_tv.setText(R.string.dd13);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMarketSelfData(Event.RefreshMarketSelfData data) {
        try {
            reFreshData(MarketDataPresent.getSelf().getMarketData().get(Constant.ZI_XUAN));
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
