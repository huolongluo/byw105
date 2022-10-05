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
import android.widget.ImageView;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.ETFMarketItemAdapter;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.OnResultCallback;

public class MarketETFFragment extends Fragment implements OnResultCallback<MarketResult.Market>, View.OnClickListener {
    private View rootView;
    private boolean isShow = false;
    protected List<MarketListBean> dataList = new ArrayList<>();
    protected List<MarketListBean> cacheDataList = new ArrayList<>();
    private final String TAG = "MarketETFFragment";
    private SwipeRefreshLayout refreshLayout;
    protected ListView listView;
    protected ETFMarketItemAdapter marketItemAdapter = null;
    private ImageView sortIV1, sortIV2, sortIV3;
    private int sortType = 0;//排序方式 1,11,2,12,3,13
    private boolean cache = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_etf, container, false);
        initView();
        initData();
        return rootView;
    }

    private void initView() {
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        listView = rootView.findViewById(R.id.lv_content);
        initAdapter();
        sortIV1 = rootView.findViewById(R.id.sort_iv1);
        sortIV2 = rootView.findViewById(R.id.sort_iv2);
        sortIV3 = rootView.findViewById(R.id.sort_iv3);
        //TODO 待完善
        rootView.findViewById(R.id.sort_market_ll).setOnClickListener(this);
        rootView.findViewById(R.id.price_market_ll).setOnClickListener(this);
        rootView.findViewById(R.id.zhangf_market_ll).setOnClickListener(this);
        refreshLayout.setOnRefreshListener(() -> {
            refresh();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        refreshLayout.setRefreshing(false);
                    } catch (Throwable t) {
                        Logger.getInstance().error(t);
                    }
                }
            }, 8000L);
        });
    }

    protected void initAdapter() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (marketItemAdapter != null) {
                    MarketListBean bean = marketItemAdapter.getItem(position);
                    MainActivity.self.gotoETFTrade(bean);
                    if (getActivity() instanceof MainActivity) {
                        MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START);
                    }
                }
            }
        });
        marketItemAdapter = new ETFMarketItemAdapter(getActivity(), dataList);
//        marketItemAdapter.setmType(mType);
        listView.setAdapter(marketItemAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_market_ll:
                if (sortType == 1) {
                    sortIV1.setImageResource(R.mipmap.market_sort_2);
                    sortType = 11;
                } else if (sortType == 11) {
                    sortIV1.setImageResource(R.mipmap.market_sort_0);
                    sortType = 0;
                } else {
                    sortIV1.setImageResource(R.mipmap.market_sort_1);
                    sortIV2.setImageResource(R.mipmap.market_sort_0);
                    sortIV3.setImageResource(R.mipmap.market_sort_0);
                    sortType = 1;
                }
                break;
            case R.id.price_market_ll:
                if (sortType == 2) {
                    sortIV2.setImageResource(R.mipmap.market_sort_2);
                    sortType = 12;
                } else if (sortType == 12) {
                    sortIV2.setImageResource(R.mipmap.market_sort_0);
                    sortType = 0;
                } else {
                    sortIV2.setImageResource(R.mipmap.market_sort_1);
                    sortIV1.setImageResource(R.mipmap.market_sort_0);
                    sortIV3.setImageResource(R.mipmap.market_sort_0);
                    sortType = 2;
                }
                break;
            case R.id.zhangf_market_ll:
                if (sortType == 3) {
                    sortIV3.setImageResource(R.mipmap.market_sort_2);
                    sortType = 13;
                } else if (sortType == 13) {
                    sortIV3.setImageResource(R.mipmap.market_sort_0);
                    sortType = 0;
                } else {
                    sortIV3.setImageResource(R.mipmap.market_sort_1);
                    sortIV1.setImageResource(R.mipmap.market_sort_0);
                    sortIV2.setImageResource(R.mipmap.market_sort_0);
                    sortType = 3;
                }
                break;
        }
        refreshList(cacheDataList);
    }

    private void initData() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //控制切换至当前界面时的业务操作
        if (isVisibleToUser) {
            //更新数据
//            resetView();
//            //
            reload();
            registerEvent();
            //重新获得数据sss
        } else {
            unregisterEvent();
        }
        Logger.getInstance().debug("guocj", "MarketETFFragment", new Exception());
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
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        refreshList(cacheDataList);
    }
    private void refreshList(List<MarketListBean> list) {
        try {
            this.dataList.clear();
            this.dataList.addAll(list);
            if (sortType != 0) {
                Collections.sort(dataList, new Comparator<MarketListBean>() {
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
            marketItemAdapter.notifyDataSetChanged();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public boolean hasData = false;

    public void refresh() {
        resetView();
        isShow = true;
    }

    public void close() {
        isShow = false;
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

    private void parseData(MarketResult.Market result) {
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
            this.dataList.clear();
            this.dataList.addAll(result.list);
//            if (cancelTxt.getVisibility() == View.VISIBLE) {
//                //过滤数据
//            } else {
//                this.dataList.clear();
//                this.dataList.addAll(result.list);
//            }
//            marketItemAdapter.notifyDataSetChanged();
            refreshList(result.list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
            //  Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
            SnackBarUtils.ShowBlue(getActivity(), getString(R.string.cc63));
        }
//
//        if (cancelTxt.getVisibility() == View.VISIBLE) {
//            Logger.getInstance().debug(TAG, "search todo!");
//            return;
//        }
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
                        SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DATA, resultStr);
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
        Logger.getInstance().debug(TAG, "obj-isShow: " + isShow + "obj:" + GsonUtil.obj2Json(obj, MarketResult.Market.class));
        //缓存数据
        parseData(obj);
        cacheData(obj);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadByEvent(BizEvent.Trade.Reload reload) {
        reload();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        if (marketItemAdapter != null) {
            marketItemAdapter.notifyDataSetChanged();
        }
    }
}
