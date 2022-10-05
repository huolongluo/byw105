package com.android.coinw.biz.trade;

import android.view.View;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huolongluo.byw.R;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.utils.DoubleUtils;

public class TradeFragment extends TradeAbsFragment {
    @Override
    protected boolean isETF() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        getMarketInfo();
    }

    @Override
    protected void initDagger() {

    }

    @Override
    protected void initViewsAndEvents(View rootView) {

    }

    @Override
    protected int getContentViewId() {
        return 0;
    }
}
