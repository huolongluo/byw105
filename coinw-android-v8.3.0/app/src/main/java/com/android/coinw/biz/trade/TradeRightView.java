package com.android.coinw.biz.trade;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.api.kx.model.XDepthData;
import com.android.coinw.api.kx.model.XLatestDeal;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.DepthHelper;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.legend.model.CommonResult;
import com.legend.modular_contract_sdk.component.market_listener.Depth;
import com.legend.modular_contract_sdk.utils.StringUtilKt;
import com.legend.modular_contract_sdk.widget.depthview.DepthFutureViewDelegate;
import com.legend.modular_contract_sdk.widget.depthview.DepthFutureViewInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.databinding.TradeRightViewBinding;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.trade.adapter.BuyAdapterNew;
import huolongluo.byw.reform.trade.adapter.SellAdapterNew;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;

/**
 * 交易 右侧买入 卖出 相关View
 */
public class TradeRightView extends BaseView implements View.OnClickListener {

    @BindView(R.id.shendu_tv)
    TextView shendu_tv;
    @BindView(R.id.dangwei_bn)
    LinearLayout dangwei_bn;
    @BindView(R.id.dangwei_tv)
    TextView dangwei_tv;
    @BindView(R.id.iv_flag_sd)
    ImageView flagDepthIV;
    @BindView(R.id.iv_flag_dw)
    ImageView flagGearIV;
    @BindView(R.id.ll_depth_gear)
    View depthGearView;
    @BindView(R.id.ll_loading)
    View loadingView;
    protected PopupWindow shenDPopupWindow;
    protected String mFirstBuy;
    private PopupWindow popupWindow;
    private ObjectAnimator objectAnimator;
    private boolean isETF;
    private XLatestDeal latestDeal;//记录每次的最新成交价对应的对象，用于计价方式切换的即时刷新

    private String mCurrentCoinId;
    private TradeRightViewBinding mBinding;
    private DepthFutureViewDelegate mDepthDelegate;

    public TradeRightView(Context context, Fragment fragment) {
        super(context, fragment);
    }

    public void setETF(boolean isETF) {
        this.isETF = isETF;
    }

    @Override
    public View setContentView(int layoutResID) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.trade_right_view, null, false);
        return mBinding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trade_right_view;
    }

    @Override
    protected void initView() {

        mDepthDelegate = new DepthFutureViewDelegate(mBinding.viewDepth);
        mDepthDelegate.setType(DepthFutureViewInterface.TYPE_SPOT);
        mDepthDelegate.init();
        mDepthDelegate.setMaxSize(5);

        String cnyName = CurrencyPairUtil.getCnyName(TradeDataHelper.getInstance().getId(isETF));
        if (TextUtils.isEmpty(cnyName)){
            cnyName = AppUtils.getDefaultCnyName();
        }
        mDepthDelegate.setUnit(cnyName, null);
        mDepthDelegate.setOnItemClickListener((isBuy, depth) -> {
            sendClickInfo(!isBuy, String.valueOf(depth.get(0)), String.valueOf(depth.get(1)));
        });
    }

    private void sendClickInfo(boolean isSell, String onePrice, String totalPrice) {
        BizEvent.Trade.ClickHandicap clickHandicap = new BizEvent.Trade.ClickHandicap();
        clickHandicap.isSell = isSell;
        clickHandicap.price = onePrice;
        clickHandicap.count = totalPrice;
        clickHandicap.isETF = isETF;
        EventBus.getDefault().post(clickHandicap);
    }

    @Override
    protected void initData() {
        initObserver();
    }

    public void getDepthData(String coinId) {
        mTradeViewModel.getDepthData(coinId);
    }

    private void initObserver() {
        mTradeViewModel.getDepthData().observe(mFragment, new Observer<CommonResult<XDepthData>>() {
            @Override
            public void onChanged(CommonResult<XDepthData> xDepthDataCommonResult) {
                Logger.getInstance().debug(TAG, "viewModel.getDepthData():" + GsonUtil.obj2Json(xDepthDataCommonResult, CommonResult.class));

                if (xDepthDataCommonResult.isSuccess()) {
                    if (xDepthDataCommonResult.getData() == null) {
                        Logger.getInstance().debug(TAG, "result is null.");
                        return;
                    }
                    if (TextUtils.equals(xDepthDataCommonResult.getCode(), "200")) {
                        initDepthPrecision();

                        refreshDepth(xDepthDataCommonResult.getData());
                        if (loadingView.getVisibility() == View.VISIBLE) {
                            loadingView.setVisibility(View.GONE);
                            depthGearView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void initDepthPrecision() {
        String[] arr = DepthHelper.getInstance().getDepthArr(TradeDataHelper.getInstance().getId() + "");
        if (arr == null || arr.length == 0) {
            TradeDataHelper.getInstance().updateDepth("-1", isETF);//盘口数据获取后更新默认深度
        } else {
            TradeDataHelper.getInstance().updateDepth(arr[0], isETF);//取接口返回币种深度列表第一个为默认精度
        }
    }

    protected void refreshDepth(XDepthData data) {

        ArrayList<TradeOrder.OrderInfo> list = TradeUtils.getDepthList(data);

        if (list != null) {
            refreshDepthView(list);
        }

    }

    private void refreshDepthView(List<TradeOrder.OrderInfo> depthList) {
        if (depthList != null) {
            int depthMerge = 0;
            if (isETF) {
                depthMerge = PricingMethodUtil.getPrecision(TradeDataHelper.getInstance().getDepth(isETF).equals("-1") ? AppConstants.BIZ.DEFAULT_ETF_DEPTH : TradeDataHelper.getInstance().getDepth(isETF));
                mDepthDelegate.setLotSize(1, CurrencyPairUtil.getQuantityPreciousById(TradeDataHelper.getInstance().getId(isETF)), depthMerge);
            } else {
                depthMerge = PricingMethodUtil.getPrecision(TradeDataHelper.getInstance().getDepth(isETF).equals("-1") ? AppConstants.BIZ.DEFAULT_DEPTH : TradeDataHelper.getInstance().getDepth(isETF));
                mDepthDelegate.setLotSize(1, CurrencyPairUtil.getQuantityPreciousById(TradeDataHelper.getInstance().getId(isETF)), depthMerge);
            }

            TradeUtils.getInstance().setDepthList(depthList);
            List<TradeOrder.OrderInfo> mergedList = TradeUtils.getInstance().mergeBuyDepthList(depthList, depthMerge);
            List<Depth.PendingOrder> sellDepthList = new ArrayList<>(mergedList.size());
            List<Depth.PendingOrder> buyDepthList = new ArrayList<>(mergedList.size());
            for (TradeOrder.OrderInfo orderInfo : mergedList) {
                sellDepthList.add(new Depth.PendingOrder(orderInfo.sellPrice == null?"0":orderInfo.sellPrice, orderInfo.sellAmount == null?"0":orderInfo.sellAmount));
                buyDepthList.add(new Depth.PendingOrder(orderInfo.buyPrice == null?"0":orderInfo.buyPrice, orderInfo.buyAmount == null?"0":orderInfo.buyAmount));
            }
            mDepthDelegate.setData(sellDepthList, buyDepthList);
            TradeUtils.getInstance().setDepthList(depthList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        refreshLatestDealPrice();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCoin(BizEvent.Trade.CoinEvent coinEvent) {
        if (coinEvent == null) {
            return;
        }
        if (coinEvent.isETF != isETF) {
            return;
        }
        mCurrentCoinId = coinEvent.id;
        mDepthDelegate.setUnit(coinEvent.cnyName, null);

        //缓存id
        getDepthData(coinEvent.id);

        loadingView.setVisibility(View.VISIBLE);
        depthGearView.setVisibility(View.GONE);
    }

    public void clearData(int id) {
        mDepthDelegate.reset();
        loadingView.setVisibility(View.VISIBLE);
        depthGearView.setVisibility(View.GONE);
    }

    private boolean isNeedUpdate(Object tagObj, int id) {
        if (tagObj == null) {//为空说明第一次更新
            return true;
        }
        if (tagObj instanceof Integer) {
            if (Integer.valueOf(tagObj.toString()) == id) {
                return true;
            }
        }
        return false;
    }

    void showDWPop() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.trade_dangwei_pup_item, null, false);
            popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            LinearLayout tv_1 = contentView.findViewById(R.id.tv_1);
            LinearLayout tv_2 = contentView.findViewById(R.id.tv_2);
            LinearLayout tv_3 = contentView.findViewById(R.id.tv_3);
            LinearLayout dangwei_cancle_bn = contentView.findViewById(R.id.dangwei_cancle_bn);
            tv_1.setOnClickListener(this);
            tv_2.setOnClickListener(this);
            tv_3.setOnClickListener(this);
            dangwei_cancle_bn.setOnClickListener(this);
            popupWindow.setOnDismissListener(() -> {
                flagGearIV.setImageResource(R.mipmap.ic_arrow_up_small);
            });
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            int[] location = new int[2];
            dangwei_bn.getLocationOnScreen(location);
            popupWindow.showAtLocation(dangwei_bn, Gravity.BOTTOM, 0, 0);
        } else {
            popupWindow.showAtLocation(dangwei_bn, Gravity.BOTTOM, 0, 0);
        }
        flagGearIV.setImageResource(R.mipmap.ic_arrow_down_small);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_4_0://深度设置
                TradeDataHelper.getInstance().updateDepth(view.getTag().toString(), isETF);
                if (TextUtils.equals("-1", TradeDataHelper.getInstance().getDepth(isETF)) || TextUtils.isEmpty(TradeDataHelper.getInstance().getDepth(isETF))) {
                    shendu_tv.setText(view.getContext().getResources().getString(R.string.default_sd));
                } else {
                    shendu_tv.setText(TradeDataHelper.getInstance().getDepth(isETF));
                }
                SPUtils.saveString(mContext, SPUtils.Trade_shendu, TradeDataHelper.getInstance().getDepth(isETF));
                AppHelper.dismissPopupWindow(shenDPopupWindow);
                break;
            case R.id.shendu_cancle_bn:
                shenDPopupWindow.dismiss();
                break;
            case R.id.dangwei_cancle_bn:
                popupWindow.dismiss();
                break;
            case R.id.tv_1:
            case R.id.tv_2:
            case R.id.tv_3://档位设置
                TradeDataHelper.getInstance().updateGear(view.getTag().toString(), isETF);
                dangwei_tv.setText(String.format("%d%s", TradeDataHelper.getInstance().getGear(), mContext.getString(R.string.qe30)));
                //为了维护历史数据，暂不修改
                SPUtils.saveString(mContext, SPUtils.Trade_dangwei, TradeDataHelper.getInstance().getGear() + "");
                setDagnwei(TradeDataHelper.getInstance().getGear());
                break;
        }
    }

    @OnClick({R.id.shendu_bn, R.id.dangwei_bn})
    public void onClickx(View v) {
        switch (v.getId()) {
            case R.id.shendu_bn:
                DepthHelper.getInstance().showDepthPopwindow(TradeDataHelper.getInstance().getId(isETF) + "", mContext, new DepthHelper.IDepthCallback() {
                    @Override
                    public void onOpen() {
                        if (flagDepthIV != null) {
                            flagDepthIV.setImageResource(R.mipmap.ic_arrow_down_small);
                        }
                    }

                    @Override
                    public void onClose() {
                        if (flagDepthIV != null) {
                            flagDepthIV.setImageResource(R.mipmap.ic_arrow_up_small);
                        }
                    }

                    @Override
                    public void onItemClick(View v) {
                        try {
                            settingDepth(v);
                        } catch (Throwable t) {
                            Logger.getInstance().error(t);
                        }
                    }
                });
                break;
            case R.id.dangwei_bn:
                showDWPop();
                break;
        }
    }

    public void updateDepth(String depth) {
        if (shendu_tv == null || mContext == null) {
            return;
        }
        try {
            if (TextUtils.equals("-1", depth) || TextUtils.isEmpty(depth)) {
                shendu_tv.setText(mContext.getResources().getString(R.string.default_sd));
            } else {
                shendu_tv.setText(TradeDataHelper.getInstance().getDepth(isETF));
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void settingDepth(View view) {
        if (TextUtils.equals("-1", view.getTag().toString()) ||
                TextUtils.isEmpty(TradeDataHelper.getInstance().getDepth(isETF))) {
            shendu_tv.setText(view.getContext().getResources().getString(R.string.default_sd));
            String[] arr = DepthHelper.getInstance().getDepthArr(TradeDataHelper.getInstance().getId() + "");
            if (arr == null || arr.length == 0) {
                TradeDataHelper.getInstance().updateDepth("-1", isETF);
            } else {
                TradeDataHelper.getInstance().updateDepth(arr[0], isETF);
            }
        } else {
            TradeDataHelper.getInstance().updateDepth(view.getTag().toString(), isETF);
            shendu_tv.setText(TradeDataHelper.getInstance().getDepth(isETF));
        }
        AppHelper.dismissPopupWindow(shenDPopupWindow);

        refreshDepthView(TradeUtils.getInstance().getDepthList());

    }


    /**
     * 设置当前价格的涨跌图标和颜色
     */
    public void refreshPrice(XLatestDeal deal) {

        double change;
        if (latestDeal == null) {
            change = 0;
        } else {
            change = 1 - StringUtilKt.getDoubleValue(latestDeal.getPrice()) / StringUtilKt.getDoubleValue(deal.getPrice());
            Logger.getInstance().error(change + "   refreshPrice " + latestDeal.getPrice() + "  " + deal.getPrice());
        }

        mDepthDelegate.setLast(deal.getPrice(), CurrencyPairUtil.getPricePreciousById(TradeDataHelper.getInstance().getId(isETF)), String.valueOf(change));

        latestDeal = deal;
        refreshLatestDealPrice();
    }

    private void refreshLatestDealPrice() {
        String convertCny = PricingMethodUtil.getPricingUnit() +
                PricingMethodUtil.getResultByExchangeRate(latestDeal.getPrice(), TradeDataHelper.getInstance().getCnyName(isETF));

        mDepthDelegate.setLastIndex("≈" + convertCny, -1);
    }

    private void setDagnwei(int length) {
        AppHelper.dismissPopupWindow(popupWindow);
        //更新数据
        EventBus.getDefault().post(new BizEvent.Trade.Gear(isETF));
    }

    public void applyTheme() {
        mBinding.invalidateAll();
        mDepthDelegate.resetPaint();
    }
}
