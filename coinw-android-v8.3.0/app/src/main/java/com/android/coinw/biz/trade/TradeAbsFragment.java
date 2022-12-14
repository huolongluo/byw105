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
    private TextView warnContentTxt;//????????????
    private LinearLayout etfTagLayout;
    protected int requestCode = 103;//??????k????????????

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
    private boolean isEtfFirst = false;//??????????????????????????????hint???true?????????etf?????????tradeBottomView???null????????????
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
        //????????????
        coinTxt.requestFocus();
        //
        etfTagLayout = rootView.findViewById(R.id.ll_etf_tag);
        etfTagLayout.setVisibility(isETF() ? View.VISIBLE : View.GONE);//ETF????????????
        starIV = rootView.findViewById(R.id.iv_star);
        starIV.setVisibility(isETF() ? View.GONE : View.VISIBLE);//ETF??????????????????????????????
        moreIV = rootView.findViewById(R.id.iv_more);
        //?????????-?????????
        leftViewGroup = rootView.findViewById(R.id.ll_content_left);
        tradeLeftView = new TradeLeftView(getActivity(), this);
        tradeLeftView.setETF(isETF());
        tradeLeftView.setFragmentManager(getChildFragmentManager());
        leftViewGroup.addView(tradeLeftView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //?????????-?????????
        rightViewGroup = rootView.findViewById(R.id.ll_content_right);
        tradeRightView = new TradeRightView(getActivity(), this);
        tradeRightView.setETF(isETF());
        tradeRightView.getDepthData(TradeDataHelper.getInstance().getId() + "");
        rightViewGroup.addView(tradeRightView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //????????? - ??????
        bottomViewGroup = rootView.findViewById(R.id.ll_bottom_view);
        tradeBottomView = new TradeBottomView(getActivity(), this);
        tradeBottomView.setETF(isETF());
        tradeBottomView.setViewPager(getChildFragmentManager());
        bottomViewGroup.addView(tradeBottomView.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //????????????
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
        //???????????????
        initView(rootView);
        initData();
        initObserver();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO ????????????????????????????????????????????????
        //????????????????????????
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
     * tab???????????????
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
                //??????????????????
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
        //????????????
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
     * ??????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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

    private void checkIsSelf() {//???????????????????????????????????????
        if (MarketDataPresent.listSelf == null) {
            return;
        }
        if (MarketDataPresent.listSelf.contains(TradeDataHelper.getInstance().getId())) {//???????????????????????????????????????
            updateOptional(1, false);//???????????????????????????????????????
            EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//?????????????????????
            return;
        }
        updateOptional(0, false);
    }

    private void initSelfLocal() {
        if (SPUtils.getBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), false)) {
            updateOptional(1, false);
            if (UserInfoManager.isLogin()) {//?????????????????????????????????????????????????????????????????????????????????????????????????????????
//                saveSelf(true);//?????????????????????????????????????????????
            }
        } else {
            updateOptional(0, false);
        }
        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//?????????????????????
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ll_menu) {//??????
            openMenu();
        } else if (vId == R.id.iv_star) {//????????????
            optionalCoin();
        } else if (vId == R.id.ll_etf_tag) {//ETF
            //ETF????????????
            //TODO ????????????
            DialogUtils.getInstance().showETFDoubtDialog(getActivity(), getString(R.string.str_etf_coin_desc));
        } else if (vId == R.id.ll_net_worth) {
            DialogUtils.getInstance().showETFDoubtDialog(getActivity(), getString(R.string.str_net_worth_desc));
        } else if (vId == R.id.iv_kline) {//K???
            if (TextUtils.isEmpty(TradeDataHelper.getInstance().getCoinName()) || TradeDataHelper.getInstance().getCoinName().equals("--")) {
                Logger.getInstance().debug(TAG, "??????k??????????????????????????????--");
                return;
            }
            int tradeType = isETF() ? Kline2Constants.TRADE_TYPE_ETF : Kline2Constants.TRADE_TYPE_COIN;
            Logger.getInstance().debug(TAG, "TradeDataHelper.getInstance().getCoinName():" + TradeDataHelper.getInstance().getCoinName());
            TradeHelper.gotoKLine(getActivity(), new KLineEntity(TradeDataHelper.getInstance().getId(isETF()), TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), tradeType));
        } else if (vId == R.id.iv_more) {//??????
            PopupWindowMenuHelper.getInstance().openPopupMenu(getActivity(), moreIV, this);
        } else if (vId == R.id.ll_recharge) {//??????
            if (!hasLoginForPopup()) {
                return;
            }
            TradeHelper.gotoRecharge(getActivity());
            PopupWindowMenuHelper.getInstance().dismiss();
        } else if (vId == R.id.ll_transfer) {//??????
            if (!hasLoginForPopup()) {
                return;
            }
            AccountTransferActivity.Companion.launch(getActivity(), null, null, 2, null, false, null);
            PopupWindowMenuHelper.getInstance().dismiss();
        } else if (vId == R.id.rl_latest) {
            initSelected(tv_latest, tv_commission, 0);
            tvAll.setVisibility(View.GONE);
//            shrinkIV.setVisibility(View.VISIBLE);
            //TODO ?????????
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
        } else if (vId == R.id.iv_warn_close) {//????????????????????????
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
     * ?????????????????????viewpager??????
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
            //TODO ????????????
            return;
        }
        TradeHelper.scrollTop(appBarLayout);
    }

    //k??????????????????????????????????????????
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
     * ?????? ???????????? ?????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        if (tradeLeftView != null) {
            tradeLeftView.reset();
        }
        //????????????
    }

    private void clickBuyOrSell(boolean isBuy) {
        if (isBuy) {
            changeTag();
            tradeLeftView.changeTag(false);//??????
            TradeHelper.scrollTop(appBarLayout);
        } else {
            changeTag();
            tradeLeftView.changeTag(true);//??????
            TradeHelper.scrollTop(appBarLayout);
        }
    }

    private void changeTag() {
        //??????????????????????????????????????????
        getMarketInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onChangeCoin(BizEvent.Trade.CoinEvent coinEvent) {
        if (coinEvent == null) {
            //TODO ????????????
            return;
        }
        //?????????????????????????????????????????????????????????true or false???????????????????????????
        if (coinEvent.isETF != isETF()) {
            return;
        }
        if (TextUtils.equals(TradeDataHelper.getInstance().getId(isETF()) + "", coinEvent.id)) {
            //TODO ????????????????????????????????????????????????????????????????????????
//            return;
        }
        if (warn_view != null) {
            warn_view.setVisibility(View.GONE);
        }
        //???????????????????????????
        TradeDataHelper.getInstance().setLimitedTimeTips(null);
        isOnChangeCoin = true;
        //????????????????????????
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
            //TODO ????????????
        } catch (Throwable t) {
            t.printStackTrace();
        }
        subscribeSocket();
        getLatestData();
        //?????????????????????
        warnContentLayout.setVisibility(View.GONE);
        //????????????????????????
        getMarketInfo();
        getLimitedTimeTips(TradeDataHelper.getInstance().getId(isETF()));
        initSelf();
    }

    private void resetView() {
        if (coinTxt == null || cnyTxt == null || tradeLeftView == null || tradeRightView == null || tradeBottomView == null) {
            return;
        }
        Coin coin = TradeDataHelper.getInstance().getCoin(isETF());
        //????????????????????????
        coinTxt.setText(coin.coinName);
        cnyTxt.setText("/" + coin.cnyName);
        tradeLeftView.onChange(coin.id + "", coin.coinName, coin.cnyName, coin.isSelf);
        tradeRightView.clearData(coin.id);
        //????????????
        tradeLeftView.updateNetValue("--");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (starIV == null) {
            return;
        }
        //??????????????????
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
            //???????????????????????????EventBus????????????????????????
            isOnChangeCoin = false;
            return;
        }
        this.isVisibleToUser=isVisibleToUser;
        //?????????????????????????????????????????????
        if (isVisibleToUser) {
            register();
            TradeDataHelper.getInstance().setETFCoin(isETF());
            //??????????????????
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
                //TODO ??????????????????
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            String json = GsonUtil.obj2Json(result.data, AssetResult.class);
            Logger.getInstance().debug(TAG, "json: " + json);
            Logger.getInstance().debug(TAG, "tradeId: " + result.data.tradeId + " id: " + TradeDataHelper.getInstance().getId(isETF()));
            //??????????????????????????????
            if (result.data.tradeId != TradeDataHelper.getInstance().getId(isETF())) {
                //
                return;
            }
            setAsset(result.data);
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO ??????????????????
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
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_DEPTH+pair,type,(it)->{//??????????????????
            Logger.getInstance().debug(TAG,"socket?????? ??????:"+GsonUtil.obj2Json(it,XDepthData.class));
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
            Logger.getInstance().debug(TAG,"socket?????? ????????????:"+GsonUtil.obj2Json(it,List.class));
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
            Logger.getInstance().debug(TAG,"socket?????? 24h??????:"+GsonUtil.obj2Json(it,X24HData.class));
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
    //????????????
    private void subscribeOrder(){
        TypeToken type=new TypeToken<OrderSocketBean>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_ORDER,type,(it)->{
            Logger.getInstance().debug(TAG,"socket?????? ????????????????????????:"+GsonUtil.obj2Json(it,OrderSocketBean.class));
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
        //?????????????????????
        OKHttpHelper.getInstance().removeRequest(url);
        //TODO ???????????????
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
        //????????????????????????????????????
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
     * ????????????
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
            EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//?????????????????????????????????
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
                    Log.d("??????????????????", result);
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("200")) {
                        MarketDataPresent.getSelf().setSelect(TradeDataHelper.getInstance().getId(isETF()), 1);//???????????????
                        updateOptional(1, true);
                        SPUtils.saveBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), TradeDataHelper.getInstance().isOptionalCoin());
                        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//?????????????????????????????????
                        //??????????????????
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
    //????????????

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
                        //???????????????????????????????????????????????????????????????????????????
                        if (MarketDataPresent.listSelf != null && MarketDataPresent.listSelf.contains(TradeDataHelper.getInstance().getId(isETF()))) {
                            MarketDataPresent.listSelf.remove(Integer.valueOf(TradeDataHelper.getInstance().getId(isETF())));
                        }
                        updateOptional(0, true);
                        SPUtils.saveBoolean(getContext(), KLine2Util.getSelfSpKey(TradeDataHelper.getInstance().getCoinName(), TradeDataHelper.getInstance().getCnyName(), Kline2Constants.TRADE_TYPE_COIN), TradeDataHelper.getInstance().isOptionalCoin());
                        EventBus.getDefault().post(new BizEvent.Trade.UpdateSelf());//?????????????????????????????????
                        //??????????????????
                        MarketDataPresent.getSelf().getSelfHttpList();
                    } else {
                        SnackBarUtils.ShowRed(getActivity(), " " + message);
                    }
                    Log.d("????????????", result);
                } catch (JSONException e) {
                    SnackBarUtils.ShowBlue(getActivity(), getString(R.string.qe57));
                    e.printStackTrace();
                }
            }
        });
    }

    private void openPopupMenu() {
        if (popupWindow != null) {//??????PopupWindow????????????
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
                //popupwindow???????????????????????????
//                backgroundAlpha(1f);
            }
        });
//        backgroundAlpha(0.9f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
//        if (bgAlpha == 1) {
//            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//????????????Flag??????,???????????????????????????????????????????????????bug
//        } else {
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//?????????????????????????????????????????????????????????????????????bug
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
                //TODO ??????????????????
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
            //TODO ??????????????????
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
        if (limitedTimeTips.tradeRule != 1) {//?????????1???????????????
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
