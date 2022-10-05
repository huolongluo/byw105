package com.android.coinw.biz.trade;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.coinw.api.kx.model.X24HData;
import com.android.coinw.api.kx.model.XDepthData;
import com.android.coinw.api.kx.model.XLatestDeal;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.PopupWindowMenuHelper;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.helper.TradeHelper;
import com.android.coinw.biz.trade.model.Coin;
import com.android.coinw.model.result.AssetResult;
import com.android.legend.model.BaseResponse;
import com.android.legend.model.CommonResult;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.kline.NetValueBean;
import com.android.legend.model.order.OrderSocketBean;
import com.android.legend.socketio.SocketIOClient;
import com.android.legend.ui.login.LoginActivity;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.android.legend.util.TimerUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.reflect.TypeToken;
import com.legend.common.data_binding.CustomAdapter;
import com.legend.common.util.ThemeUtil;
import com.legend.common.view.textview.DinproMediumTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.databinding.FgtTradeBinding;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.LimitedTimeTipsResult;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util;
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.reform.mine.activity.TradeOrderListActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.HttpRequestManager;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.EncryptUtils;
import huolongluo.bywx.utils.ValueUtils;
import io.socket.client.On;
import okhttp3.Request;
public abstract class TradeAbsFragment extends BaseFragment implements View.OnClickListener {
    private RadioGroup rgDirection;
    protected TextView coinTxt, cnyTxt;
    private TextView cnyPriceTxt;
    protected ImageView starIV, moreIV;
    private TradeRightView tradeRightView;
    protected TradeLeftView tradeLeftView;
    private AppBarLayout appBarLayout;
    private RelativeLayout lthLayout;
    private ViewGroup bottomViewGroup, rightViewGroup, leftViewGroup;
    protected TradeBottomView tradeBottomView;
    private View warnContentLayout, warnCloseView;
    private TextView warnContentTxt;//提示信息
    private LinearLayout etfTagLayout;
    protected int requestCode = 103;//去往k线图详情

    protected String TAG = this.getClass().getSimpleName() + "-x";
    private RelativeLayout rl_latest;
    private RelativeLayout rl_commission;
    private TextView tv_latest;
    private TextView tv_commission;
    private TextView tvAll;
    private ImageView shrinkIV;

    private PopupWindow popupWindow;
    private SwipeRefreshLayout refresh_layout;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isOnChangeCoin = false;
    private RelativeLayout warn_view;
    private TextView warn_des;
    private boolean isEtfFirst = false;//因为读取当前订单通过hint为true调用，etf第一次tradeBottomView为null不会调用
    private TradeViewModel viewModel;
    private boolean isVisibleToUser=false;
    private CountDownTimer netValueCountDownTimer;

    private FgtTradeBinding mBinding;
    private ObservableField<Boolean> mTickerIsUp = new ObservableField<>(true);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register();
    }

    public void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = FgtTradeBinding.inflate(inflater, container, false);
        mBinding.setTickerIsUp(mTickerIsUp);

        View rootView = mBinding.getRoot();
        appBarLayout = rootView.findViewById(R.id.abl_content);
        lthLayout = rootView.findViewById(R.id.lth);
        coinTxt = rootView.findViewById(R.id.tv_coin);
        cnyTxt = rootView.findViewById(R.id.tv_cny);
        warn_des = rootView.findViewById(R.id.warn_des);
        cnyPriceTxt = rootView.findViewById(R.id.tv_cny_price);
        refresh_layout = rootView.findViewById(R.id.refresh_layout);
        rootView.findViewById(R.id.bt_close).setOnClickListener(this);
        warn_view = rootView.findViewById(R.id.warn_layout);
        rootView.findViewById(R.id.bt_view).setOnClickListener(this);
        refresh_layout.setEnabled(false);
        refresh_layout.setColorSchemeResources(R.color.color_3e3f65, R.color.color_565781, R.color.color_2b2c54);
        //设置焦点
        coinTxt.requestFocus();
        //
        etfTagLayout = rootView.findViewById(R.id.ll_etf_tag);
        etfTagLayout.setVisibility(isETF() ? View.VISIBLE : View.GONE);//ETF币种标识
        starIV = rootView.findViewById(R.id.iv_star);
        starIV.setVisibility(isETF() ? View.GONE : View.VISIBLE);//ETF币种时，隐藏收藏按钮
        moreIV = rootView.findViewById(R.id.iv_more);
        //左部分-操作盘
        leftViewGroup = rootView.findViewById(R.id.ll_content_left);
        tradeLeftView = new TradeLeftView(getActivity(), this);
        tradeLeftView.setETF(isETF());
        tradeLeftView.setFragmentManager(getChildFragmentManager());
        leftViewGroup.addView(tradeLeftView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //右部分-交易对
        rightViewGroup = rootView.findViewById(R.id.ll_content_right);
        tradeRightView = new TradeRightView(getActivity(), this);
        tradeRightView.setETF(isETF());
        tradeRightView.getDepthData(TradeDataHelper.getInstance().getId() + "");
        rightViewGroup.addView(tradeRightView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //下部分 - 委单
        bottomViewGroup = rootView.findViewById(R.id.ll_bottom_view);
        tradeBottomView = new TradeBottomView(getActivity(), this);
        tradeBottomView.setETF(isETF());
        tradeBottomView.setViewPager(getChildFragmentManager());
        bottomViewGroup.addView(tradeBottomView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //提示信息
        warnContentLayout = rootView.findViewById(R.id.ll_warn);
        warnContentTxt = rootView.findViewById(R.id.tv_warn_content);
        warnCloseView = rootView.findViewById(R.id.iv_warn_close);
        //
        rgDirection = mBinding.tradePanel.llDirection.rgDirection;

        rgDirection.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.tv_tag_buy:
                    clickBuyOrSell(true);
                    break;
                case R.id.tv_tag_sell:
                    clickBuyOrSell(false);
                    break;
            }
        });

        initBottomTab(rootView);
        //初始化布局
        initView(rootView);
        initData();
        initObserver();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO 获得默认币种数据（第一次的情况）
        //获得个人资产数据
        getMarketInfo();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    protected void initBottomTab(View rootView) {
        rl_latest = rootView.findViewById(R.id.rl_latest);
        rl_latest.setVisibility(View.GONE);
        tv_latest = rl_latest.findViewById(R.id.tv_latest);
        tv_latest.setText(getResources().getString(R.string.zxcj));
        rl_commission = rootView.findViewById(R.id.rl_commission);
        tv_commission = rl_commission.findViewById(R.id.tv_latest);
        tv_commission.setText(getResources().getString(R.string.dqwt));
        tvAll = rootView.findViewById(R.id.tv_all);
        shrinkIV = rootView.findViewById(R.id.iv_shrink);
        shrinkIV.setTag(0);
        tvAll.setOnClickListener(this);
        shrinkIV.setOnClickListener(this);
        initSelected(tv_latest, tv_commission, 0);
    }

    /**
     * tab选中的样式
     * @param tv_latest
     * @param tv_commission
     * @param i
     */
    private void initSelected(TextView tv_latest, TextView tv_commission, int i) {
        tv_latest.setCompoundDrawables(null, null, null, getDrawableLine());
        tv_latest.setTextColor(ThemeUtil.INSTANCE.getThemeColor(getContext(), R.attr.col_text_title));
        tv_commission.setTextColor(ThemeUtil.INSTANCE.getThemeColor(getContext(), R.attr.col_text_content));
        tv_commission.setCompoundDrawables(null, null, null, null);
        selectBottomViewPager(i);
    }

    private void addEvent(View rootView) {
        //
        etfTagLayout.setOnClickListener(this);
        starIV.setOnClickListener(this);
        rootView.findViewById(R.id.ll_net_worth).setOnClickListener(this);
        rootView.findViewById(R.id.ll_menu).setOnClickListener(this);
        rootView.findViewById(R.id.iv_kline).setOnClickListener(this);
        rootView.findViewById(R.id.iv_more).setOnClickListener(this);
        rl_latest.setOnClickListener(this);
        rl_commission.setOnClickListener(this);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    refresh_layout.setRefreshing(false);
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.err));
                    return;
                }
                //重新获得数据
                getMarketInfo();
                try {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh_layout.setRefreshing(false);
                            } catch (Throwable t) {
                                Logger.getInstance().error(t);
                            }
                        }
                    }, 8000L);
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
        warnCloseView.setOnClickListener(this);
    }

    private void initView(View rootView) {
        initSelf();
        //添加事件
        addEvent(rootView);
        resetView();
    }
    private void initData(){
        viewModel=new ViewModelProvider(this).get(TradeViewModel.class);
        getLatestData();
    }
    private void initObserver(){
        viewModel.getNetValueData().observe(getViewLifecycleOwner(), new Observer<CommonResult<BaseResponse<NetValueBean>>>() {
            @Override
            public void onChanged(CommonResult<BaseResponse<NetValueBean>> stringCommonResult) {
                if(stringCommonResult.isSuccess()){
                    if(stringCommonResult.getData()!=null&&stringCommonResult.getData().getData()!=null){
                        if(tradeLeftView!=null){
                            tradeLeftView.updateNetValue(stringCommonResult.getData().getData().getPrice());
                        }
                    }
                }
            }
        });
        viewModel.getLatestData().observe(getViewLifecycleOwner(), new Observer<CommonResult<List<XLatestDeal>>>() {
            @Override
            public void onChanged(CommonResult<List<XLatestDeal>> result) {
                Logger.getInstance().debug(TAG,"viewModel.getLatestData():"+GsonUtil.obj2Json(result,CommonResult.class));
                if(result.isSuccess()){
                    if(result.getData()!=null&&result.getData().size()>0&&tradeRightView!=null){
                        tradeRightView.refreshPrice(result.getData().get(0));
                    }
                }
            }
        });
    }

    /**
     * 初始化自选 当登录使用接口和本地混合，需要先判断接口是否为自选，因为侧滑栏需要接口的数据，接口不是自选再判断本地
     */
    private void initSelf() {
        if (isETF()) {
            return;
        }
        if (UserInfoManager.isLogin()) {
            checkIsSelf();
        } else {
            initSelfLocal();
        }
    }

    private void checkIsSelf() {//行情已经调用获取了自选列表
        if (MarketDataPresent.listSelf == null) {
            return;
        }
        if (MarketDataPresent.listSelf.contains(TradeDataHelper.getInstance().getId())) {//接口已是自选不需要判断本地
            updateOptional(1, false);//接口已是自选不需要判断本地
            EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//更新侧滑栏自选
            return;
        }
        updateOptional(0, false);
    }

    private void initSelfLocal() {
        if (SPUtils.getBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), false)) {
            updateOptional(1, false);
            if (UserInfoManager.isLogin()) {//本地和接口混合时，本地是自选而接口不是自选，需要隐式调用接口更新为自选
//                saveSelf(true);//本地有但账户没有，静默添加一次
            }
        } else {
            updateOptional(0, false);
        }
        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//更新侧滑栏自选
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ll_menu) {//菜单
            openMenu();
        } else if (vId == R.id.iv_star) {//添加自选
            optionalCoin();
        } else if (vId == R.id.ll_etf_tag) {//ETF
            //ETF提示弹框
            //TODO 信息弹框
            DialogUtils.getInstance().showETFDoubtDialog(getActivity(), getString(R.string.str_etf_coin_desc));
        } else if (vId == R.id.ll_net_worth) {
            DialogUtils.getInstance().showETFDoubtDialog(getActivity(), getString(R.string.str_net_worth_desc));
        } else if (vId == R.id.iv_kline) {//K线
            if (TextUtils.isEmpty(TradeDataHelper.getInstance().getCoinName()) || TradeDataHelper.getInstance().getCoinName().equals("--")) {
                Logger.getInstance().debug(TAG, "进入k线失败，数据为空或为--");
                return;
            }
            int tradeType = isETF() ? Kline2Constants.TRADE_TYPE_ETF : Kline2Constants.TRADE_TYPE_COIN;
            Logger.getInstance().debug(TAG, "TradeDataHelper.getInstance().getCoinName():" + TradeDataHelper.getInstance().getCoinName());
            TradeHelper.gotoKLine(getActivity(), new KLineEntity(TradeDataHelper.getInstance().getId(isETF()), TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), tradeType));
        } else if (vId == R.id.iv_more) {//更多
            PopupWindowMenuHelper.getInstance().openPopupMenu(getActivity(), moreIV, this);
        } else if (vId == R.id.ll_recharge) {//充值
            if (!hasLoginForPopup()) {
                return;
            }
            TradeHelper.gotoRecharge(getActivity());
            PopupWindowMenuHelper.getInstance().dismiss();
        } else if (vId == R.id.ll_transfer) {//划转
            if (!hasLoginForPopup()) {
                return;
            }
            AccountTransferActivity.Companion.launch(getActivity(), null, null, 2, null, false, null);
            PopupWindowMenuHelper.getInstance().dismiss();
        } else if (vId == R.id.rl_latest) {
            initSelected(tv_latest, tv_commission, 0);
            tvAll.setVisibility(View.GONE);
//            shrinkIV.setVisibility(View.VISIBLE);
            //TODO 待解决
//            scrollTopForBottomView();
        } else if (vId == R.id.rl_commission) {
            gotoEntrust();
//            scrollTopForBottomView();
        } else if (vId == R.id.tv_all) {
            if (FastClickUtils.isFastClick(1000)) {
                return;
            }
            getActivity().startActivity(new Intent(getActivity(), UserInfoManager.isLogin() ? TradeOrderListActivity.class : LoginActivity.class));
        } else if (vId == R.id.iv_shrink) {
            if (FastClickUtils.isFastClick(1000)) {
                return;
            }
            int tag = (int) shrinkIV.getTag();
            if (tag == 0) {
                appBarLayout.setExpanded(false);
                shrinkIV.setTag(1);
                shrinkIV.setImageResource(R.mipmap.ic_down);
            } else {
                appBarLayout.setExpanded(true);
                shrinkIV.setTag(0);
                shrinkIV.setImageResource(R.mipmap.ic_up);
            }
        } else if (vId == R.id.iv_warn_close) {//关闭风险提示信息
            warnContentLayout.setVisibility(View.GONE);
        } else if (vId == R.id.bt_close) {
            warn_view.setVisibility(View.GONE);
        } else if (vId == R.id.bt_view) {
            String id = TradeDataHelper.getInstance().getId(isETF()) + "";
            String bchUrl = DataUtils.getBCHUrl(id);
            TradeHelper.openLimitedTimeTips(getActivity(), bchUrl);
        }
    }

    protected void openMenu() {
        if (MainActivity.self != null && MainActivity.self.drawer_layout != null){
            if (MainActivity.self.drawer_layout.isDrawerOpen(GravityCompat.START)) {
                MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START);
            } else {
                MainActivity.self.drawer_layout.openDrawer(GravityCompat.START);
            }
        }
    }

    private boolean hasLoginForPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (!UserInfoManager.isLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return false;
        }
        return true;
    }

    /**
     * 交易底部的左右viewpager切换
     * @param
     */
    private void selectBottomViewPager(int index) {
        BizEvent.Trade.SelectViewPager selectViewPager = new BizEvent.Trade.SelectViewPager();
        selectViewPager.index = index;
        selectViewPager.isETF = isETF();
        EventBus.getDefault().post(selectViewPager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onItemClick(BizEvent.Trade.ClickHandicap clickHandicap) {
        if (clickHandicap == null) {
            //TODO 异常情况
            return;
        }
        TradeHelper.scrollTop(appBarLayout);
    }

    //k线更新自选需要更新此处的自选
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOptionalEvent(BizEvent.Trade.UpdateSelfTrade event) {
        initSelf();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickBuyOrSell(BizEvent.Trade.KLineClickEvent event) {
        clickBuyOrSell(event.isBuy);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void marketInfo(BizEvent.Trade.MarketInfo marketInfo) {
        if (marketInfo.isETF != isETF()) {
            return;
        }
        getMarketInfo();
        gotoEntrust();
    }

    public void refreshMarketInfo(BizEvent.Trade.RefreshMarketInfo refreshMarketInfo) {
        if (refreshMarketInfo.isETF != isETF()) {
            return;
        }
        getMarketInfo();
    }

    private void gotoEntrust() {
        if (tvAll == null || shrinkIV == null) {
            return;
        }
        initSelected(tv_commission, tv_latest, 1);
        tvAll.setVisibility(View.VISIBLE);
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        if (tradeLeftView != null) {
            tradeLeftView.reset();
        }
        //退出登录
    }

    private void clickBuyOrSell(boolean isBuy) {
        if (isBuy) {
            changeTag();
            tradeLeftView.changeTag(false);//出售
            TradeHelper.scrollTop(appBarLayout);
        } else {
            changeTag();
            tradeLeftView.changeTag(true);//出售
            TradeHelper.scrollTop(appBarLayout);
        }
    }

    private void changeTag() {
        //切换状态的时候，加载可用余额
        getMarketInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onChangeCoin(BizEvent.Trade.CoinEvent coinEvent) {
        if (coinEvent == null) {
            //TODO 异常情况
            return;
        }
        //判断是否为同一种类型（根据业务暂时采用true or false，未采用多类型值）
        if (coinEvent.isETF != isETF()) {
            return;
        }
        if (TextUtils.equals(TradeDataHelper.getInstance().getId(isETF()) + "", coinEvent.id)) {
            //TODO 说明选择为同一币种，是否可以直接返回？待大量测试
//            return;
        }
        if (warn_view != null) {
            warn_view.setVisibility(View.GONE);
        }
        //切换币种，清空数据
        TradeDataHelper.getInstance().setLimitedTimeTips(null);
        isOnChangeCoin = true;
        //更新所选币种数据
        coinTxt.setText(coinEvent.coinName);
        String cnyName = coinEvent.cnyName;
        if (TextUtils.isEmpty(coinEvent.cnyName)) {
            cnyName = CurrencyPairUtil.getCnyName(NumberUtil.toLong(coinEvent.id));
        }
        cnyTxt.setText("/" + cnyName);
        TradeDataHelper.getInstance().updateDepth("-1", isETF());
        tradeRightView.updateDepth("-1");
        tradeLeftView.onChange(coinEvent.id, coinEvent.coinName, cnyName, coinEvent.selfStation);
        if (!TextUtils.equals(coinEvent.id, TradeDataHelper.getInstance().getId(isETF()) + "")) {
            tradeBottomView.clearData();
        }
        unSubscribeSocket();
        try {
            int id = Integer.valueOf(coinEvent.id);
            Coin coin = new Coin(id, coinEvent.coinName, cnyName, coinEvent.selfStation, isETF());
            TradeDataHelper.getInstance().updateCoin(coin);
            //TODO 更新数据
        } catch (Throwable t) {
            t.printStackTrace();
        }
        subscribeSocket();
        getLatestData();
        //重置风险提示框
        warnContentLayout.setVisibility(View.GONE);
        //获得个人资产数据
        getMarketInfo();
        getLimitedTimeTips(TradeDataHelper.getInstance().getId(isETF()));
        initSelf();
    }

    private void resetView() {
        if (coinTxt == null || cnyTxt == null || tradeLeftView == null || tradeRightView == null || tradeBottomView == null) {
            return;
        }
        Coin coin = TradeDataHelper.getInstance().getCoin(isETF());
        //更新所选币种数据
        coinTxt.setText(coin.coinName);
        cnyTxt.setText("/" + coin.cnyName);
        tradeLeftView.onChange(coin.id + "", coin.coinName, coin.cnyName, coin.isSelf);
        tradeRightView.clearData(coin.id);
        //更新界面
        tradeLeftView.updateNetValue("--");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (starIV == null) {
            return;
        }
        //重新检查状态
        if (TradeDataHelper.getInstance().isOptionalCoin()) {
            starIV.setImageResource(R.drawable.ic_trade_collect_on);
        } else {
            starIV.setImageResource(R.drawable.ic_trade_collect);
        }
        gotoEntrust();
        if (isEtfFirst && tradeBottomView != null) {
            isEtfFirst = false;
            tradeBottomView.setVisible(true);
        }
        if(isVisibleToUser){
            subscribeSocket();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unSubscribeSocket();
        if(MainActivity.self!=null&&MainActivity.self.drawer_layout!=null){
            if (MainActivity.self.drawer_layout.isDrawerOpen(GravityCompat.START)) {
                MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START, false);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregister();
        if (null != tradeRightView) {
            tradeRightView.onDestroy();
        }
        if (tradeLeftView != null) {
            tradeLeftView.onDestroy();
        }
        if (tradeBottomView != null) {
            tradeBottomView.onDestroy();
        }
        if (leftViewGroup != null) {
            leftViewGroup.removeView(tradeLeftView.getView());
            tradeLeftView = null;
            rightViewGroup.removeView(tradeRightView.getView());
            tradeRightView = null;
            bottomViewGroup.removeView(tradeBottomView.getView());
            tradeBottomView = null;
        }
        super.onDestroy();
    }

    protected abstract boolean isETF();

    protected void checkETFDisclaimer() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isOnChangeCoin) {
            //说明用户点击，排除EventBus调用后，再次请求
            isOnChangeCoin = false;
            return;
        }
        this.isVisibleToUser=isVisibleToUser;
        //控制切换至当前界面时的业务操作
        if (isVisibleToUser) {
            register();
            TradeDataHelper.getInstance().setETFCoin(isETF());
            //重新获得数据
            getMarketInfo();
            subscribeSocket();
        } else {
            unregister();
            unSubscribeSocket();
        }
        if (tradeBottomView != null) {
            tradeBottomView.setVisible(isVisibleToUser);
        } else {
            isEtfFirst = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    protected INetCallback<SingleResult<AssetResult>> marketInfoCallback = new INetCallback<SingleResult<AssetResult>>() {
        @Override
        public void onSuccess(SingleResult<AssetResult> result) throws Throwable {
            Logger.getInstance().debug(TAG, "asset result!");
            if (result == null || result.data == null) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            String json = GsonUtil.obj2Json(result.data, AssetResult.class);
            Logger.getInstance().debug(TAG, "json: " + json);
            Logger.getInstance().debug(TAG, "tradeId: " + result.data.tradeId + " id: " + TradeDataHelper.getInstance().getId(isETF()));
            //更新个人币种数据信息
            if (result.data.tradeId != TradeDataHelper.getInstance().getId(isETF())) {
                //
                return;
            }
            setAsset(result.data);
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
        }
    };
    private void subscribeSocket(){
        subscribeDepth();
        subscribeLatest();
        subscribe24H();
        subscribeOrder();
        getNetValue();
    }

    private void unSubscribeSocket(){
        unSubscribeDepth();
        unSubscribeLatest();
        unSubscribe24H();
        unSubscribeOrder();
        if(netValueCountDownTimer!=null){
            netValueCountDownTimer.cancel();
        }
    }
    private void subscribeDepth(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF())+"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        TypeToken type=new TypeToken<XDepthData>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_DEPTH+pair,type,(it)->{//盘口全量推送
            Logger.getInstance().debug(TAG,"socket返回 盘口:"+GsonUtil.obj2Json(it,XDepthData.class));
            try {
                tradeRightView.refreshDepth((XDepthData)it);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }
    private void subscribeLatest(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF()) +"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        TypeToken type=new TypeToken<List<XLatestDeal>>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_LATEST_DEAL+pair,type,(it)->{
            Logger.getInstance().debug(TAG,"socket返回 最新成交:"+GsonUtil.obj2Json(it,List.class));
            try {
                if(tradeRightView!=null){
                    List<XLatestDeal> list=(List<XLatestDeal>)it;
                    if(list!=null&&list.size()>0){
                        tradeRightView.refreshPrice(list.get(0));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }
    private void subscribe24H(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF()) +"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        TypeToken type=new TypeToken<X24HData>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_24H_DATA+pair,type,(it)->{
            Logger.getInstance().debug(TAG,"socket返回 24h数据:"+GsonUtil.obj2Json(it,X24HData.class));
            try {
                double ratio= MathHelper.mul(100.0,DoubleUtils.parseDouble(((X24HData)it).getChangeRate()));
                mTickerIsUp.set(ratio >= 0);
                if (ratio >= 0) {
                    cnyPriceTxt.setText("+"+ NorUtils.NumberFormat(2).format(ratio) + "%");
                } else {
                    cnyPriceTxt.setText(NorUtils.NumberFormat(2).format(ratio) + "%");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }
    //当前委单
    private void subscribeOrder(){
        TypeToken type=new TypeToken<OrderSocketBean>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_ORDER,type,(it)->{
            Logger.getInstance().debug(TAG,"socket返回 当前委托订单数据:"+GsonUtil.obj2Json(it,OrderSocketBean.class));
            try {
                viewModel.setOrderSocketData((OrderSocketBean)it);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }

    private void unSubscribeDepth(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF()) +"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_DEPTH+pair);
    }
    private void unSubscribeLatest(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF()) +"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_LATEST_DEAL+pair);
    }
    private void unSubscribe24H(){
        String pair = TradeDataHelper.getInstance().getCoinName(isETF()) +"-"+
                TradeDataHelper.getInstance().getCnyName(isETF());
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_24H_DATA+pair);
    }
    private void unSubscribeOrder(){
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_ORDER);
    }
    protected void getMarketInfo() {
        if (tradeLeftView == null) {
            return;
        }
        if (!UserInfoManager.isLogin()) {
            tradeLeftView.reset();
            return;
        }
        String url = UrlConstants.DOMAIN + UrlConstants.userMarketInfo;
        //取消前一个请求
        OKHttpHelper.getInstance().removeRequest(url);
        //TODO 待细化处理
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("type", "4");
        p.put("exchangeId", TradeDataHelper.getInstance().getId(isETF()) + "");
        p = EncryptUtils.encrypt(p);
        p.put("loginToken", UserInfoManager.getToken());
        Type type = new TypeToken<SingleResult<AssetResult>>() {
        }.getType();
        OKHttpHelper.getInstance().postForStringResult(url, p, marketInfoCallback, type);
        //
    }

    private void setAsset(AssetResult result) {
        //判断是否是当前选择的币种
        tradeLeftView.updateAsset(result);
    }

    private void updateOptional(int optional, boolean isShowToast) {
        TradeDataHelper.getInstance().updateOptionalCoin(optional);
        if (optional == 1) {
            starIV.setImageResource(R.drawable.ic_trade_collect_on);
            if (isShowToast) {
                SnackBarUtils.ShowBlue(getActivity(), getString(R.string.as74));
            }
        } else {
            starIV.setImageResource(R.drawable.ic_trade_collect);
            if (isShowToast) {
                SnackBarUtils.ShowBlue(getActivity(), getString(R.string.as75));
            }
        }
    }

    /**
     * 自选币种
     */
    private void optionalCoin() {
        if (UserInfoManager.isLogin()) {
            if (!TradeDataHelper.getInstance().isOptionalCoin()) {
                saveSelf(false);
            } else {
                delete();
            }
        } else {
            SPUtils.saveBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), !TradeDataHelper.getInstance().isOptionalCoin());
            updateOptional(TradeDataHelper.getInstance().isOptionalCoin() ? 0 : 1, true);
            EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//更新侧滑栏和行情的自选
            EventBus.getDefault().post(new BizEvent.Market.RefreshSelfLocalList());
        }
    }

    private void saveSelf(boolean isSilent) {
        if (!TradeHelper.checkLogin(getActivity())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("trademId", TradeDataHelper.getInstance().getId(isETF()) + "");
        params.put("tradeType", TransferAccount.SPOT.getValue());
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.UPDATE_SELF, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if (isSilent) {
                    return;
                }
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.qe52));
            }

            @Override
            public void requestSuccess(String result) {
                if (isSilent) {
                    MarketDataPresent.getSelfHttpList();
                    return;
                }
                try {
                    Log.d("用户添加自选", result);
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("200")) {
                        MarketDataPresent.getSelf().setSelect(TradeDataHelper.getInstance().getId(isETF()), 1);//老逻辑保留
                        updateOptional(1, true);
                        SPUtils.saveBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), TradeDataHelper.getInstance().isOptionalCoin());
                        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//更新侧滑栏和行情的自选
                        //更新自选列表
                        MarketDataPresent.getSelfHttpList();
                    } else {
                        SnackBarUtils.ShowRed(getActivity(), " " + message);
                    }
                } catch (JSONException e) {
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.qe54));
                    e.printStackTrace();
                }
            }
        });
    }
    //删除自选

    private void delete() {
        if (!TradeHelper.checkLogin(getActivity())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("trademId", TradeDataHelper.getInstance().getId(isETF()) + "");
        params.put("tradeType", TransferAccount.SPOT.getValue());
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.UPDATE_SELF, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.qe55));
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("200")) {
                        MarketDataPresent.getSelf().setSelect(TradeDataHelper.getInstance().getId(isETF()), 0);
                        //删除操作因为更新自选列表是异步操作，需要此处先删除
                        if (MarketDataPresent.listSelf != null && MarketDataPresent.listSelf.contains(TradeDataHelper.getInstance().getId(isETF()))) {
                            MarketDataPresent.listSelf.remove(Integer.valueOf(TradeDataHelper.getInstance().getId(isETF())));
                        }
                        updateOptional(0, true);
                        SPUtils.saveBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), TradeDataHelper.getInstance().isOptionalCoin());
                        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//更新侧滑栏和行情的自选
                        //更新自选列表
                        MarketDataPresent.getSelf().getSelfHttpList();
                    } else {
                        SnackBarUtils.ShowRed(getActivity(), " " + message);
                    }
                    Log.d("用户自选", result);
                } catch (JSONException e) {
                    SnackBarUtils.ShowBlue(getActivity(), getString(R.string.qe57));
                    e.printStackTrace();
                }
            }
        });
    }

    private void openPopupMenu() {
        if (popupWindow != null) {//说明PopupWindow已经创建
            popupWindow.showAsDropDown(moreIV, 0, Util.dp2px(getActivity(), 5));
//            backgroundAlpha(0.9f);
            return;
        }
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_trade_pop_menu, null, false);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        LinearLayout rechargeLayout = contentView.findViewById(R.id.ll_recharge);
        LinearLayout transferLayout = contentView.findViewById(R.id.ll_transfer);
        ImageView imageview1 = contentView.findViewById(R.id.imageview1);
        ImageView imageview2 = contentView.findViewById(R.id.imageview2);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.ll_recharge:
                            v.setBackgroundResource(R.drawable.otcmenu_bg3);
                            imageview1.setImageResource(R.mipmap.hu1);
                            break;
                        case R.id.ll_transfer:
                            v.setBackgroundColor(getResources().getColor(R.color.ff5e568a));
                            imageview2.setImageResource(R.mipmap.order1);
                            break;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    switch (v.getId()) {
                        case R.id.ll_recharge:
                            v.setBackgroundResource(R.drawable.otcmenu_bg1);
                            imageview1.setImageResource(R.mipmap.hz2);
                            break;
                        case R.id.ll_transfer:
                            v.setBackgroundColor(getResources().getColor(R.color.ff4d447f));
                            imageview2.setImageResource(R.mipmap.order2);
                            break;
                    }
                }
                return false;
            }
        };
        contentView.findViewById(R.id.rl_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
//            rechargeLayout.setOnTouchListener(onTouchListener);
//            transferLayout.setOnTouchListener(onTouchListener);
        rechargeLayout.setOnClickListener(this);
        transferLayout.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setClippingEnabled(false);
//            try {
//                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
//                mLayoutInScreen.setAccessible(true);
//                mLayoutInScreen.set(popupWindow, true);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        }
//        popupWindow.setBackgroundDrawable(getDrawable());
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        ColorDrawable dw = new ColorDrawable(-00000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(moreIV, 0, Util.dp2px(getActivity(), 5));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //popupwindow消失时使背景不透明
//                backgroundAlpha(1f);
            }
        });
//        backgroundAlpha(0.9f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
//        if (bgAlpha == 1) {
//            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
//        } else {
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
//        }
        getActivity().getWindow().setAttributes(lp);
    }

    private Drawable getDrawable() {
        ShapeDrawable bgdrawable = new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(getResources().getColor(R.color.color_F2010101));
        return bgdrawable;
    }

    private Drawable getDrawableLine() {

        return null;
    }

    private void getLimitedTimeTips(int id) {
        if (UserInfoManager.isLogin() && (DataUtils.isBCH(id + "") && warn_view != null)) {
            String cacheDate = SPUtils.getString(BaseApp.getSelf(), id + "", "");
            String currentDate = DateUtils.getCurrentDate(System.currentTimeMillis(), "yyyy/MM/dd");
            if (TextUtils.equals(cacheDate, currentDate)) {
                return;
            }
            String content = getString(R.string.dd12);
            if (warn_des != null) {
                warn_des.setText(content);
            }
            warn_view.setVisibility(View.VISIBLE);
        } else {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("id", id);//26
            paramsMap = EncryptUtils.encrypt(paramsMap);
            Type type = new TypeToken<LimitedTimeTipsResult>() {
            }.getType();
            HttpRequestManager.getInstance().post(UrlConstants.limitedTimeTips, paramsMap, limitedCallback, type);
        }
    }

    protected INetCallback<LimitedTimeTipsResult> limitedCallback = new INetCallback<LimitedTimeTipsResult>() {
        @Override
        public void onSuccess(LimitedTimeTipsResult ltt) throws Throwable {
            Logger.getInstance().debug(TAG, "trade result!");
            if (ltt == null) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (ltt.data == null) {
                Logger.getInstance().debug(TAG, "data is null.");
                return;
            }
            setTips(ltt.data);
//            setLimitedTimeTips(ltt.data.isLimitedTimeContent);
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
            setLimitedTimeTips("");
        }
    };

    private void setTips(LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips) {
        TradeDataHelper.getInstance().setLimitedTimeTips(limitedTimeTips);
        if (limitedTimeTips == null) {
            return;
        }
        setLimitedTimeTips(limitedTimeTips.isLimitedTimeContent);
        //
        if (warn_view == null) {
            return;
        }
        //
        if (limitedTimeTips.tradeRule != 1) {//只有是1时，才弹窗
            warn_view.setVisibility(View.GONE);
            return;
        }
        warn_view.setVisibility(View.VISIBLE);
        if (warn_des != null) {
            if (TextUtils.isEmpty(limitedTimeTips.tradeRuleDesc)) {
                warn_des.setText("--");
            } else {
                warn_des.setText(limitedTimeTips.tradeRuleDesc);
            }
        }
        //
    }

    private void setLimitedTimeTips(String content) {
        if (warnContentTxt == null) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            warnContentLayout.setVisibility(View.GONE);
        } else {
            warnContentLayout.setVisibility(View.VISIBLE);
        }
        warnContentTxt.setText(ValueUtils.getString(content));
    }
    private void getLatestData(){
        viewModel.getLatestData(TradeDataHelper.getInstance().getId(isETF())+"");
    }
    private void getNetValue(){
        if(!isETF()){
            return;
        }
        if(netValueCountDownTimer!=null){
            netValueCountDownTimer.cancel();
        }
        netValueCountDownTimer=TimerUtil.Companion.createCountDownTimer(10000000000l,AppConstants.TIMER.NET_VALUE,
                (time)->{
            //java.lang.NullPointerException: Attempt to invoke virtual method 'm.b.ca g.b.a.b.b.ia.d(java.lang.String)' on a null object
                    if (viewModel != null) {
                        viewModel.getNetValue(TradeDataHelper.getInstance().getId(isETF()) + "");
                    }
                    return null;
                },()->{
                    return null;
                });
        netValueCountDownTimer.start();
    }

    @Override
    public void applyTheme() {
        mBinding.invalidateAll();
        mBinding.tradePanel.invalidateAll();
        mBinding.tradePanel.llDirection.invalidateAll();

        tradeRightView.applyTheme();
        tradeLeftView.applyTheme();
        tradeBottomView.applyTheme();
    }
}
