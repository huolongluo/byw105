package huolongluo.byw.byw.ui.fragment.maintab02;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONException;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.utils.Utilities;
import com.android.legend.socketio.SocketIOClient;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.ETFMarketItemSlideAdapter;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.reform.home.activity.kline2.KLineActivity;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.OnResultCallback;
public class SideETFFragment extends Fragment implements OnResultCallback<MarketResult.Market>, View.OnClickListener {
    private View rootView;
    private EditText contentTxt;
    private TextView cancelTxt;
    private Map<String, Object> params = new HashMap<>();
    private Type type = new TypeToken<MarketResult>() {
    }.getType();
    private boolean isShow = false;
    private List<MarketListBean> dataList = new ArrayList<>();
    private List<MarketListBean> cacheDataList = new ArrayList<>();
    private final String TAG = "SideETFFragment";
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ETFMarketItemSlideAdapter marketItemAdapter = null;
    private boolean isScroll = false;
    private String searchStr = "";
    private boolean cache = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_etf_side, container, false);
        initView();
        initData();
        return rootView;
    }

    private void initView() {
        contentTxt = rootView.findViewById(R.id.et_content);
        cancelTxt = rootView.findViewById(R.id.cancle_tv);
        //
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        listView = rootView.findViewById(R.id.lv_content);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {//0静止，1滑动，2,滚动
                    isScroll = false;
                } else {
                    isScroll = true;
                }
//                marketItemAdapter.setScrollStatus(isScroll);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (marketItemAdapter != null) {
                    MarketListBean bean = marketItemAdapter.getItem(position);
                    if (getActivity() instanceof KLineActivity) {
                        EventBus.getDefault().post(new BizEvent.Trade.CoinEvent(bean.getId() + "", bean.getCoinName(), bean.getCnyName(), bean.getMycurrency(), true));
                    } else if (getActivity() instanceof MainActivity) {
                        MainActivity.self.gotoETFTrade(bean);
                        MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START);
                    } else {
                        EventBus.getDefault().post(new BizEvent.Trade.CoinEvent(bean.getId() + "", bean.getCoinName(), bean.getCnyName(), bean.getMycurrency(), true));
                    }
                }
            }
        });
        marketItemAdapter = new ETFMarketItemSlideAdapter(getActivity(), dataList);
//        marketItemAdapter.setmType(mType);
        listView.setAdapter(marketItemAdapter);
        //TODO 待完善
        rootView.findViewById(R.id.sort_market_ll).setOnClickListener(this);
        rootView.findViewById(R.id.price_market_ll).setOnClickListener(this);
        rootView.findViewById(R.id.zhangf_market_ll).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
    }

    private void initData() {
        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
//                reFresh();
            }
        });
        contentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTxt.setVisibility(View.VISIBLE);
                contentTxt.setCursorVisible(true);
            }
        });
        contentTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    try {
                        search(s.toString());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } else {
                    resetView();
                }
            }
        });
        contentTxt.requestFocus();
        contentTxt.setCursorVisible(false);
    }

    public void reload() {
        String result = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DATA, "");
        MarketResult.Market market = GsonUtil.json2Obj(result, MarketResult.Market.class);
        if (market != null) {
            parseData(market);
            cache = false;
        }
        refresh();
    }

    public void resetView() {
        this.searchStr = "";
        if (contentTxt == null || cancelTxt == null) {
            return;
        }
        if (cancelTxt.getVisibility() == View.GONE) {
            return;
        }
        if (cancelTxt != null) {
            cancelTxt.setVisibility(View.GONE);
        }
        contentTxt.setCursorVisible(false);
        contentTxt.setText("");
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        refreshList(cacheDataList);
    }

    private void search(String text) {
        this.searchStr = text;
        if (TextUtils.isEmpty(text)) {
            refreshList(cacheDataList);
            return;
        }
        //筛选
        List<MarketListBean> searchList = new ArrayList<>();
        for (MarketListBean bean : cacheDataList) {
            if (bean.getCoinName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(bean);
            }
        }
        refreshList(searchList);
    }

    private void refreshList(List<MarketListBean> list) {
        try {
            this.dataList.clear();
            this.dataList.addAll(list);
            marketItemAdapter.notifyDataSetChanged();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public boolean hasData = false;

    public void refresh() {
        if (contentTxt == null) {
            return;
        }
        //
        resetView();
        //TODO 优化
        registerEvent();
        isShow = true;
    }

    private void registerEvent() {
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_ETF_MARKET, new TypeToken<MarketResult.Market>() {
        }, bean -> {
            parseData(bean);
            return null;
        });
    }

    private void unregisterEvent() {
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_ETF_MARKET);
    }

    public void close() {
        unregisterEvent();
        isShow = false;
        try {
            if (contentTxt != null) {
                if (KeybordS.isSoftInputShow(getActivity())) {
                    KeybordS.closeKeybord(contentTxt, getActivity());
                }
                KeybordS.closeKeybord(contentTxt, getActivity());
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void parseData(final MarketResult.Market result) {
        if (result == null) {
            Logger.getInstance().debug(TAG, "market data is null.");
            return;
        }
        if (!result.result) {
            //TODO 处理失败情况
            Logger.getInstance().debug(TAG, "result is false!");
            return;
        }
        if (result.list == null) {
            //TODO 处理数据异常情况
            Logger.getInstance().debug(TAG, "list data is null!");
            return;
        }
        try {
            this.cacheDataList.clear();
            this.cacheDataList.addAll(result.list);
            if (!TextUtils.isEmpty(searchStr)) {
                //过滤数据
                search(searchStr);
            } else {
                this.dataList.clear();
                this.dataList.addAll(result.list);
            }
            marketItemAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
            //  Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
            SnackBarUtils.ShowBlue(getActivity(), getString(R.string.cc63));
        }
        if (cancelTxt.getVisibility() == View.VISIBLE) {
            Logger.getInstance().debug(TAG, "search todo!");
            return;
        }
        Logger.getInstance().debug(TAG, "refreshed!");
    }

    private void cacheData(MarketResult.Market result) {
        if (cache) {
            return;
        }
        if (result == null) {
            Logger.getInstance().debug(TAG, "market data is null.");
            return;
        }
        if (!result.result) {
            //TODO 处理失败情况
            Logger.getInstance().debug(TAG, "result is false!");
            return;
        }
        if (result.list == null) {
            //TODO 处理数据异常情况
            Logger.getInstance().debug(TAG, "list data is null!");
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!cache) {
                        String resultStr = GsonUtil.obj2Json(result, MarketResult.Market.class);
                        if (TextUtils.isEmpty(resultStr)) {
                            return;
                        }
                        SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DATA, resultStr);
                        cache = true;
                    }
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
    }

    @Override
    public void onResult(MarketResult.Market obj, String[] params) {
        Logger.getInstance().debug(TAG, "obj-isShow: " + isShow);
        if (!isShow) {
            return;
        }
        //缓存数据
        cacheData(obj);
        parseData(obj);
    }

    @Override
    public void onFail() {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
