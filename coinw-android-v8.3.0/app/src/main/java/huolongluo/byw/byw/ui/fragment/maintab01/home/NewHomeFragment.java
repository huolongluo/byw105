package huolongluo.byw.byw.ui.fragment.maintab01.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.utils.Utilities;
import com.android.legend.socketio.SocketIOClient;
import com.android.legend.ui.mine.MineActivity;
import com.android.legend.view.framelayout.SkinMarqueeView;
import com.google.gson.reflect.TypeToken;
import com.legend.common.TestThemeActivity;
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter;
import com.mob.tools.utils.ResHelper;
import com.sunfusheng.marqueeview.MarqueeView;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.BR;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.inform.activity.NoticeActivity;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.adapter.HomeAdapter;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.CoinInfoBean;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.RiseFallBean;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.wrap.CoinInfoBeanWrap;
import huolongluo.byw.byw.ui.fragment.maintab05.ScanQRLoginActivity;
import huolongluo.byw.helper.FaceVerifyHelper;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.RiseFallResult;
import huolongluo.byw.model.ScrollNoticeBean;
import huolongluo.byw.reform.home.activity.ShareActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.zxing.MipcaActivityCapture;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.byw.view.NewUserHomeHeaderView;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NewHomeFragment extends BaseFragment implements View.OnClickListener {
    private HomeAdapter homeAdapter;
    private RiseFallBean riseFallBean;
    private DataBindingRecyclerViewAdapter<CoinInfoBeanWrap> homeAdapter2;
    private Type type = new TypeToken<RiseFallResult>() {
    }.getType();
    private Map<String, Object> params = new HashMap<>();
    public boolean isShow = true;
    private long lastTime = 0L;
    private HomeBannerHandler homeBannerHandler = new HomeBannerHandler();
    private HomeDynamicHandler homeDynamicHandler = new HomeDynamicHandler();
    private HomeTopCoinHandler homeTopCoinHandler = new HomeTopCoinHandler();//首页行情滑动

    @Override
    protected void initDagger() {
    }

    private ListView listView;
    private Toolbar titleBar;
    private RelativeLayout zhang_view;
    private TextView zhangfuTv;
    private TextView newCoinsTv;
    private RelativeLayout chengjiaoe_bn;
    private RelativeLayout newCoinsLayout;
    private ImageButton scan_iv;
    private ImageButton news_iv;
    @BindView(R.id.ivMine)
    ImageView ivMine;
    @BindView(R.id.main_view)
    ScrollView main_view;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bn_imageview1)
    ImageView bn_imageview1;
    @BindView(R.id.iv_new_coins_tag)
    ImageView newCoinsTagIV;
    @BindView(R.id.bn_imageview3)
    ImageView bn_imageview3;
    @BindView(R.id.textview1)
    TextView textview1;
    @BindView(R.id.tv_new_coins)
    TextView newCoinsTxt;
    @BindView(R.id.textview3)
    TextView textview3;
    @BindView(R.id.tv_home_right)
    TextView tvHomeRight;
    @BindView(R.id.tv_home_center)
    TextView tvHomeCenter;
    @BindView(R.id.marqueeView)
    SkinMarqueeView marqueeView;
    @BindView(R.id.rlt_marqueeView)
    RelativeLayout rlt_marqueeView;
    List<CoinInfoBean> topList = new ArrayList<>();
    Dialog dialog = null;
    //dynamic
    @BindView(R.id.ll_home_dynamic)
    LinearLayout homeDynamicLayout;
    @BindView(R.id.newUserHomeHeaderView)
    NewUserHomeHeaderView newUserHomeHeaderView;
    @BindView(R.id.proHomeHeader)
    View proHomeHeader;
    private int screenWidth;
    private int screenHeight;
    private boolean isTurnover = false;//记录是否正在查看的是成交额榜

    @Override
    protected void initViewsAndEvents(View rootView) {
        initView(rootView);
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        if (Constant.STOP_SERVICE_IS_STOP_STARTUP) {
            return;
        }
        int[] screenSize = ResHelper.getScreenSize(mContext);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
        try {
            dialog = CustomLoadingDialog.createLoadingDialog(getActivity());
            dialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        homeAdapter = new HomeAdapter(getActivity(), mCurrentState);
        listView.setAdapter(homeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("排行榜", "onItemClick************");
                if (homeAdapter != null) {
                    //java.lang.IndexOutOfBoundsException: Index: 10, Size: 10
                    //    at java.util.ArrayList.get(ArrayList.java:437)
                    //    at huolongluo.byw.byw.ui.fragment.maintab01.adapter.HomeAdapter.getItem(HomeAdapter.java:2)
                    //    at huolongluo.byw.byw.ui.fragment.maintab01.home.NewHomeFragment$2.onItemClick(NewHomeFragment.java:2)
                    //    at android.widget.AdapterView.performItemClick(AdapterView.java:330)
                    //    at android.widget.AbsListView.performItemClick(AbsListView.java:1329)
                    //    at android.widget.AbsListView$PerformClick.run(AbsListView.java:3398)
                    //    at android.widget.AbsListView$3.run(AbsListView.java:4394)
                    //    at android.os.Handler.handleCallback(Handler.java:883)
                    //    at android.os.Handler.dispatchMessage(Handler.java:100)
                    //    at android.os.Looper.loop(Looper.java:230)
                    //    at android.app.ActivityThread.main(ActivityThread.java:7741)
                    //    at java.lang.reflect.Method.invoke(Native Method)
                    //    at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:508)
                    //    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1034)
                    try {
                        CoinInfoBean bean = (CoinInfoBean) homeAdapter.getItem(position);
                        MainActivity.self.gotoTrade(new MarketListBean(bean.getId(), bean.getCnyName(), bean.getCoinName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        List<CoinInfoBeanWrap> topListWrap = new LinkedList<>();
        for (CoinInfoBean coinInfoBean : topList) {
            topListWrap.add(new CoinInfoBeanWrap(coinInfoBean));
        }
        homeAdapter2 = new DataBindingRecyclerViewAdapter<>(getContext(), R.layout.home_item, BR.coinInfo, topListWrap);
        recyclerView.setAdapter(homeAdapter2);
        //
        homeAdapter2.setOnItemClickListener((view1, position) -> {

            if (homeAdapter2 == null) {
                return;
            }
            CoinInfoBean bean = homeAdapter2.getAllData().get(position).getCoinInfoBean();
            if (bean != null) {
                MobclickAgent.onEvent(getContext(), Constant.UMENG_EVENT_PRE_B + (position + 1), bean.getCoinName());
                MainActivity.self.gotoTrade(new MarketListBean(bean.getId(), bean.getCnyName(), bean.getCoinName()));
            }

        });
        rlt_marqueeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(),NoticeActivity.class));
            }
        });
        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                startActivity(new Intent(requireActivity(),NoticeActivity.class));
            }
        });
        // 设置首页
        switchHomeHeader(SPUtils.isProHome(mContext), false);
        showProgress("");
        //
        loadCacheData();
        refresh();
        getScrollNotice();
    }

    private void loadCacheData() {
        Observable.just("").observeOn(Schedulers.io()).map(new Func1<String, RiseFallBean>() {
            @Override
            public RiseFallBean call(String r) {
                try {
                    String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("riseFallApi");
                    if (!TextUtils.isEmpty(result)) {
                        //TODO  json解析造成4s 造成卡顿
                        RiseFallBean bean = GsonUtil.json2Obj(result, RiseFallBean.class);
                        return bean;
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                }
            }
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return null;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(bean -> {
            if (bean == null || bean.getTotalAsset() == null) {
                return;
            }
            riseFallBean = bean;
            try {
                if (riseFallBean.getTotalAsset() != null && riseFallBean.getTotalAsset().size() > 0) {
                }
                notifyHomeAdapterData();
                updataRecommend(riseFallBean.appRecommend);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void cacheData(RiseFallBean bean) {
        if (bean == null) {
            return;
        }
        if (lastTime > 0L && System.currentTimeMillis() - lastTime < 5 * 60 * 1000) {
            return;
        }
        lastTime = System.currentTimeMillis();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                String txt = GsonUtil.obj2Json(bean, RiseFallBean.class);
                CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().remove("riseFallApi");
                CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("riseFallApi", txt);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        if (isVisibleToUser) {
            isShow = true;
            refreshHomeTitle();
            if (params.isEmpty()) {
                params.put("type", "1");
            }
            refresh();
            if (SPUtils.isProHome(mContext)) {
                //TODO 测试专用
                homeDynamicHandler.refresh();
            } else {
                if (newUserHomeHeaderView != null) {
                    newUserHomeHeaderView.refreshStepInfo(); // 更新新手引导状态
                }
            }
            registerEvent();
        } else {
            isShow = false;
            unregisterEvent();
        }
    }

    private void registerEvent() {
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_HOME, new TypeToken<RiseFallBean>() {
        }, bean -> {
            parseData(bean);
            return null;
        });
    }

    private void unregisterEvent() {
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_HOME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(newsId)) {
            upDataNews(newsId);
        }
        // 刷新用户状态
        if (getUserVisibleHint() && !SPUtils.isProHome(mContext) && newUserHomeHeaderView != null) {
            newUserHomeHeaderView.refreshStepInfo();
        }
        isShow = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isShow = false;
        DialogManager.INSTANCE.dismiss();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fgt_home;
    }

    private float downPoint = 0;

    private void initView(View rootView) {
        viewClick(ivMine, this);
        news_iv = findView(R.id.news_iv);
        scan_iv = findView(R.id.scan_iv);
        viewClick(news_iv, this);
        viewClick(scan_iv, this);
        listView = findView(R.id.listView);
        listView.setFocusable(false);
        titleBar = findView(R.id.title);
        zhang_view = findView(R.id.zhang_bn);
        zhangfuTv = findView(R.id.textview1);
        newCoinsTv = findView(R.id.tv_new_coins);
        newCoinsLayout = findView(R.id.rl_new_coins);
        newCoinsTxt = findView(R.id.tv_new_coins);
        chengjiaoe_bn = findView(R.id.chengjiaoe_bn);
        zhang_view.setOnClickListener(this);
        newCoinsLayout.setOnClickListener(this);
        chengjiaoe_bn.setOnClickListener(this);
        refreshHomeTitle();
        //banner
        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        LinearLayout dotLayout = findView(R.id.dot_parent);
        LinearLayout dotTopLayout = findView(R.id.ll_dot);
        //
        //
        //使用通用RecyclerView组件
        PagingScrollHelper scrollHelper = new PagingScrollHelper();//初始化横向管理器
        HorizontalPageLayoutManager horizontalPageLayoutManager = new HorizontalPageLayoutManager(1, 3);//这里两个参数是行列，这里实现的是一行三列
//        titleAdapter = new RsdTitleAdapter(this);//设置适配器
//        mrecRsd.setAdapter(titleAdapter);
        scrollHelper.setUpRecycleView(recyclerView);//将横向布局管理器和recycler view绑定到一起
        //scrollHelper.setOnPageChangeListener(this);//设置滑动监听
        recyclerView.setLayoutManager(horizontalPageLayoutManager);//设置为横向
        scrollHelper.updateLayoutManger();
        scrollHelper.scrollToPosition(0);//默认滑动到第一页
        recyclerView.setHorizontalScrollBarEnabled(true);
        homeTopCoinHandler.init(getActivity(), scrollHelper, dotTopLayout);
        //
        homeBannerHandler.init(getActivity(), viewPager, dotLayout);
        homeDynamicHandler.init(getActivity(), homeDynamicLayout);

        textview1.setSelected(true);
        //
        main_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downPoint = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        EventBus.getDefault().post(new BizEvent.ShowRedEnvelope(true));
                        if (!isScrolling) {
                            if (downPoint != 0) {
                                float currentPosition = event.getRawY();
                                float pad = (downPoint - currentPosition);
                                if (Util.px2dp(getActivity(), pad) < 20) {
                                    //  main_view.setPadding(0,0,0, (int) pad);
                                } else {
                                    ///   updataList();
                                    // main_view.setPadding(0,0,0, (int) pad);
                                }
                            } else {
                                downPoint = event.getRawY();
                            }
                        } else {
                            downPoint = 0;
                        }
                        isScrolling = false;
                        //Log.i("onScrollChange", "=  " + event.getRawY());
                        break;
                    case MotionEvent.ACTION_UP:
                        EventBus.getDefault().post(new BizEvent.ShowRedEnvelope(false));
                        break;
                }
                return false;
            }
        });
        // 新手首页，头部
        newUserHomeHeaderView.setCallback(new NewUserHomeHeaderView.Callback() {
            @Override
            public void onProHomeClick() {
                switchHomeHeader(true, true);
            }
        });
    }

    boolean isScrolling = true;
    private String newsId = "";

    private void refreshHomeTitle() {
        if (tvHomeCenter == null) {
            return;
        }
        if (isTurnover) {//成交额的title最右边显示24小时成交额
            tvHomeRight.setText(getActivity().getResources().getString(R.string.home_24h_turnover) + "(" + PricingMethodUtil.getPricingSelectType() + ")");
        } else {
            tvHomeRight.setText(getActivity().getResources().getString(R.string.str_chg));
        }
    }

    public void upDataNews(String id) {//336
        if (news_iv == null) {
            return;
        }
        newsId = id;
    }

    void updataRecommend(List<CoinInfoBean> list) {
        if (homeAdapter2 == null) {
            return;
        }
        List<CoinInfoBeanWrap> topListWrap = new LinkedList<>();
        for (CoinInfoBean coinInfoBean : list) {
            topListWrap.add(new CoinInfoBeanWrap(coinInfoBean));
        }
        homeAdapter2.refreshData(topListWrap);
    }

    private int mCurrentState;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zhang_bn:
                mCurrentState = 0;
                isTurnover = false;
                refreshHomeTitle();
                bn_imageview1.setVisibility(View.VISIBLE);
                newCoinsTagIV.setVisibility(View.INVISIBLE);
                bn_imageview3.setVisibility(View.INVISIBLE);
                textview1.setSelected(true);
                newCoinsTxt.setSelected(false);
                textview3.setSelected(false);
                notifyHomeAdapterData();
                break;
            case R.id.rl_new_coins://新币榜（由于历史原因，暂时采用此方法实现吧，待重构优化此处内容）
                mCurrentState = 2;
                isTurnover = false;
                refreshHomeTitle();
                bn_imageview1.setVisibility(View.INVISIBLE);
                newCoinsTagIV.setVisibility(View.VISIBLE);
                bn_imageview3.setVisibility(View.INVISIBLE);
                textview1.setSelected(false);
                newCoinsTxt.setSelected(true);
                textview3.setSelected(false);
                notifyHomeAdapterData();
                break;
            case R.id.chengjiaoe_bn:
                mCurrentState = 3;
                isTurnover = true;
                refreshHomeTitle();
                bn_imageview1.setVisibility(View.INVISIBLE);
                newCoinsTagIV.setVisibility(View.INVISIBLE);
                bn_imageview3.setVisibility(View.VISIBLE);
                textview1.setSelected(false);
                newCoinsTxt.setSelected(false);
                textview3.setSelected(true);
                notifyHomeAdapterData();
                break;
            case R.id.news_iv:
                // TODO: 2018/10/19 0019 公告
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            case R.id.scan_iv:
                // TODO: 2018/10/19 0019 扫码登录
                if (UserInfoManager.isLogin()) {
                    Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
                    startActivityForResult(intent, 1);
                } else {
                    DialogUtils.getInstance().showTwoButtonDialog1(getActivity(), getString(R.string.dd2), getString(R.string.dd7), getString(R.string.dd6));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            startActivity(LoginActivity.class);
                        }
                    });
                }
                break;
            case R.id.ll1:
                if (riseFallBean != null && riseFallBean.appRecommend.size() > 0) {
                    MainActivity.self.gotoTrade(new MarketListBean(riseFallBean.appRecommend.get(0).getId(), riseFallBean.appRecommend.get(0).getCnyName(), riseFallBean.appRecommend.get(0).getCoinName()));
                }
                break;
//            case R.id.ll2:
//                if (riseFallBean != null && riseFallBean.appRecommend.size() > 1) {
//                    MainActivity.self.gotoTrade(new MarketListBean(riseFallBean.appRecommend.get(1).getId(), riseFallBean.appRecommend.get(1).getCnyName(), riseFallBean.appRecommend.get(1).getCoinName()));
//                }
//                break;
            case R.id.licai_rl:
                startActivity(ShareActivity.class);
                break;
            case R.id.ivMine:
                MineActivity.launch(requireContext());
                break;
        }
    }

    /**
     * 刷新userInfo后，更新新主页配置
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.refreshInfo event) {
        switchHomeHeader(isEnterProHome(), false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.SwitchHomeUi event) {
        switchHomeHeader(!event.isNewbie, false);
    }

    //本地计价方式切换需要马上刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        refreshHomeTitle();
        if (homeAdapter != null) {
            homeAdapter.notifyDataSetChanged();
        }
        if (homeAdapter2 != null) {
            homeAdapter2.notifyDataSetChanged();
        }
    }

    /**
     * 切换首页头部
     *
     * @param isToPro     是否切换到专业版，true为切换到专业版，false为切换到新手版
     * @param isAnimation 是否需要切换动画
     */
    private void switchHomeHeader(boolean isToPro, boolean isAnimation) {
        SPUtils.saveProHome(mContext, isToPro); // 保存切换到哪种首页
        if (isToPro) {
            newUserHomeHeaderView.setVisibility(View.GONE);
            proHomeHeader.setVisibility(View.VISIBLE);
            zhangfuTv.setText(R.string.zhangfub); // 专业版首页，列表第一个tab显示涨幅榜
            newCoinsTv.setText(R.string.str_new_coins); // 专业版首页，列表第二个tab显示新币榜
        } else {
            newUserHomeHeaderView.setVisibility(View.VISIBLE);
            proHomeHeader.setVisibility(View.GONE);
            zhangfuTv.setText(R.string.zhuliub);// 新手版首页，列表第一个tab显示主流榜
            newCoinsTv.setText(R.string.zhangfub);// 新手版首页，列表第二个tab显示涨幅榜
            main_view.scrollTo(0, 0); // scroll to top
            if (isShow) { //当用户在新手首页时，会在onresume中更新，所以这里不需要更新
                newUserHomeHeaderView.refreshStepInfo(); // 更新新手引导状态
            }
        }
        notifyHomeAdapterData();
        if (isAnimation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int x, y;
                if (isToPro) {
                    x = screenWidth;
                    y = (int) (screenHeight * 0.05);
                } else {
                    x = (int) (screenWidth * 0.5);
                    y = (int) (screenHeight * 0.95);
                }
                ViewAnimationUtils.
                        createCircularReveal(view, x, y, 0, (float) Math.hypot(screenWidth, screenHeight)).setDuration(500).start();
            } else {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.otc_act_in));
            }
        }
    }

    /**
     * 是否进入专业版，true为切换到专业版，false为切换到新手版
     */
    private boolean isEnterProHome() {
        if (UserInfoManager.isLogin()) {
            // 用户已登录，根据userInfo的字段判断进入哪个页面
            UserInfoBean userInfo = UserInfoManager.getUserInfo();
            return !userInfo.isNewComer();
        } else {
            // 用户未登录，根据上一次的标记的记录进入哪个页面
            return SPUtils.isProHome(mContext);
        }
    }

    private Handler handler = new Handler();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String ret = bundle.getString(CodeUtils.RESULT_STRING);
                    // showLoginDialog(ret);
                    if (aliVerify(ret)) {
                        return;
                    }
                    if (!URLUtil.isValidUrl(ret) || TextUtils.isEmpty(ret)) {
                        MToast.show(getActivity(), getString(R.string.dd3), 1);
                        return;
                    } else if (!ret.contains("/user/appQrcode/login.html")) {
                        MToast.show(getActivity(), getString(R.string.dd3), 1);
                        return;
                    }
                    MToast.show(getActivity(), getString(R.string.dd5), 1);
                    Intent intent = new Intent(getActivity(), ScanQRLoginActivity.class);
                    intent.putExtra("loginUrl", ret);
                    startActivityForResult(intent, 100);
                }
            }
        }
    }

    public void toPage(String notification)//点击通知做跳转
    {
        if (homeDynamicHandler != null) {
            homeDynamicHandler.toPage(notification);
        }
    }

    private boolean aliVerify(String ret) {
        if (ret.contains("faceIdVerifyToken")) {//该二维码用于阿里人脸认证
            FaceVerifyHelper.getInstance().verify(getContext(), ret);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        if (homeBannerHandler != null) {
            homeBannerHandler.release();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        DialogManager.INSTANCE.dismiss();
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void getScrollNotice(){
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("size", AppConstants.UI.DEFAULT_PAGE_SIZE);
        String url = UrlConstants.GET_SCROLL_NOTICE;
        netTags.add(url);
        OKHttpHelper.getInstance().get(url, params, getScrollNoticeCallback, new TypeToken<ScrollNoticeBean>() {
        }.getType());
    }

    /****************************modify by guocj in 2019-07-29***************************/
    private void refresh() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Logger.getInstance().debug(TAG, "url is : " + UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall);
        if (params.isEmpty()) {
            params.put("type", "1");
        }
        String url = UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall;
        netTags.add(url);
        OKHttpHelper.getInstance().get(url, params, callback, type);
    }

    private INetCallback<RiseFallResult> callback = new INetCallback<RiseFallResult>() {
        @Override
        public void onSuccess(RiseFallResult rfr) throws Throwable {
            Logger.getInstance().debug(TAG, "risefall result! is show ? " + isShow, new Exception());
            Logger.getInstance().debug(TAG, "callback 收到的数据: " + GsonUtil.obj2Json(rfr, RiseFallResult.class));
            if (!isShow) {
                return;
            }
            if (rfr == null) {
                closeDialog();
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (rfr.data == null) {
                closeDialog();
                Logger.getInstance().debug(TAG, "data is null.");
                return;
            }
            parseData(rfr.data.data);
            closeDialog();
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
            if (!isShow) {
                return;
            }
            closeDialog();
        }
    };
    private INetCallback<ScrollNoticeBean> getScrollNoticeCallback = new INetCallback<ScrollNoticeBean>() {
        @Override
        public void onSuccess(ScrollNoticeBean bean) {
            if (bean == null) return;
            if (bean.getCode() == 0) {
                marqueeView.startWithList(bean.getData());
            }
        }

        @Override
        public void onFailure(Exception e) {
        }
    };

    private void closeDialog() {
        if (dialog != null) {
            AppHelper.dismissDialog(dialog);
            dialog = null;
        }
        DialogManager.INSTANCE.dismiss();
    }

    private void parseData(RiseFallBean result) {
        if (result == null) {
            Logger.getInstance().debug(TAG, "market data is null.");
            return;
        }
        Logger.getInstance().debug(TAG, "refreshed!");
        if (result.getTotalAsset() != null && result.getTotalAsset().size() > 0) {
        }
        riseFallBean = result;
        notifyHomeAdapterData();
        updataRecommend(riseFallBean.appRecommend);
    }

    /****************************modify by guocj in 2019-07-29***************************/
    private void notifyHomeAdapterData() {
        if (riseFallBean == null || homeAdapter == null) {
            return;
        }
        if (mCurrentState == 0) {
            if (SPUtils.isProHome(mContext)) { // 专业版首页，显示涨幅榜
                homeAdapter.notifyData(mCurrentState, riseFallBean.riseList);
            } else {  // 新手版首页，显示主流榜
                homeAdapter.notifyData(mCurrentState, riseFallBean.mainList);
            }
        } else if (mCurrentState == 1) {
            homeAdapter.notifyData(mCurrentState, riseFallBean.fallList);
        } else if (mCurrentState == 2) {
            if (SPUtils.isProHome(mContext)) { // 专业版首页，显示新币榜
                homeAdapter.notifyData(mCurrentState, riseFallBean.newList);
            } else {  // 新手版首页，显示涨幅榜
                homeAdapter.notifyData(mCurrentState, riseFallBean.riseList);
            }
        } else {
            homeAdapter.notifyData(mCurrentState, riseFallBean.legalMoneyList);
        }
    }

    @Override
    public void applyTheme() {
        homeAdapter2.notifyDataSetChanged();
        homeAdapter.notifyDataSetChanged();
    }
}




