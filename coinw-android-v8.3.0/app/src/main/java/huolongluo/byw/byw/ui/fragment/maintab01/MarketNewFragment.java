package huolongluo.byw.byw.ui.fragment.maintab01;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONException;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.model.Coin;
import com.android.coinw.utils.Utilities;
import com.android.legend.socketio.SocketIOClient;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.bean.SelectCoinBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MainAreaFragment;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Hot;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.reform.home.activity.ShareActivity;
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.reform.market.MarketAdapter;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.ScreenShotListenManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.bywx.HttpRequestManager;
import huolongluo.bywx.OnResultCallback;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
import rx.Subscription;
/**
 * Created by LS on 2018/7/4.
 * zh行情页面币币的主Fragment,和MarketSwapFragment，MarketETFFragment同级
 * 上级为MarketHomeFragment
 */
public class MarketNewFragment extends BaseFragment implements MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>, OnResultCallback<MarketResult.Market>, TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    public static final int TYPE_STORAGE=8;
    public static final int TYPE_NFT=9;
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
    @BindView(R.id.viewPager1)
    ViewPager viewPager;
    @BindView(R.id.delete_history_ll)
    LinearLayout delete_history_ll;
    private View hotsearchLayout;
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
    public static MarketNewFragment instance;
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
    @BindView(R.id.warn_view)
    LinearLayout warn_view;
    @BindView(R.id.total_view)
    RelativeLayout total_view;
    @BindView(R.id.ivSpread)
    ImageView ivSpread;
    @BindView(R.id.tvWarn)
    TextView tvWarn;
    private Map<String, Object> params = new HashMap<>();
    private Type type = new TypeToken<MarketResult>() {
    }.getType();
    private boolean first = true;
    private Dialog dialog;
    public static List<MarketListBean> listMarket;//缓存行情获取的数据.侧滑栏，k线和交易页需要共用使用static
    private TitleEntity titleEntity;

    //***************************************************************************************************************
    @Override
    protected int getContentViewId() {
        instance = this;
        return R.layout.fragment_market_new;
    }

    private void setHotview(List<Hot> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        hot_ll1.setVisibility(View.VISIBLE);
        Hot hot = dataList.get(0);
        hot_tv1.setText(hot.shortName);
        hot_tv1_1.setText("/" + hot.name);
        hot_ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotClick(hot);
            }
        });
        if (dataList.size() > 1) {
            hot_ll2.setVisibility(View.VISIBLE);
            Hot hot1 = dataList.get(1);
            hot_tv2.setText(hot1.shortName);
            hot_tv2_1.setText("/" + hot1.name);
            hot_ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hotClick(hot1);
                }
            });
            if (dataList.size() > 2) {
                search_ll.setVisibility(View.GONE);
                hot_ll3.setVisibility(View.VISIBLE);
                Hot hot2 = dataList.get(2);
                hot_tv3.setText(hot2.shortName);
                hot_tv3_1.setText("/" + hot2.name);
                hot_ll3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hotClick(hot2);
                    }
                });
            }
        }
    }

    private void hotClick(Hot hot) {
        if (hot == null) {
            return;
        }
        int length = copyListMap.size();
        //清除搜索
        for (int i = 0; i < length-1; i++) {
            KeybordS.closeKeybord(et_content, getActivity());
            TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
            if (textView != null) {
                textView.setText("");
                textView.setVisibility(View.GONE);
            }
        }
        isSearching = false;
        search_ll.setVisibility(View.VISIBLE);
        et_content.setText("");
        et_content.setCursorVisible(false);
        main_search_ll.setVisibility(View.GONE);
        ((MainActivity) getActivity()).setObscuration(View.GONE);
        setData(copyListMap);
        MainActivity.self.gotoTrade(hot.shortName, hot.name, hot.tmid, hot.selfselection);
        et_content.setText("");
        et_content.setCursorVisible(false);
        KeybordS.closeKeybord(et_content, getActivity());
    }

    private void clearSearchHindView(int length) {
        //清除搜索
        for (int i = 0; i < length-1; i++) {
            KeybordS.closeKeybord(et_content, getActivity());
            TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
            if (textView != null) {
                textView.setText("");
                textView.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < length-1; i++) {
            TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
            if (textView != null) {
                textView.setText("");
                textView.setVisibility(View.GONE);
            }
        }
    }

    private void showDialog() {
        try {
            if (getActivity() == null) {
                return;
            }
            dialog = CustomLoadingDialog.createLoadingDialog(getActivity());
            dialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void closeDialog() {
        if (dialog != null) {
            AppHelper.dismissDialog(dialog);
            dialog = null;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        isShow = isVisibleToUser;
        if (isVisibleToUser) {
            //处理第一次加载
            if (first) {
                first = false;
                //判断是否有缓存数据
                showDialog();
            }else{
                registerEvent();
            }
            if (params.isEmpty()) {
                params.put("type", "1");
                params = EncryptUtils.encrypt(params);
            }
            Map planMap = new HashMap();
            planMap.put("type", "1");
            for (int i = 0; i < fragments.length; i++) {
                if (fragments[i] != null) {
                    fragments[i].checkUserInfo();
                }
            }
            refreshList();//更新自选列表
            isShow = true;
            collectHotData();
        } else {
            unregisterEvent();
            DialogManager.INSTANCE.dismiss();
            isShow = false;
            if (et_content != null) {
                if (KeybordS.isSoftInputShow(getActivity())) {
                    KeybordS.closeKeybord(et_content, getActivity());
                }
            }
            if (main_search_ll != null && main_search_ll.getVisibility() != View.GONE) {
                //隐藏搜索框
                KeybordS.closeKeybord(et_content, getActivity());
                int length = copyListMap.size();
                //清除搜索
                for (int i = 0; i < length-1; i++) {
                    TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
                    if (textView != null) {
                        textView.setText("");
                        textView.setVisibility(View.GONE);
                    }
                }
                isSearching = false;
                search_ll.setVisibility(View.VISIBLE);
                et_content.setText("");
                et_content.setCursorVisible(false);
                main_search_ll.setVisibility(View.GONE);
                ((MainActivity) getActivity()).setObscuration(View.GONE);
                Utilities.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setData(copyListMap);
                    }
                });
                et_content.setText("");
                et_content.setCursorVisible(false);
            }
        }
        Logger.getInstance().debug(TAG, "setUserVisibleHint-end!");
    }

    public static MarketNewFragment getInstance() {
        Bundle args = new Bundle();
        MarketNewFragment fragment = new MarketNewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
        EventBus.getDefault().register(this);
    }

    private List<MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>> mDataCallbackList = new ArrayList<>();

    private void setMarketDataCallback(MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback) {
        MarketDataPresent.getSelf().setDataCallback(callback);
    }

    public void reFresh() {
        refresh();
    }

    @Override
    public void onResult(MarketResult.Market obj, String[] params) {//未执行
//        Logger.getInstance().debug(TAG, "币币行情的数据 obj:" + GsonUtil.obj2Json(obj, MarketResult.Market.class));
        Logger.getInstance().debug(TAG, "obj-isShow: " + isShow);
//        if (!first) {
//            if (!isShow) {
//                return;
//            }
//        }
        closeDialog();
        if (obj == null) {
            return;
        }
        getData = true;
        if (BuildConfig.DEBUG) {
            String json = GsonUtil.obj2Json(obj, MarketResult.Market.class);
            Logger.getInstance().debug(TAG, "json: " + (json == null ? "" : json.substring(0, 5)));
            //测试环境
        } else {
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!CacheManager.isCache()) {
                        try {
                            String result = GsonUtil.obj2Json(obj, MarketResult.Market.class);
                            CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("indexApi_1", result);
                            CacheManager.setCacheSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
        parseData(obj);
    }

    @Override
    public void onFail() {
        getData = false;
    }

    Map<Integer, List<MarketListBean>> freshListMap = new HashMap<>();

    //刷新自选列表
    void refreshList() {
        freshListMap.clear();
        freshListMap.putAll(MarketDataPresent.getSelf().getMarketData());
        if (freshListMap.containsKey(Constant.ZI_XUAN)) {
            List<MarketListBean> listBeans = freshListMap.get(Constant.ZI_XUAN);
            Coin coin = TradeDataHelper.getInstance().getCoin();
            if (listBeans != null && coin.id >= 0) {
                MarketListBean bean1 = new MarketListBean(coin.id, coin.cnyName, coin.coinName);
                if (listBeans.contains(bean1)) {
                    if (coin.isSelf == 0) {
                        listBeans.remove(bean1);
                    } else {
                        listBeans.get(listBeans.indexOf(bean1)).setSelfselection(coin.isSelf);
                    }
                } else if (coin.isSelf == 1) {
                    if (listBeen != null) {
                        if (listBeen.contains(bean1)) {
                            MarketListBean bean2 = listBeen.get(listBeen.indexOf(bean1));
                            listBeans.add(bean2);
                        }
                    }
                }
                setData(freshListMap);
            }
        }
    }

    public void showProgressDialog(Context context) {
        DialogManager.INSTANCE.showProgressDialog(context, "");
    }

    public void dismiss() {
        DialogManager.INSTANCE.dismiss();
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initViewsAndEvents(View rootView) {
        Logger.getInstance().debug("guocj", "initViewsAndEvents");
        licai_rl.setOnClickListener(v -> startActivity(MoneyManagerActivity.class));
        hotsearchLayout = rootView.findViewById(R.id.ll_hotsearch);
        hot_ll1 = rootView.findViewById(R.id.hot_ll1);
        hot_ll2 = rootView.findViewById(R.id.hot_ll2);
        hot_ll3 = rootView.findViewById(R.id.hot_ll3);
        hot_tv1 = rootView.findViewById(R.id.hot_tv1);
        hot_tv2 = rootView.findViewById(R.id.hot_tv2);
        hot_tv3 = rootView.findViewById(R.id.hot_tv3);
        hot_tv1_1 = rootView.findViewById(R.id.hot_tv1_1);
        hot_tv2_1 = rootView.findViewById(R.id.hot_tv2_1);
        hot_tv3_1 = rootView.findViewById(R.id.hot_tv3_1);
        srarch_button = rootView.findViewById(R.id.srarch_button);//搜索
        srarch_button.setVisibility(View.GONE);
        //搜索
        et_content = rootView.findViewById(R.id.et_content);
        search_ll.setOnClickListener(v -> {
            KeybordS.closeKeybord(et_content, getActivity());
            isSearching = false;
            search_ll.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setObscuration(View.GONE);
            et_content.setText("");
            et_content.setCursorVisible(false);
            main_search_ll.setVisibility(View.GONE);
            ((MainActivity) getActivity()).setObscuration(View.GONE);
        });
        srarch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_content.setCursorVisible(true);
                et_content.setFocusable(true);
                et_content.requestFocus();
                //   srarch_button.setVisibility(View.GONE);
                search_ll.setVisibility(View.GONE);
                ((MainActivity) getActivity()).setObscuration(View.GONE);
                KeybordS.openKeybord(et_content, getActivity());
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    if (search_ll.getVisibility() != View.GONE) {
                        search_ll.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).setObscuration(View.GONE);
                    }
                    search(s.toString());
                } else {
                    if (search_ll.getVisibility() != View.VISIBLE) {
                        search_ll.setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).setObscuration(View.VISIBLE);
                    }
                    int length = copyListMap.size();
                    //清除搜索
                    for (int i = 0; i < length-1; i++) {
                        TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
                        if (textView != null) {
                            textView.setText("");
                            textView.setVisibility(View.GONE);
                        }
                    }
                    setData(copyListMap);
                }
            }
        });
        main_search_ll.setVisibility(View.GONE);
        errer_view.setVisibility(View.GONE);
        errer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketDataPresent.getSelf().setDataCallback(MarketNewFragment.this);
                MarketDataPresent.getSelf().requestTitle1();
                errer_view.setVisibility(View.GONE);
                showProgressDialog(getActivity());
                // initWebSocket();
            }
        });
        screenShotListenManager = ScreenShotListenManager.newInstance(getActivity());
        eventClick(ll_share).subscribe(o -> {
            startActivity(ShareActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(ll_search).subscribe(o -> {
            String text1 = SPUtils.getString(getActivity(), SPUtils.MARKET_1, null);
            if (TextUtils.isEmpty(text1)) {
                sousuo_title_rl.setVisibility(View.INVISIBLE);
                his_ll1.setVisibility(View.INVISIBLE);
                his_ll2.setVisibility(View.INVISIBLE);
                his_ll3.setVisibility(View.INVISIBLE);
            } else {
                sousuo_title_rl.setVisibility(View.VISIBLE);
                his_ll1.setVisibility(View.VISIBLE);
                String[] texe1_1 = text1.split("/");
                his_tv1.setText(texe1_1[0]);
                his_tv1_1.setText("/" + texe1_1[1]);
                String texe1_id = texe1_1[2];
                his_ll1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int length = copyListMap.size();
                        //清除搜索
                        clearSearchHindView(length);
                        isSearching = false;
                        search_ll.setVisibility(View.VISIBLE);
                        et_content.setText("");
                        et_content.setCursorVisible(false);
                        main_search_ll.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).setObscuration(View.GONE);
                        setData(copyListMap);
                        MainActivity.self.gotoTrade(new MarketListBean(Integer.parseInt(texe1_id), texe1_1[1], texe1_1[0]));
                        et_content.setText("");
                        et_content.setCursorVisible(false);
                        KeybordS.closeKeybord(et_content, getActivity());
//                        HotMoneyPresenter.collectHotData(null, coinBean.getId() + "");
                    }
                });
                iv_close1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        his_ll1.setVisibility(View.GONE);
                        SPUtils.delete1(getActivity());
                    }
                });
            }
            String text2 = SPUtils.getString(getActivity(), SPUtils.MARKET_2, null);
            if (TextUtils.isEmpty(text2)) {
                his_ll2.setVisibility(View.INVISIBLE);
                his_ll3.setVisibility(View.INVISIBLE);
            } else {
                his_ll2.setVisibility(View.VISIBLE);
                String[] texe1_1 = text2.split("/");
                his_tv2.setText(texe1_1[0]);
                his_tv2_1.setText("/" + texe1_1[1]);
                String texe1_id = texe1_1[2];
                his_ll2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int length = copyListMap.size();
                        //清除搜索
                        clearSearchHindView(length);
                        isSearching = false;
                        search_ll.setVisibility(View.VISIBLE);
                        et_content.setText("");
                        et_content.setCursorVisible(false);
                        main_search_ll.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).setObscuration(View.GONE);
                        setData(copyListMap);
                        MainActivity.self.gotoTrade(new MarketListBean(Integer.parseInt(texe1_id), texe1_1[1], texe1_1[0]));
                        et_content.setText("");
                        et_content.setCursorVisible(false);
                        KeybordS.closeKeybord(et_content, getActivity());
                    }
                });
                iv_close2.setOnClickListener(view -> {
                    his_ll2.setVisibility(View.GONE);
                    SPUtils.delete2(getActivity());
                });
            }
            String text3 = SPUtils.getString(getActivity(), SPUtils.MARKET_3, null);
            if (TextUtils.isEmpty(text3)) {
                his_ll3.setVisibility(View.INVISIBLE);
            } else {
                his_ll3.setVisibility(View.VISIBLE);
                String[] texe1_1 = text3.split("/");
                // String[] texe1_2 = texe1_1[0].split("/");
                his_tv3.setText(texe1_1[0]);
                his_tv3_1.setText("/" + texe1_1[1]);
                String texe1_id = texe1_1[2];
                his_ll3.setOnClickListener(v -> {
                    int length = copyListMap.size();
                    //清除搜索
                    clearSearchHindView(length);
                    isSearching = false;
                    search_ll.setVisibility(View.VISIBLE);
                    et_content.setText("");
                    et_content.setCursorVisible(false);
                    main_search_ll.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).setObscuration(View.GONE);
                    setData(copyListMap);
                    MainActivity.self.gotoTrade(new MarketListBean(Integer.parseInt(texe1_id), texe1_1[1], texe1_1[0]));
                    et_content.setText("");
                    et_content.setCursorVisible(false);
                    KeybordS.closeKeybord(et_content, getActivity());
                });
                iv_close3.setOnClickListener(view -> {
                    his_ll3.setVisibility(View.GONE);
                    SPUtils.delete3(getActivity());
                });
            }
            ((MainActivity) getActivity()).setObscuration(View.VISIBLE);
            search_ll.setVisibility(View.VISIBLE);
            main_search_ll.setVisibility(View.VISIBLE);
            et_content.setFocusable(true);
            et_content.requestFocus();
            et_content.setCursorVisible(true);
            KeybordS.openKeybord(et_content, getActivity());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(cancle_tv).subscribe(o -> {
            reFresh();
            int length = copyListMap.size();
            //清除搜索
            for (int i = 0; i < length-1; i++) {
                TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
                if (textView != null) {
                    textView.setText("");
                    textView.setVisibility(View.GONE);
                }
            }
            KeybordS.closeKeybord(et_content, getActivity());
            isSearching = false;
            search_ll.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setObscuration(View.VISIBLE);
            et_content.setText("");
            et_content.setCursorVisible(false);
            main_search_ll.setVisibility(View.GONE);
            ((MainActivity) getActivity()).setObscuration(View.GONE);
            setData(copyListMap);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        MarketDataPresent.getSelf().setDataCallback(this);
        MarketDataPresent.getSelf().requestTitle1();
        //获取自选与个人币种
        MarketDataPresent.exchangeMarket(1);
        MarketDataPresent.exchangeMarket(2);
        collectHotData();
        delete_history_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.delete(getActivity());
                his_ll1.setVisibility(View.GONE);
                his_ll2.setVisibility(View.GONE);
                his_ll3.setVisibility(View.GONE);
            }
        });
        refresh();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void setData(Map<Integer, List<MarketListBean>> copyListMap) {
        boolean refresh = false;
        for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : MarketDataPresent.getSelf().getCallBackList()) {
            refresh = true;
            callback.onSuccess(new HashMap<>(copyListMap));
            MarketDataPresent.getSelf().setListMap(new HashMap<>(copyListMap));
        }
        EventBus.getDefault().post(new Event.RefreshMarketSelfData());
        if (refresh) {
            first = false;
        }
    }

    boolean getData = false;
    private Subscription ms;

    public void reload() {
        MarketDataPresent.getSelf().requestTitle1();
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("indexApi_1");
        MarketResult.Market market = GsonUtil.json2Obj(result, MarketResult.Market.class);
        if (market != null) {
            parseData(market);
        }
        reFresh();
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        MarketDataPresent.exchangeMarket(1);
        MarketDataPresent.exchangeMarket(2);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void marketBBSocket(Event.MarketBBSocket data) {
        if(data.isOpen){
            registerEvent();
        }else{
            unregisterEvent();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        if(fragments==null){
            return;
        }
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i] != null) {
                if(fragments[i] instanceof MainAreaFragment){
                    ((MainAreaFragment)fragments[i]).refreshAdapter();
                }

            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upTitle(TitleEntity titleEntity) {
        if (titleEntity.getCode() == 0) {
            MarketDataPresent.getSelf().removeDataCallback(this);
            updateTabView(titleEntity);
            //result为null
            String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("indexApi_1");
            MarketResult.Market market = GsonUtil.json2Obj(result, MarketResult.Market.class);
            if (market != null) {
                closeDialog();
                parseData(market);
            }
        }
    }

    void search(String text) {
        isSearching = true;
        Map<Integer, List<MarketListBean>> searchListMap = new HashMap<>();
        Set<Map.Entry<Integer, List<MarketListBean>>> entry = copyListMap.entrySet();
        Set<Integer> keyset = copyListMap.keySet();
        for (int i : keyset) {
            List<MarketListBean> listBeans = new ArrayList<>();
            for (MarketListBean bean : copyListMap.get(i)) {
                if (bean.getCoinName().toLowerCase().contains(text.toLowerCase())) {
                    listBeans.add(bean);
                }
            }
            searchListMap.put(i, listBeans);
        }
        int length = copyListMap.size();
        for (int i = 0; i < length-1; i++) {
            int type = (int) tabLayout.getTabAt(i).getTag();
            TextView textView = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.number_tv);
            {
                if (searchListMap.get(type).size() > 0) {
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(searchListMap.get(type).size() + "");
                        textView.setBackgroundResource(R.drawable.rund_yellow1);
                        //用于跳转到搜索结果页面
                        viewPager.setCurrentItem(i, false);
                    }
                } else {
                    if (textView != null) {
                        textView.setText(" ");
                        textView.setVisibility(View.GONE);
                        textView.setBackgroundColor(getResources().getColor(R.color.ff00000));
                    }
                }
            }
        }
        setData(searchListMap);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void collectHotData() {
        if (hotsearchLayout == null) {
            return;
        }
        String data = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HOT_SEARCH, "");
        if (TextUtils.isEmpty(data)) {
            return;
        }
        Type type = new TypeToken<List<Hot>>() {
        }.getType();
        List<Hot> dataList = GsonUtil.json2Obj(data, type);
        if (dataList != null && dataList.size() > 0) {
            hotsearchLayout.setVisibility(View.VISIBLE);
            setHotview(dataList);
        }
    }

    private void updateTabView(TitleEntity tradingArea) {
        this.titleEntity=tradingArea;
        if (listMap != null) {
            listMap.clear();
        }
        fragments = new AllMarketFragment[tradingArea.getFbs().size()];
        tabLayout.removeAllTabs();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        listMap.put(Constant.ZI_XUAN,new ArrayList<>());
        if (tradingArea.getFbs() != null) {//接口分类
            for (int i = 0; i < tradingArea.getFbs().size(); i++) {
                TitleEntity.FbsBean fbsBean = tradingArea.getFbs().get(i);

                listMap.put(fbsBean.getType(), new ArrayList<>());
                if (fbsBean.getAreaCoins().size() != 0) {
                    ArrayList<Integer> areCoinsMap = new ArrayList<>();
                    for (TitleEntity.FbsBean.AreaCoinsBean ti : fbsBean.getAreaCoins()) {
                        areCoinsMap.add(ti.getFid());
                    }
                    childAreMap.put(fbsBean.getType(), areCoinsMap);
                }
                TabLayout.Tab tab = tabLayout.newTab();
                View view = View.inflate(getActivity(), R.layout.market_tab_item, null);
                TextView textView = view.findViewById(R.id.tv_cnyt1);
                textView.setText(fbsBean.getAreaName());
                RelativeLayout linear3 = view.findViewById(R.id.linearl);
                linear3.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, DeviceUtils.dip2px(getActivity(), 29)));
                tab.setTag(fbsBean.getType());
                tab.setCustomView(view);
                tabLayout.addTab(tab);
                if (fbsBean.getAreaCoins().size() != 0) {
                    MainAreaFragment areaFragment = new MainAreaFragment();
                    areaFragment.setDataValue(fbsBean);
                    fragments[i] = areaFragment;
                    fragments[i].areaName = fbsBean.getAreaName();
                    fragments[i].areaCoinsStr = fbsBean.getAreaCoinsStr();
                } else {
                    fragments[i] = new AllMarketFragment();
                    fragments[i].mType = fbsBean.getType() + "";
                    fragments[i].isShowWarn = fbsBean.isShowRisk();
                    setMarketDataCallback(fragments[i]);
                }
            }
        }
        MarketAdapter marketAdapter = new MarketAdapter(getChildFragmentManager(), listMap, fragments);
        if (fragments.length > 0) {
            viewPager.setAdapter(marketAdapter);
            viewPager.setCurrentItem(0, false);
            viewPager.setOffscreenPageLimit(fragments.length);
            tabLayout.addOnTabSelectedListener(this);
            tabLayout.getTabAt(0).select();
            viewPager.setOnPageChangeListener(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        super.onResume();
        isShow = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isShow = false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataCallbackList.clear();
        MarketDataPresent.getSelf().clearCallback();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ms != null && !ms.isUnsubscribed()) {
            ms.unsubscribe();
        }
    }

    //http接口返回
    @Override
    public void onSuccess(Map<Integer, List<MarketListBean>> object) {
        dismiss();
    }

    @Override
    public void onTitleSuccess(TradingArea tradingArea) {
    }

    @Override
    public void onTitleSuccess(TitleEntity titleEntity) {
        if (titleEntity.getCode() == 0) {
            MarketDataPresent.getSelf().removeDataCallback(this);
            updateTabView(titleEntity);
            //result为null
            String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("indexApi_1");
            MarketResult.Market market = GsonUtil.json2Obj(result, MarketResult.Market.class);
            if (market != null) {
                parseData(market);
            }
        }
    }

    @Override
    public void onFail(String failMessage) {
        errer_view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        try {
            showWarn(titleEntity.getFbs().get(tab.getPosition()).getType());
        }catch (Throwable t){
            t.printStackTrace();
        }
        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getTag() instanceof Integer) {
            if (((int) tab.getTag()) == 10) {
                if (!Share.get().getFirstXianShi()) {
                    Share.get().setFirstXianShi(true);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).showXianshiWarn(0);
                    }
                }
            }
        }
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
         if (tabLayout != null && tabLayout.getTabAt(position) != null) {
            tabLayout.getTabAt(position).select();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void showWarn(int type){
        if (type != 2 && type != 3 && type != 0 && type != 6&&type!=7&&type!=TYPE_STORAGE&&type!=TYPE_NFT) {
            warn_view.setVisibility(View.GONE);
            return;
        }
        warn_view.setVisibility(View.VISIBLE);
        if (type == 0) {
            tvWarn.setText(getResources().getString(R.string.xianshi_wran1));
        } else if (type == 2) {
            tvWarn.setText(getResources().getString(R.string.xianshi_wran11));
        } else if (type == 3) {
            tvWarn.setText(getResources().getString(R.string.xianshi_wran12));
        } else if (type == 4) {//杠杆交易风险提示
            tvWarn.setText(getResources().getString(R.string.margin_26));
        } else if (type == 6) {//defi专区风险提示
            tvWarn.setText(getResources().getString(R.string.defi_tip));
        } else if (type == 7) {//dao专区风险提示
            tvWarn.setText(getResources().getString(R.string.dao_tip));
        } else if (type == TYPE_STORAGE) {//存储专区风险提示
            tvWarn.setText(getResources().getString(R.string.storage_tip));
        } else if (type == TYPE_NFT) {//nft专区风险提示
            tvWarn.setText(getResources().getString(R.string.nft_tip));
        }
        total_view.setOnClickListener(view -> {
            ivSpread.setActivated(!ivSpread.isActivated());
            if(tvWarn.getVisibility()==View.VISIBLE){
                tvWarn.setVisibility(View.GONE);
            }else{
                tvWarn.setVisibility(View.VISIBLE);
            }

        });
    }

    /*********************modify by guocj [start]*******************/
    //业务内容，暂只能采用以前方式
    private void refresh() {//zh行情接口数据
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        if (params.isEmpty()) {
            params.put("type", "1");
            params = EncryptUtils.encrypt(params);
        }
        netTags.add(UrlConstants.DOMAIN + UrlConstants.exchangeMarket);
        HttpRequestManager.getInstance().post(UrlConstants.DOMAIN + UrlConstants.exchangeMarket, params, callback, type);
    }

    private INetCallback<MarketResult> callback = new INetCallback<MarketResult>() {
        @Override
        public void onSuccess(MarketResult mr) throws Throwable {
            dismiss();
            if (mr == null) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (mr.data == null) {
                Logger.getInstance().debug(TAG, "data is null.");
                return;
            }
            if (BuildConfig.DEBUG) {
                //测试专用
            } else {
            }
            closeDialog();
            parseData(mr.data.data);
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
        }
    };
    private void registerEvent() {//zh行情socket数据来源
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_MARKET, new TypeToken<MarketResult.Market>() {
        }, bean -> {
            parseData(bean);
            return null;
        });
    }

    private void unregisterEvent() {
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_MARKET);
    }

    private void parseData(MarketResult.Market result) {
        if (result == null) {
            Logger.getInstance().debug(TAG, "market data is null.");
            return;
        }
        if (null == main_search_ll) {
            return;
        }
        if (main_search_ll.getVisibility() == View.VISIBLE) {
            isSearching = true;
            return;
        } else {
            isSearching = false;
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
        listBeen.clear();
        try {
            dismiss();
            listBeen = result.list;
            if (listMap.size() > 0 && listBeen.size() > 0) {
                if (TradeDataHelper.getInstance().getId() == 0) {
                    MarketListBean listBean = listBeen.get(0);
                    Coin coin = new Coin(listBean.getId(), listBean.getCoinName(), listBean.getCnyName(), listBean.getMycurrency());
                    TradeDataHelper.getInstance().updateCoin(coin);
                    EventBus.getDefault().post(new Event.ChangeOption(listBean.getCoinName()));
                }
                Set<Map.Entry<Integer, List<MarketListBean>>> entry = listMap.entrySet();
                for (Map.Entry<Integer, List<MarketListBean>> entry1 : entry) {
                    listMap.get(entry1.getKey()).clear();
                }
                listMarket = result.list;
                for (MarketListBean bean : result.list) {
                    boolean isNeedGetLocal=true;
                    if (MarketDataPresent.listSelf.contains(bean.getId())) {//去掉自选的逻辑重新写
                        bean.setSelfselection(1);
                        listMap.get(300).add(bean);
                        isNeedGetLocal=false;
                    }
                    //本地自选补充
                    if(isNeedGetLocal){
                        if(SPUtils.getBoolean(getContext(), KLine2Util.getSelfSpKey(bean.getCoinName(),bean.getCnyName(),
                                Kline2Constants.TRADE_TYPE_COIN),false)){
                            bean.setSelfselection(1);
                            listMap.get(300).add(bean);
                        }

                    }
                    if (listMap.get(bean.getType()) != null) {
                        listMap.get(bean.getType()).add(bean);
                    }
                }
            }
            copyListMap.clear();
            copyListMap.putAll(listMap);
            setData(listMap);
        } catch (JSONException e) {
            e.printStackTrace();
            for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : MarketDataPresent.getSelf().getCallBackList()) {
                callback.onFail("fail");
            }
        }
    }

    /*********************modify by guocj [end]*******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadByEvent(BizEvent.Trade.ReloadBB reload) {
        reload();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//自选更新的优化
    public void updateSelf(BizEvent.Trade.UpdateSelf event) {
        Logger.getInstance().debug(TAG,"refreshData updateSelf listMarket == null:"+(listMarket == null)+"listMap == null:"+(listMap == null)+
                "MarketDataPresent.getSelf().getCallBackList()==null:"+(MarketDataPresent.getSelf().getCallBackList()==null)+"MarketDataPresent.getSelf().getCallBackList().size():"+
                MarketDataPresent.getSelf().getCallBackList().size());
        if (listMarket == null || listMap == null||MarketDataPresent.getSelf().getCallBackList()==null||MarketDataPresent.getSelf().getCallBackList().size()==0) {
            return;
        }
        listMap.get(300).clear();
        for (MarketListBean bean : listMarket) {
            boolean isNeedGetLocal=true;
            if (MarketDataPresent.listSelf.contains(bean.getId())) {//去掉自选的逻辑重新写
                bean.setSelfselection(1);
                listMap.get(300).add(bean);
                isNeedGetLocal=false;
            }
            //本地自选补充
            if(isNeedGetLocal){
                if(SPUtils.getBoolean(getContext(), KLine2Util.getSelfSpKey(bean.getCoinName(),bean.getCnyName(),
                        Kline2Constants.TRADE_TYPE_COIN),false)){
                    bean.setSelfselection(1);
                    listMap.get(300).add(bean);
                }

            }
        }
        Logger.getInstance().debug(TAG,"refreshData listMap:"+GsonUtil.obj2Json(listMap,Map.class));
        MarketDataPresent.getSelf().getCallBackList().get(0).onSuccess(new HashMap<>(listMap));
    }
}
