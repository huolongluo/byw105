package com.android.coinw.biz.trade;

import android.text.TextUtils;

import androidx.core.view.GravityCompat;

import com.android.coinw.biz.trade.helper.ETFHepler;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.model.Coin;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.bywx.utils.AppUtils;

public class TradeETFFragment extends TradeFragment {
    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        getMarketInfo();
    }

    @Override
    protected void openMenu() {
        //打开ETF的侧滑栏
        if (MainActivity.self.drawer_layout.isDrawerOpen(GravityCompat.START)) {
            MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            MainActivity.self.drawer_layout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected boolean isETF() {
        return true;
    }
}
